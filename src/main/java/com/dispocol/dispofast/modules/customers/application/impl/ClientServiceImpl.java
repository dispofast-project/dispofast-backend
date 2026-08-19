package com.dispocol.dispofast.modules.customers.application.impl;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateClientRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateIndividualRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateOrganizationRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.PriceHistoryEntryDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.PriceHistoryResponseDTO;
import com.dispocol.dispofast.modules.customers.api.mappers.ClientMapper;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.ClientType;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.LegalDocument;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientTypeRepository;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.pricelist.application.interfaces.PriceListService;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAsset;
import com.dispocol.dispofast.shared.MediaAsset.domain.MediaAssetType;
import com.dispocol.dispofast.shared.MediaAsset.persistence.MediaAssetRepository;
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import com.dispocol.dispofast.shared.S3.infra.UploadFileFailedException;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final CityRepository cityRepository;
  private final UserRepository userRepository;
  private final ClientTypeRepository clientTypeRepository;
  private final PriceListRepository priceListRepository;
  private final PriceListService priceListService;
  private final MediaAssetRepository mediaAssetRepository;
  private final S3Service s3Service;
  private final SalesOrderItemRepository salesOrderItemRepository;

  private static final String LEGAL_DOCS_BUCKET = "dispofast-legal-documents";

  @Override
  @Transactional(readOnly = true)
  public Page<ClientPreviewDTO> getAllClients(
      Pageable pageable, String text, String key, Boolean isActive, String city) {
    Specification<Client> spec =
        (root, query, cb) -> {
          boolean isCountQuery =
              Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType());
          if (!isCountQuery) {
            root.fetch("defaultAdvisor", JoinType.INNER);
            root.fetch("city", JoinType.INNER);
          }
          return cb.conjunction();
        };

    if (text != null && !text.isBlank()) {
      spec = spec.and(buildSearchSpec(text.trim().toLowerCase(), key));
    }

    if (isActive != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
    }

    if (city != null && !city.isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("city").get("code"), city.trim()));
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    if (!isAdmin) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.equal(root.get("defaultAdvisor").get("email"), auth.getName()));
    }

    Page<Client> clientPage = clientRepository.findAll(spec, pageable);
    return clientPage.map(clientMapper::toPreviewDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public ClientResponseDTO getClientById(UUID id) {
    Client client =
        clientRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("No se encontró el cliente solicitado."));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    if (!isAdmin) {
      boolean isOwner =
          client.getDefaultAdvisor() != null
              && auth.getName().equalsIgnoreCase(client.getDefaultAdvisor().getEmail());
      if (!isOwner) {
        throw new ResourceNotFoundException("No se encontró el cliente solicitado.");
      }
    }

    return clientMapper.toResponseDTO(client);
  }

  @Override
  @Transactional
  public ClientResponseDTO createClient(
      CreateClientRequestDTO request, List<MultipartFile> documents, AppUser createdByUser) {
    if (clientRepository.existsByIdentificationNumber(request.getIdentificationNumber())) {
      throw new IllegalArgumentException("Ya existe un cliente con este número de identificación.");
    }
    if (clientRepository.existsByEmailIgnoreCase(request.getEmail())) {
      throw new IllegalArgumentException("Ya existe un cliente con este correo electrónico.");
    }

    City city =
        cityRepository
            .findById(request.getCityCode())
            .orElseThrow(
                () -> new ResourceNotFoundException("La ciudad seleccionada no fue encontrada."));

    AppUser advisor;
    if (request.getDefaultAdvisorId() != null) {
      advisor =
          userRepository
              .findById(request.getDefaultAdvisorId())
              .orElseThrow(
                  () -> new ResourceNotFoundException("El asesor seleccionado no fue encontrado."));
    } else {
      boolean isAdmin =
          createdByUser.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
      if (isAdmin) {
        throw new IllegalArgumentException(
            "Los administradores deben especificar un asesor al crear un cliente.");
      }
      advisor = createdByUser;
    }

    ClientType clientType =
        clientTypeRepository
            .findById(request.getClientTypeId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "El tipo de cliente seleccionado no fue encontrado."));

    PriceList priceList =
        priceListRepository
            .findById(request.getPriceListId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "La lista de precios seleccionada no fue encontrada."));

    Client client;
    if (request instanceof CreateIndividualRequestDTO individualRequest) {
      client = clientMapper.toIndividual(individualRequest);
    } else if (request instanceof CreateOrganizationRequestDTO organizationRequest) {
      client = clientMapper.toOrganization(organizationRequest);
    } else {
      throw new IllegalArgumentException("Tipo de cliente no válido.");
    }

    client.setCity(city);
    client.setDefaultAdvisor(advisor);
    client.setClientType(clientType);
    client.setPriceList(priceList);

    Client savedClient = clientRepository.save(client);

    if (documents != null && !documents.isEmpty()) {
      for (MultipartFile file : documents) {
        String storagePath =
            "clients/"
                + savedClient.getId()
                + "/"
                + UUID.randomUUID()
                + "_"
                + file.getOriginalFilename();

        try {
          s3Service.uploadFile(
              LEGAL_DOCS_BUCKET,
              storagePath,
              file.getInputStream(),
              file.getContentType(),
              file.getSize());
        } catch (IOException e) {
          throw new UploadFileFailedException(
              "Subir el documento: " + file.getName() + " ha fallado.");
        }

        MediaAsset asset = new MediaAsset();
        asset.setFilename(file.getOriginalFilename());
        asset.setStoragePath(storagePath);
        asset.setMimeType(file.getContentType());
        asset.setFileSize(file.getSize());
        asset.setType(MediaAssetType.LEGAL_DOC);
        mediaAssetRepository.save(asset);

        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setFileAttachment(asset);
        savedClient.addLegalDocument(legalDocument);
      }
      clientRepository.save(savedClient);
    }

    return clientMapper.toResponseDTO(savedClient);
  }

  @Override
  @Transactional
  public ClientResponseDTO updateClient(
      UUID id, CreateClientRequestDTO request, List<MultipartFile> documents) {
    Client client =
        clientRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("No se encontró el cliente solicitado."));

    if (!client.getIdentificationNumber().equals(request.getIdentificationNumber())
        && clientRepository.existsByIdentificationNumberAndIdNot(
            request.getIdentificationNumber(), id)) {
      throw new IllegalArgumentException("Ya existe un cliente con este número de identificación.");
    }
    if (!client.getEmail().equalsIgnoreCase(request.getEmail())
        && clientRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) {
      throw new IllegalArgumentException("Ya existe un cliente con este correo electrónico.");
    }

    City city =
        cityRepository
            .findById(request.getCityCode())
            .orElseThrow(
                () -> new ResourceNotFoundException("La ciudad seleccionada no fue encontrada."));

    AppUser advisor =
        userRepository
            .findById(request.getDefaultAdvisorId())
            .orElseThrow(
                () -> new ResourceNotFoundException("El asesor seleccionado no fue encontrado."));

    ClientType clientType =
        clientTypeRepository
            .findById(request.getClientTypeId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "El tipo de cliente seleccionado no fue encontrado."));

    PriceList priceList =
        priceListRepository
            .findById(request.getPriceListId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "La lista de precios seleccionada no fue encontrada."));

    if (client instanceof Individual individual
        && request instanceof CreateIndividualRequestDTO individualRequest) {
      clientMapper.updateIndividual(individualRequest, individual);
    } else if (client instanceof Organization organization
        && request instanceof CreateOrganizationRequestDTO organizationRequest) {
      clientMapper.updateOrganization(organizationRequest, organization);
    } else {
      throw new IllegalArgumentException("No es posible cambiar el tipo de cliente.");
    }

    client.setCity(city);
    client.setDefaultAdvisor(advisor);
    client.setClientType(clientType);
    client.setPriceList(priceList);

    if (documents != null && !documents.isEmpty()) {
      for (MultipartFile file : documents) {
        String storagePath =
            "clients/"
                + client.getId()
                + "/"
                + UUID.randomUUID()
                + "_"
                + file.getOriginalFilename();

        try {
          s3Service.uploadFile(
              LEGAL_DOCS_BUCKET,
              storagePath,
              file.getInputStream(),
              file.getContentType(),
              file.getSize());
        } catch (IOException e) {
          throw new UploadFileFailedException(
              "Subir el documento: " + file.getName() + " ha fallado.");
        }

        MediaAsset asset = new MediaAsset();
        asset.setFilename(file.getOriginalFilename());
        asset.setStoragePath(storagePath);
        asset.setMimeType(file.getContentType());
        asset.setFileSize(file.getSize());
        asset.setType(MediaAssetType.LEGAL_DOC);
        mediaAssetRepository.save(asset);

        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setFileAttachment(asset);
        client.addLegalDocument(legalDocument);
      }
    }

    Client savedClient = clientRepository.save(client);
    return clientMapper.toResponseDTO(savedClient);
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] getLegalDocument(UUID clientId, UUID documentId) {
    Client client =
        clientRepository
            .findById(clientId)
            .orElseThrow(
                () -> new ResourceNotFoundException("No se encontró el cliente solicitado."));

    LegalDocument doc =
        client.getLegalDocuments().stream()
            .filter(d -> d.getId().equals(documentId))
            .findFirst()
            .orElseThrow(
                () -> new ResourceNotFoundException("El documento solicitado no fue encontrado."));

    return s3Service.downloadFile(LEGAL_DOCS_BUCKET, doc.getFileAttachment().getStoragePath());
  }

  @Override
  @Transactional(readOnly = true)
  public PriceHistoryResponseDTO getPriceHistory(UUID clientId, UUID productId) {
    Client client =
        clientRepository
            .findById(clientId)
            .orElseThrow(
                () -> new ResourceNotFoundException("No se encontró el cliente solicitado."));

    OffsetDateTime periodStart =
        OffsetDateTime.now().withDayOfMonth(1).minusMonths(1).truncatedTo(ChronoUnit.DAYS);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    String advisorEmail = isAdmin ? null : auth.getName();

    List<PriceHistoryEntryDTO> entries =
        salesOrderItemRepository
            .findByClientIdAndProductId(clientId, productId, periodStart, advisorEmail)
            .stream()
            .map(
                item ->
                    PriceHistoryEntryDTO.builder()
                        .source("ORDER")
                        .documentNumber(item.getOrder().getOrderNumber())
                        .date(item.getOrder().getOrderDate())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
            .sorted(Comparator.comparing(PriceHistoryEntryDTO::getDate).reversed())
            .toList();

    BigDecimal currentListPrice =
        priceListService.resolveUnitPrice(client.getPriceList().getId(), productId).orElse(null);

    return PriceHistoryResponseDTO.builder()
        .entries(entries)
        .currentListPrice(currentListPrice)
        .build();
  }

  private Specification<Client> buildSearchSpec(String text, String key) {
    return (root, query, cb) -> {
      String pattern = "%" + text + "%";

      if (key != null) {
        return switch (key) {
          case "name" -> buildNamePredicate(root, cb, pattern);
          case "identification" -> cb.like(cb.lower(root.get("identificationNumber")), pattern);
          case "advisor" -> cb.like(cb.lower(root.get("defaultAdvisor").get("fullName")), pattern);
          default -> buildNamePredicate(root, cb, pattern);
        };
      }

      // No key: search across name, identification, and email
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(buildNamePredicate(root, cb, pattern));
      predicates.add(cb.like(cb.lower(root.get("identificationNumber")), pattern));
      predicates.add(cb.like(cb.lower(root.get("email")), pattern));
      return cb.or(predicates.toArray(new Predicate[0]));
    };
  }

  private Predicate buildNamePredicate(Root<Client> root, CriteriaBuilder cb, String pattern) {
    // Search in Individual (firstName + lastName) or Organization (legalName)
    Predicate individualName =
        cb.and(
            cb.equal(root.type(), Individual.class),
            cb.or(
                cb.like(cb.lower(cb.treat(root, Individual.class).get("firstName")), pattern),
                cb.like(cb.lower(cb.treat(root, Individual.class).get("lastName")), pattern)));

    Predicate orgName =
        cb.and(
            cb.equal(root.type(), Organization.class),
            cb.like(cb.lower(cb.treat(root, Organization.class).get("legalName")), pattern));

    return cb.or(individualName, orgName);
  }
}

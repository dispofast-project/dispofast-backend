package com.dispocol.dispofast.modules.customers.application.impl;

import com.dispocol.dispofast.modules.customers.api.dtos.ClientPreviewDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.ClientResponseDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateClientRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateIndividualRequestDTO;
import com.dispocol.dispofast.modules.customers.api.dtos.CreateOrganizationRequestDTO;
import com.dispocol.dispofast.modules.customers.api.mappers.ClientMapper;
import com.dispocol.dispofast.modules.customers.application.interfaces.ClientService;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.ClientType;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientTypeRepository;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.pricelist.domain.PriceList;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

  private final ClientRepository clientRepository;
  private final ClientMapper clientMapper;
  private final CityRepository cityRepository;
  private final UserRepository userRepository;
  private final ClientTypeRepository clientTypeRepository;
  private final PriceListRepository priceListRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<ClientPreviewDTO> getAllClients(
      Pageable pageable, String text, String key, Boolean isActive, String city) {
    Specification<Client> spec = Specification.where(null);

    if (text != null && !text.isBlank()) {
      spec = spec.and(buildSearchSpec(text.trim().toLowerCase(), key));
    }

    if (isActive != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("isActive"), isActive));
    }

    if (city != null && !city.isBlank()) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("city").get("code"), city.trim()));
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
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    return clientMapper.toResponseDTO(client);
  }

  @Override
  @Transactional
  public ClientResponseDTO createClient(CreateClientRequestDTO request) {
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
                () ->
                    new ResourceNotFoundException(
                        "City not found with code: " + request.getCityCode()));

    AppUser advisor =
        userRepository
            .findById(request.getDefaultAdvisorId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Advisor user not found with ID: " + request.getDefaultAdvisorId()));

    ClientType clientType =
        clientTypeRepository
            .findById(request.getClientTypeId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Client type not found with ID: " + request.getClientTypeId()));

    PriceList priceList =
        priceListRepository
            .findById(request.getPriceListId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Price list not found with ID: " + request.getPriceListId()));

    Client client;
    if (request instanceof CreateIndividualRequestDTO individualRequest) {
      client = clientMapper.toIndividual(individualRequest);
    } else if (request instanceof CreateOrganizationRequestDTO organizationRequest) {
      client = clientMapper.toOrganization(organizationRequest);
    } else {
      throw new IllegalArgumentException("Invalid client request type");
    }

    client.setCity(city);
    client.setDefaultAdvisor(advisor);
    client.setClientType(clientType);
    client.setPriceList(priceList);

    Client savedClient = clientRepository.save(client);
    return clientMapper.toResponseDTO(savedClient);
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

package com.dispocol.dispofast.modules.cartera.application.impl;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.mappers.ArEntryMapper;
import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import com.dispocol.dispofast.modules.cartera.domain.ArEntrySource;
import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArEntryServiceImpl implements ArEntryService {

  private static final int DEFAULT_PAYMENT_TERM_DAYS = 30;

  private final ArEntryRepository arEntryRepository;
  private final ArEntryMapper arEntryMapper;
  private final ClientRepository clientRepository;
  private final UserRepository userRepository;
  private final CityRepository cityRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<ArEntryResponseDTO> getArEntries(Pageable pageable, ArEntryFilterDTO filter) {
    Specification<ArEntry> spec = buildSpecification(filter);
    return arEntryRepository.findAll(spec, pageable).map(arEntryMapper::toResponseDTO);
  }

  @Override
  @Transactional
  public ArEntryResponseDTO createManualEntry(CreateManualArEntryRequestDTO request) {
    ArEntry entry = new ArEntry();
    entry.setClient(clientRepository.getReferenceById(request.getClientId()));

    if (request.getAsesorUserId() != null) {
      entry.setAsesor(userRepository.getReferenceById(request.getAsesorUserId()));
    }
    if (request.getCityId() != null) {
      entry.setCity(cityRepository.getReferenceById(request.getCityId()));
    }

    entry.setValue(request.getValue());
    entry.setInvoiceNumber(request.getInvoiceNumber());
    entry.setInvoiceDate(request.getInvoiceDate());
    entry.setPaymentTermDays(request.getPaymentTermDays());
    entry.setExpirationDate(request.getInvoiceDate().plusDays(request.getPaymentTermDays()));
    entry.setSource(ArEntrySource.MANUAL);

    return arEntryMapper.toResponseDTO(arEntryRepository.save(entry));
  }

  @Override
  @Transactional
  public ArEntryResponseDTO createFromOrder(SalesOrder order) {
    OffsetDateTime invoiceDate = OffsetDateTime.now();
    int termDays = DEFAULT_PAYMENT_TERM_DAYS;

    ArEntry entry = new ArEntry();
    entry.setClient(order.getClient());
    entry.setAsesor(order.getAsesor());
    entry.setOrder(order);
    entry.setCity(order.getShipmentCity());
    entry.setValue(
        order.getTotalValue() != null ? order.getTotalValue() : java.math.BigDecimal.ZERO);
    entry.setInvoiceNumber(order.getInvoiceNumber());
    entry.setInvoiceDate(invoiceDate);
    entry.setPaymentTermDays(termDays);
    entry.setExpirationDate(invoiceDate.plusDays(termDays));
    entry.setSource(ArEntrySource.ORDER);

    return arEntryMapper.toResponseDTO(arEntryRepository.save(entry));
  }

  private Specification<ArEntry> buildSpecification(ArEntryFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Data-level security: VENDEDOR only sees their assigned clients
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      boolean isVendedor =
          auth != null
              && auth.getAuthorities().stream()
                  .anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR"));

      if (isVendedor && auth != null) {
        predicates.add(
            cb.equal(root.get("client").get("defaultAdvisor").get("email"), auth.getName()));
      }

      if (filter != null) {
        if (filter.getClientId() != null) {
          predicates.add(cb.equal(root.get("client").get("id"), filter.getClientId()));
        }
        if (filter.getAsesorUserId() != null) {
          predicates.add(cb.equal(root.get("asesor").get("id"), filter.getAsesorUserId()));
        }
        if (filter.getState() != null) {
          predicates.add(cb.equal(root.get("state"), filter.getState()));
        }
        if (filter.getFechaInicio() != null) {
          predicates.add(
              cb.greaterThanOrEqualTo(
                  root.get("invoiceDate"),
                  filter.getFechaInicio().atStartOfDay().atOffset(java.time.ZoneOffset.UTC)));
        }
        if (filter.getFechaFin() != null) {
          predicates.add(
              cb.lessThanOrEqualTo(
                  root.get("invoiceDate"),
                  filter
                      .getFechaFin()
                      .plusDays(1)
                      .atStartOfDay()
                      .atOffset(java.time.ZoneOffset.UTC)));
        }
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}

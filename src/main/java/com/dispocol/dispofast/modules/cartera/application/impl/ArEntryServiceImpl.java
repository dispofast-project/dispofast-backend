package com.dispocol.dispofast.modules.cartera.application.impl;

import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryFilterDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.ArEntryResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.CreateManualArEntryRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.mappers.ArEntryMapper;
import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import com.dispocol.dispofast.modules.cartera.domain.ArEntrySource;
import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.customers.domain.Client;
import com.dispocol.dispofast.modules.customers.domain.Individual;
import com.dispocol.dispofast.modules.customers.domain.Organization;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.invoices.application.interfaces.InvoiceService;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
  private final InvoiceService invoiceService;

  @Override
  @Transactional(readOnly = true)
  public Page<ArEntryResponseDTO> getArEntries(Pageable pageable, ArEntryFilterDTO filter) {
    Specification<ArEntry> spec = buildSpecification(filter);
    return arEntryRepository.findAll(spec, pageable).map(arEntryMapper::toResponseDTO);
  }

  @Override
  @Transactional
  public ArEntryResponseDTO createManualEntry(CreateManualArEntryRequestDTO request) {
    Invoice invoice =
        invoiceService.createManual(
            request.getInvoiceNumber(),
            request.getClientId(),
            request.getInvoiceDate(),
            request.getValue());

    ArEntry entry = new ArEntry();
    entry.setClient(clientRepository.getReferenceById(request.getClientId()));

    if (request.getAsesorUserId() != null) {
      entry.setAsesor(userRepository.getReferenceById(request.getAsesorUserId()));
    }
    if (request.getCityId() != null) {
      entry.setCity(cityRepository.getReferenceById(request.getCityId()));
    }

    entry.setValue(request.getValue());
    entry.setInvoice(invoice);
    entry.setPaymentTermDays(request.getPaymentTermDays());
    entry.setExpirationDate(request.getInvoiceDate().plusDays(request.getPaymentTermDays()));
    entry.setSource(ArEntrySource.MANUAL);

    return arEntryMapper.toResponseDTO(arEntryRepository.save(entry));
  }

  @Override
  @Transactional
  public ArEntryResponseDTO createFromOrder(Invoice invoice) {
    SalesOrder order = invoice.getSalesOrder();

    ArEntry entry = new ArEntry();
    entry.setClient(invoice.getClient());
    entry.setAsesor(order != null ? order.getAsesor() : null);
    entry.setOrder(order);
    entry.setCity(order != null ? order.getShipmentCity() : null);
    entry.setValue(invoice.getTotalValue());
    entry.setInvoice(invoice);
    entry.setPaymentTermDays(DEFAULT_PAYMENT_TERM_DAYS);
    entry.setExpirationDate(invoice.getIssueDate().plusDays(DEFAULT_PAYMENT_TERM_DAYS));
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
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
          predicates.add(buildSearchPredicate(root, cb, filter.getSearch().trim().toLowerCase()));
        }
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Matches the search text against client name (Individual first/last name or Organization legal
   * name), invoice number, and order number, so search covers the whole dataset instead of just the
   * currently loaded page.
   */
  private Predicate buildSearchPredicate(Root<ArEntry> root, CriteriaBuilder cb, String text) {
    String pattern = "%" + text + "%";
    Path<Client> clientPath = root.get("client");
    Join<ArEntry, Invoice> invoiceJoin = root.join("invoice", JoinType.LEFT);
    Join<ArEntry, SalesOrder> orderJoin = root.join("order", JoinType.LEFT);

    Predicate individualName =
        cb.and(
            cb.equal(clientPath.type(), Individual.class),
            cb.or(
                cb.like(cb.lower(cb.treat(clientPath, Individual.class).get("firstName")), pattern),
                cb.like(
                    cb.lower(cb.treat(clientPath, Individual.class).get("lastName")), pattern)));

    Predicate organizationName =
        cb.and(
            cb.equal(clientPath.type(), Organization.class),
            cb.like(cb.lower(cb.treat(clientPath, Organization.class).get("legalName")), pattern));

    Predicate invoiceMatch = cb.like(cb.lower(invoiceJoin.get("invoiceNumber")), pattern);
    Predicate orderMatch = cb.like(cb.lower(orderJoin.get("orderNumber")), pattern);

    return cb.or(individualName, organizationName, invoiceMatch, orderMatch);
  }
}

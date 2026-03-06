package com.dispocol.dispofast.modules.orders.application.impl;

import com.dispocol.dispofast.modules.orders.api.dtos.AttachInvoiceRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderItemDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderFilterDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderItemResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.UpdateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.mappers.SalesOrderItemMapper;
import com.dispocol.dispofast.modules.orders.api.mappers.SalesOrderMapper;
import com.dispocol.dispofast.modules.orders.application.interfaces.SalesOrderService;
import com.dispocol.dispofast.modules.orders.domain.OrderState;
import com.dispocol.dispofast.modules.orders.domain.SalesOrder;
import com.dispocol.dispofast.modules.orders.domain.SalesOrderItem;
import com.dispocol.dispofast.modules.orders.infra.exceptions.InvalidOrderStateException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderAlreadyExistsException;
import com.dispocol.dispofast.modules.orders.infra.exceptions.SalesOrderNotFoundException;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderRepository;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.Location;
import jakarta.persistence.criteria.Predicate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService {

  private static final Set<OrderState> TERMINAL_STATES =
      EnumSet.of(OrderState.DELIVERED, OrderState.CANCELLED);

  private final SalesOrderRepository salesOrderRepository;
  private final SalesOrderItemRepository salesOrderItemRepository;
  private final SalesOrderMapper salesOrderMapper;
  private final SalesOrderItemMapper salesOrderItemMapper;
  private final LocationService locationService;
  private final QuotesRepository quotesRepository;

  @Override
  @Transactional
  public SalesOrderResponseDTO createSalesOrder(CreateSalesOrderRequestDTO request) {
    if (salesOrderRepository.existsByOrderNumber(request.getOrderNumber())) {
      throw new SalesOrderAlreadyExistsException(
          "Ya existe una orden con el número: " + request.getOrderNumber());
    }

    SalesOrder order = salesOrderMapper.toEntity(request);
    order.setState(OrderState.PENDING);

    if (order.getOrderDate() == null) {
      order.setOrderDate(OffsetDateTime.now());
    }

    if (request.getShipmentCityId() != null) {
      Location city = locationService.findEntityById(request.getShipmentCityId());
      order.setShipmentCity(city);
    }

    SalesOrder savedOrder = salesOrderRepository.save(order);
    return buildResponse(savedOrder, saveItems(request.getItems(), savedOrder));
  }

  @Override
  @Transactional
  public SalesOrderResponseDTO createSalesOrderFromQuote(UUID quoteId) {
    Quotes quote =
        quotesRepository
            .findById(quoteId)
            .orElseThrow(
                () ->
                    new SalesOrderNotFoundException(
                        "Cotización no encontrada con id: " + quoteId));

    if (quote.getStatus() != QuoteStatus.ACCEPTED) {
      throw new InvalidOrderStateException(
          "Solo se puede crear una orden a partir de una cotización aceptada. Estado actual: "
              + quote.getStatus().getValue());
    }

    if (salesOrderRepository.existsByQuoteId(quoteId)) {
      throw new SalesOrderAlreadyExistsException(
          "Ya existe una orden generada para la cotización: " + quoteId);
    }

    SalesOrder order = new SalesOrder();
    order.setAccount(quote.getAccount());
    order.setAsesor(quote.getSeller());
    order.setPriceList(quote.getPriceList());
    order.setShipmentCity(quote.getLocation());
    order.setQuote(quote);
    order.setState(OrderState.PENDING);
    order.setOrderDate(OffsetDateTime.now());

    SalesOrder savedOrder = salesOrderRepository.save(order);
    return buildResponse(savedOrder, List.of());
  }

  @Override
  @Transactional(readOnly = true)
  public SalesOrderResponseDTO getSalesOrderById(UUID id) {
    SalesOrder order = findOrderOrThrow(id);
    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    return buildResponse(order, salesOrderItemMapper.toResponseDTOList(items));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SalesOrderResponseDTO> getAllSalesOrders(
      Pageable pageable, SalesOrderFilterDTO filter) {
    Specification<SalesOrder> spec = buildSpecification(filter);
    return salesOrderRepository
        .findAll(spec, pageable)
        .map(
            order -> {
              List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(order.getId());
              return buildResponse(order, salesOrderItemMapper.toResponseDTOList(items));
            });
  }

  @Override
  @Transactional
  public SalesOrderResponseDTO updateSalesOrder(UUID id, UpdateSalesOrderRequestDTO request) {
    SalesOrder order = findOrderOrThrow(id);

    if (TERMINAL_STATES.contains(order.getState())) {
      throw new InvalidOrderStateException(
          "No se puede modificar una orden en estado: " + order.getState().getValue());
    }

    salesOrderMapper.updateEntityFromDTO(request, order);

    if (request.getShipmentCityId() != null) {
      Location city = locationService.findEntityById(request.getShipmentCityId());
      order.setShipmentCity(city);
    }

    List<SalesOrderItemResponseDTO> itemResponses;
    if (request.getItems() != null && !request.getItems().isEmpty()) {
      salesOrderItemRepository.deleteByOrderId(id);
      itemResponses = saveItems(request.getItems(), order);
    } else {
      itemResponses = salesOrderItemMapper.toResponseDTOList(salesOrderItemRepository.findByOrderId(id));
    }

    SalesOrder updatedOrder = salesOrderRepository.save(order);
    return buildResponse(updatedOrder, itemResponses);
  }

  @Override
  @Transactional
  public SalesOrderResponseDTO attachInvoice(UUID id, AttachInvoiceRequestDTO request) {
    SalesOrder order = findOrderOrThrow(id);

    if (order.getState() != OrderState.PENDING) {
      throw new InvalidOrderStateException(
          "Solo se puede adjuntar factura a una orden pendiente. Estado actual: "
              + order.getState().getValue());
    }

    salesOrderMapper.applyInvoice(request, order);
    order.setState(OrderState.INVOICED);

    SalesOrder savedOrder = salesOrderRepository.save(order);
    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    return buildResponse(savedOrder, salesOrderItemMapper.toResponseDTOList(items));
  }

  @Override
  @Transactional
  public void deleteSalesOrder(UUID id) {
    SalesOrder order = findOrderOrThrow(id);

    if (order.getState() != OrderState.PENDING) {
      throw new InvalidOrderStateException(
          "Solo se pueden eliminar órdenes en estado pendiente. Estado actual: "
              + order.getState().getValue());
    }

    salesOrderItemRepository.deleteByOrderId(id);
    salesOrderRepository.delete(order);
  }

  private SalesOrder findOrderOrThrow(UUID id) {
    return salesOrderRepository
        .findById(id)
        .orElseThrow(
            () -> new SalesOrderNotFoundException("Orden de venta no encontrada con id: " + id));
  }

  private List<SalesOrderItemResponseDTO> saveItems(
      List<CreateSalesOrderItemDTO> itemDTOs, SalesOrder order) {
    if (itemDTOs == null || itemDTOs.isEmpty()) {
      return List.of();
    }
    List<SalesOrderItem> items =
        itemDTOs.stream()
            .map(salesOrderItemMapper::toEntity)
            .peek(item -> item.setOrder(order))
            .toList();
    return salesOrderItemMapper.toResponseDTOList(salesOrderItemRepository.saveAll(items));
  }

  private SalesOrderResponseDTO buildResponse(
      SalesOrder order, List<SalesOrderItemResponseDTO> items) {
    SalesOrderResponseDTO response = salesOrderMapper.toResponseDTO(order);
    response.setItems(items);
    return response;
  }

  private Specification<SalesOrder> buildSpecification(SalesOrderFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filter.getState() != null) {
        predicates.add(cb.equal(root.get("state"), filter.getState()));
      }
      if (filter.getAccountId() != null) {
        predicates.add(cb.equal(root.get("account").get("id"), filter.getAccountId()));
      }
      if (filter.getAsesorUserId() != null) {
        predicates.add(cb.equal(root.get("asesor").get("id"), filter.getAsesorUserId()));
      }
      if (filter.getOrderNumber() != null && !filter.getOrderNumber().isBlank()) {
        predicates.add(
            cb.like(
                cb.lower(root.get("orderNumber")),
                "%" + filter.getOrderNumber().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}

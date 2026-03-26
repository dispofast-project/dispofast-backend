package com.dispocol.dispofast.modules.orders.application.impl;

import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
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
import com.dispocol.dispofast.modules.pricelist.application.interfaces.PriceListService;
import com.dispocol.dispofast.modules.pricelist.infra.persistence.PriceListRepository;
import com.dispocol.dispofast.modules.quotes.domain.QuoteStatus;
import com.dispocol.dispofast.modules.quotes.domain.Quotes;
import com.dispocol.dispofast.modules.quotes.infra.persistence.QuotesRepository;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.City;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  private final ClientRepository clientRepository;
  private final PriceListRepository priceListRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;
  private final InventoryService inventoryService;
  private final PriceListService priceListService;
  private final ArEntryService arEntryService;

  @Override
  @Transactional
  public SalesOrderResponseDTO createSalesOrder(CreateSalesOrderRequestDTO request) {
    if (salesOrderRepository.existsByOrderNumber(request.getOrderNumber())) {
      throw new SalesOrderAlreadyExistsException(
          "Ya existe una orden con el numero: " + request.getOrderNumber());
    }

    SalesOrder order = salesOrderMapper.toEntity(request);
    order.setState(OrderState.PENDING);

    if (order.getOrderDate() == null) {
      order.setOrderDate(OffsetDateTime.now());
    }

    resolveOrderReferences(
        order,
        request.getClientId(),
        request.getAsesorUserId(),
        request.getPriceListId(),
        request.getShipmentCityId(),
        request.getQuoteId());

    SalesOrder savedOrder = salesOrderRepository.save(order);
    List<SalesOrderItemResponseDTO> itemResponses =
        saveItems(request.getItems(), savedOrder, request);

    // Reserve stock for each item
    for (CreateSalesOrderItemDTO item : request.getItems()) {
      inventoryService.reserveStock(item.getProductId(), item.getQuantity());
    }

    // Persist totalValue calculated in saveItems
    salesOrderRepository.save(savedOrder);
    arEntryService.createFromOrder(savedOrder);

    return buildResponse(savedOrder, itemResponses);
  }

  @Override
  @Transactional
  public SalesOrderResponseDTO createSalesOrderFromQuote(UUID quoteId) {
    Quotes quote =
        quotesRepository
            .findById(quoteId)
            .orElseThrow(
                () ->
                    new SalesOrderNotFoundException("Cotizacion no encontrada con id: " + quoteId));

    if (quote.getStatus() != QuoteStatus.ACCEPTED) {
      throw new InvalidOrderStateException(
          "Solo se puede crear una orden a partir de una cotizacion aceptada. Estado actual: "
              + quote.getStatus().getValue());
    }

    if (salesOrderRepository.existsByQuoteId(quoteId)) {
      throw new SalesOrderAlreadyExistsException(
          "Ya existe una orden generada para la cotizacion: " + quoteId);
    }

    SalesOrder order = new SalesOrder();
    order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    order.setClient(quote.getAccount());
    order.setAsesor(quote.getSeller());
    order.setPriceList(quote.getPriceList());
    order.setShipmentCity(quote.getCity());
    order.setQuote(quote);
    order.setState(OrderState.PENDING);
    order.setOrderDate(OffsetDateTime.now());
    order.setTotalValue(java.math.BigDecimal.valueOf(quote.getTotalAmount()));

    SalesOrder savedOrder = salesOrderRepository.save(order);
    arEntryService.createFromOrder(savedOrder);

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
    return salesOrderRepository
        .findAll(buildSpecification(filter), pageable)
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

    OrderState previousState = order.getState();
    OrderState requestedState = request.getState();

    salesOrderMapper.updateEntityFromDTO(request, order);

    if (request.getAsesorUserId() != null) {
      order.setAsesor(userRepository.getReferenceById(request.getAsesorUserId()));
    }
    if (request.getPriceListId() != null) {
      order.setPriceList(priceListRepository.getReferenceById(request.getPriceListId()));
    }
    if (request.getShipmentCityId() != null) {
      City city = locationService.findEntityById(request.getShipmentCityId());
      order.setShipmentCity(city);
    }

    List<SalesOrderItemResponseDTO> itemResponses;

    // Handle stock transitions when moving to a terminal state
    if (requestedState != null && requestedState != previousState) {
      List<SalesOrderItem> currentItems = salesOrderItemRepository.findByOrderId(id);

      if (requestedState == OrderState.DELIVERED) {
        currentItems.forEach(
            item -> inventoryService.confirmStock(item.getProduct().getId(), item.getQuantity()));
      } else if (requestedState == OrderState.CANCELLED) {
        currentItems.forEach(
            item -> inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity()));
      }

      // Do not process item changes when transitioning to terminal state
      itemResponses = salesOrderItemMapper.toResponseDTOList(currentItems);
    } else if (request.getItems() != null && !request.getItems().isEmpty()) {
      // Release reserved stock for the old items before replacing them
      List<SalesOrderItem> oldItems = salesOrderItemRepository.findByOrderId(id);
      oldItems.forEach(
          item -> inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity()));

      salesOrderItemRepository.deleteByOrderId(id);
      itemResponses = saveItems(request.getItems(), order, null);

      // Reserve stock for the new items
      request
          .getItems()
          .forEach(dto -> inventoryService.reserveStock(dto.getProductId(), dto.getQuantity()));
    } else {
      itemResponses =
          salesOrderItemMapper.toResponseDTOList(salesOrderItemRepository.findByOrderId(id));
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
          "Solo se pueden eliminar ordenes en estado pendiente. Estado actual: "
              + order.getState().getValue());
    }

    // Release reserved stock before deleting
    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    items.forEach(
        item -> inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity()));

    salesOrderItemRepository.deleteByOrderId(id);
    salesOrderRepository.delete(order);
  }

  private void resolveOrderReferences(
      SalesOrder order,
      UUID clientId,
      UUID asesorUserId,
      UUID priceListId,
      String shipmentCityId,
      UUID quoteId) {

    order.setClient(clientRepository.getReferenceById(clientId));
    order.setAsesor(userRepository.getReferenceById(asesorUserId));

    if (priceListId != null) {
      order.setPriceList(priceListRepository.getReferenceById(priceListId));
    }
    if (shipmentCityId != null) {
      order.setShipmentCity(locationService.findEntityById(shipmentCityId));
    }
    if (quoteId != null) {
      order.setQuote(quotesRepository.getReferenceById(quoteId));
    }
  }

  private SalesOrder findOrderOrThrow(UUID id) {
    return salesOrderRepository
        .findById(id)
        .orElseThrow(
            () -> new SalesOrderNotFoundException("Orden de venta no encontrada con id: " + id));
  }

  private static final BigDecimal IVA_RATE = BigDecimal.valueOf(0.19);

  private List<SalesOrderItemResponseDTO> saveItems(
      List<CreateSalesOrderItemDTO> itemDTOs,
      SalesOrder order,
      CreateSalesOrderRequestDTO request) {
    if (itemDTOs == null || itemDTOs.isEmpty()) {
      order.setTotalValue(BigDecimal.ZERO);
      return List.of();
    }

    UUID priceListId = order.getPriceList() != null ? order.getPriceList().getId() : null;
    if (priceListId == null) {
      throw new IllegalArgumentException(
          "La orden debe tener una lista de precios asignada para calcular los precios");
    }

    BigDecimal subtotal = BigDecimal.ZERO;
    BigDecimal taxAmount = BigDecimal.ZERO;
    List<SalesOrderItem> items = new ArrayList<>();

    for (CreateSalesOrderItemDTO dto : itemDTOs) {
      com.dispocol.dispofast.modules.inventory.domain.Product product =
          productRepository
              .findById(dto.getProductId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Producto no encontrado: " + dto.getProductId()));

      SalesOrderItem item = salesOrderItemMapper.toEntity(dto);
      item.setOrder(order);
      item.setProduct(product);

      BigDecimal unitPrice =
          priceListService
              .resolveUnitPrice(priceListId, product.getReference())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "El producto '"
                              + product.getReference()
                              + "' no tiene precio en la lista de precios seleccionada"));

      BigDecimal itemDiscount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
      BigDecimal lineTotal = dto.getQuantity().multiply(unitPrice).subtract(itemDiscount);

      item.setUnitPrice(unitPrice);
      item.setLineTotal(lineTotal);
      subtotal = subtotal.add(lineTotal);

      if (!product.isTaxFree()) {
        taxAmount = taxAmount.add(lineTotal.multiply(IVA_RATE));
      }
      items.add(item);
    }

    // Apply commercial discounts (only when request is available, e.g. on create)
    BigDecimal discountAmount = BigDecimal.ZERO;
    BigDecimal additionalDiscountAmount = BigDecimal.ZERO;
    BigDecimal retefuente = BigDecimal.ZERO;
    BigDecimal reteica = BigDecimal.ZERO;
    BigDecimal freight = BigDecimal.ZERO;

    if (request != null) {
      int discountPct = request.getDiscountRate() != null ? request.getDiscountRate() : 0;
      discountAmount =
          subtotal
              .multiply(BigDecimal.valueOf(discountPct))
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      BigDecimal addDiscountPct =
          request.getAdditionalDiscountRate() != null
              ? request.getAdditionalDiscountRate()
              : BigDecimal.ZERO;
      additionalDiscountAmount =
          subtotal
              .multiply(addDiscountPct)
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      retefuente =
          request.getRetefuenteAmount() != null ? request.getRetefuenteAmount() : BigDecimal.ZERO;
      reteica = request.getReteicaAmount() != null ? request.getReteicaAmount() : BigDecimal.ZERO;
      freight = request.getFreight() != null ? request.getFreight() : BigDecimal.ZERO;
    }

    BigDecimal totalValue =
        subtotal
            .add(taxAmount)
            .subtract(discountAmount)
            .subtract(additionalDiscountAmount)
            .subtract(retefuente)
            .subtract(reteica)
            .add(freight)
            .setScale(2, RoundingMode.HALF_UP);

    order.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
    order.setRetefuenteAmount(retefuente);
    order.setReteicaAmount(reteica);
    order.setFreight(freight);
    order.setTotalValue(totalValue);

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
      if (filter.getClientId() != null) {
        predicates.add(cb.equal(root.get("client").get("id"), filter.getClientId()));
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

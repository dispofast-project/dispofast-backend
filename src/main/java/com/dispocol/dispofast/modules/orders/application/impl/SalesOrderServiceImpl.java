package com.dispocol.dispofast.modules.orders.application.impl;

import com.dispocol.dispofast.modules.cartera.application.interfaces.ArEntryService;
import com.dispocol.dispofast.modules.customers.infra.persistence.ClientRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.inventory.application.interfaces.InventoryService;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import com.dispocol.dispofast.modules.invoices.application.interfaces.InvoiceService;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
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
import com.dispocol.dispofast.modules.shipping.application.interfaces.ShipmentService;
import com.dispocol.dispofast.shared.location.application.interfaces.LocationService;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.params.infra.persistence.SystemParamRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
public class SalesOrderServiceImpl implements SalesOrderService {

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
  private final SystemParamRepository systemParamRepository;
  private final InvoiceService invoiceService;
  private final ShipmentService shipmentService;
  private final OrderEmailComposer orderEmailComposer;

  @Override
  @Transactional
  public SalesOrderResponseDTO createSalesOrder(CreateSalesOrderRequestDTO request) {
    if (salesOrderRepository.existsByOrderNumber(request.getOrderNumber())) {
      throw new SalesOrderAlreadyExistsException("Ya existe una orden con ese número de pedido.");
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
    List<SalesOrderItemResponseDTO> itemResponses = saveItems(request.getItems(), savedOrder);

    // Reserve stock for each item
    for (CreateSalesOrderItemDTO item : request.getItems()) {
      inventoryService.reserveStock(item.getProductId(), item.getQuantity());
    }

    // Persist totalValue calculated in saveItems
    salesOrderRepository.save(savedOrder);

    orderEmailComposer.sendOrderCreatedEmail(savedOrder, itemResponses);

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
                    new SalesOrderNotFoundException(
                        "La cotización seleccionada no fue encontrada."));

    if (quote.getStatus() != QuoteStatus.ACCEPTED) {
      throw new InvalidOrderStateException(
          "Solo se pueden crear órdenes a partir de cotizaciones aceptadas.");
    }

    if (salesOrderRepository.existsByQuoteId(quoteId)) {
      throw new SalesOrderAlreadyExistsException(
          "Ya existe una orden generada para esta cotización.");
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

    SalesOrder savedOrder = salesOrderRepository.save(order);

    // Copy the quote's items onto the order and derive totals from them, instead of trusting
    // quote.getTotalAmount() directly: the order used to be created with zero items but that
    // pre-computed total, so its payment summary and its (empty) items table disagreed.
    List<CreateSalesOrderItemDTO> itemDTOs =
        quote.getItems().stream()
            .map(
                quoteItem -> {
                  CreateSalesOrderItemDTO dto = new CreateSalesOrderItemDTO();
                  dto.setProductId(quoteItem.getProduct().getId());
                  dto.setQuantity(quoteItem.getQuantity());
                  dto.setUnitPrice(quoteItem.getUnitPrice());
                  return dto;
                })
            .toList();

    // Carry over the quote's discount rates (fractions, e.g. 0.05) as the order's percentage
    // fields (e.g. 5), so an order created from a discounted quote keeps that discount instead
    // of silently losing it.
    if (quote.getCommercialDiscountRate() != null) {
      savedOrder.setDiscountRate(
          quote.getCommercialDiscountRate().multiply(BigDecimal.valueOf(100)).intValue());
    }
    if (quote.getOtherDiscountsRate() != null) {
      savedOrder.setAdditionalDiscountRate(
          quote.getOtherDiscountsRate().multiply(BigDecimal.valueOf(100)));
    }

    List<SalesOrderItemResponseDTO> itemResponses = saveItems(itemDTOs, savedOrder);

    for (CreateSalesOrderItemDTO item : itemDTOs) {
      inventoryService.reserveStock(item.getProductId(), item.getQuantity());
    }

    salesOrderRepository.save(savedOrder);

    return buildResponse(savedOrder, itemResponses);
  }

  @Override
  @Transactional(readOnly = true)
  public SalesOrderResponseDTO getSalesOrderById(UUID id) {
    SalesOrder order = findOrderOrThrow(id);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin =
        auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    if (!isAdmin) {
      boolean isOwner =
          order.getAsesor() != null
              && auth.getName().equalsIgnoreCase(order.getAsesor().getEmail());
      if (!isOwner) {
        throw new SalesOrderNotFoundException("La orden solicitada no fue encontrada.");
      }
    }

    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    return buildResponse(order, salesOrderItemMapper.toResponseDTOList(items));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<SalesOrderResponseDTO> getAllSalesOrders(
      Pageable pageable, SalesOrderFilterDTO filter) {
    Page<SalesOrder> page = salesOrderRepository.findAll(buildSpecification(filter), pageable);

    List<UUID> orderIds = page.getContent().stream().map(SalesOrder::getId).toList();
    Map<UUID, List<SalesOrderItem>> itemsByOrderId =
        orderIds.isEmpty()
            ? Map.of()
            : salesOrderItemRepository.findByOrderIdInWithProduct(orderIds).stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));
    Map<UUID, String> invoiceNumberByOrderId =
        orderIds.isEmpty() ? Map.of() : invoiceService.findInvoiceNumbersByOrderIds(orderIds);
    Map<UUID, String> trackingCodeByOrderId =
        orderIds.isEmpty() ? Map.of() : shipmentService.findTrackingCodesByOrderIds(orderIds);

    return page.map(
        order -> {
          List<SalesOrderItem> items = itemsByOrderId.getOrDefault(order.getId(), List.of());
          SalesOrderResponseDTO response = salesOrderMapper.toResponseDTO(order);
          response.setItems(salesOrderItemMapper.toResponseDTOList(items));
          response.setInvoiceNumber(invoiceNumberByOrderId.get(order.getId()));
          response.setTrackingCode(trackingCodeByOrderId.get(order.getId()));
          return response;
        });
  }

  @Override
  @Transactional
  public SalesOrderResponseDTO updateSalesOrder(UUID id, UpdateSalesOrderRequestDTO request) {
    SalesOrder order = findOrderOrThrow(id);

    if (OrderState.TERMINAL_STATES.contains(order.getState())) {
      throw new InvalidOrderStateException(
          "Esta orden no puede ser modificada porque ya fue entregada o cancelada.");
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
        shipmentService.delayByOrderId(id);
      }

      // Do not process item changes when transitioning to terminal state
      itemResponses = salesOrderItemMapper.toResponseDTOList(currentItems);
    } else if (request.getItems() != null && !request.getItems().isEmpty()) {
      // Release reserved stock for the old items before replacing them
      List<SalesOrderItem> oldItems = salesOrderItemRepository.findByOrderId(id);
      oldItems.forEach(
          item -> inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity()));

      salesOrderItemRepository.deleteByOrderId(id);
      itemResponses = saveItems(request.getItems(), order);

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
  public SalesOrderResponseDTO attachInvoice(UUID id, String invoiceNumber, MultipartFile file) {
    SalesOrder order = findOrderOrThrow(id);

    Invoice invoice = invoiceService.createFromOrder(order, invoiceNumber, file);
    order.setState(OrderState.INVOICED);
    SalesOrder savedOrder = salesOrderRepository.save(order);
    arEntryService.createFromOrder(invoice);
    String cityCode = order.getShipmentCity() != null ? order.getShipmentCity().getCode() : null;
    shipmentService.createFromInvoice(invoice.getId(), order.getShipmentAddress(), cityCode);

    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    return buildResponse(savedOrder, salesOrderItemMapper.toResponseDTOList(items));
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] downloadInvoice(UUID id) {
    findOrderOrThrow(id);
    return invoiceService.downloadPdfByOrderId(id);
  }

  @Override
  @Transactional(readOnly = true)
  public String getInvoiceFileName(UUID id) {
    findOrderOrThrow(id);
    return invoiceService.getPdfFileNameByOrderId(id);
  }

  @Override
  @Transactional
  public void deleteSalesOrder(UUID id) {
    SalesOrder order = findOrderOrThrow(id);

    if (order.getState() != OrderState.PENDING) {
      throw new InvalidOrderStateException("Solo se pueden eliminar órdenes en estado pendiente.");
    }

    // Release reserved stock before deleting
    List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
    items.forEach(
        item -> inventoryService.releaseStock(item.getProduct().getId(), item.getQuantity()));

    salesOrderItemRepository.deleteByOrderId(id);
    salesOrderRepository.delete(order);
  }

  @Override
  @Transactional
  public void cancelOrderByDelayedShipment(UUID orderId) {
    salesOrderRepository
        .findById(orderId)
        .filter(order -> !OrderState.TERMINAL_STATES.contains(order.getState()))
        .ifPresent(
            order -> {
              salesOrderItemRepository
                  .findByOrderId(orderId)
                  .forEach(
                      item ->
                          inventoryService.releaseStock(
                              item.getProduct().getId(), item.getQuantity()));
              order.setState(OrderState.CANCELLED);
              salesOrderRepository.save(order);
            });
  }

  @Override
  @Transactional
  public void syncStateFromShipment(UUID orderId, OrderState newState) {
    salesOrderRepository
        .findById(orderId)
        .filter(order -> !OrderState.TERMINAL_STATES.contains(order.getState()))
        .filter(order -> order.getState() != newState)
        .ifPresent(
            order -> {
              if (newState == OrderState.DELIVERED) {
                salesOrderItemRepository
                    .findByOrderId(orderId)
                    .forEach(
                        item ->
                            inventoryService.confirmStock(
                                item.getProduct().getId(), item.getQuantity()));
              }
              order.setState(newState);
              salesOrderRepository.save(order);
            });
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
            () -> new SalesOrderNotFoundException("La orden solicitada no fue encontrada."));
  }

  private static final BigDecimal IVA = BigDecimal.valueOf(0.19);

  private List<SalesOrderItemResponseDTO> saveItems(
      List<CreateSalesOrderItemDTO> itemDTOs, SalesOrder order) {
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
                          "Uno de los productos seleccionados no fue encontrado."));

      SalesOrderItem item = salesOrderItemMapper.toEntity(dto);
      item.setOrder(order);
      item.setProduct(product);

      BigDecimal unitPrice =
          dto.getUnitPrice() != null
              ? dto.getUnitPrice()
              : priceListService
                  .resolveUnitPrice(priceListId, product.getId())
                  .orElseThrow(
                      () ->
                          new IllegalArgumentException(
                              "El producto '"
                                  + product.getSku()
                                  + "' no tiene precio configurado en la lista de precios seleccionada."));

      BigDecimal itemDiscount = dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO;
      BigDecimal lineTotal = dto.getQuantity().multiply(unitPrice).subtract(itemDiscount);

      item.setUnitPrice(unitPrice);
      item.setLineTotal(lineTotal);
      subtotal = subtotal.add(lineTotal);

      if (!product.isTaxFree()) {
        taxAmount = taxAmount.add(lineTotal.multiply(IVA));
      }
      items.add(item);
    }

    // Apply commercial discounts/freight already set on the order entity — populated from the
    // create request on creation, or from the update request's mapped fields when editing, so
    // this reflects the order's current discount/freight regardless of the call path.
    BigDecimal retefuente = BigDecimal.ZERO;

    int discountPct = order.getDiscountRate() != null ? order.getDiscountRate() : 0;
    BigDecimal discountAmount =
        subtotal
            .multiply(BigDecimal.valueOf(discountPct))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal addDiscountPct =
        order.getAdditionalDiscountRate() != null
            ? order.getAdditionalDiscountRate()
            : BigDecimal.ZERO;
    BigDecimal additionalDiscountAmount =
        subtotal
            .multiply(addDiscountPct)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

    BigDecimal freight = order.getFreight() != null ? order.getFreight() : BigDecimal.ZERO;

    // Retefuente: calculada en el backend según params configurables
    BigDecimal retefuenteRate =
        systemParamRepository
            .findByClave("RETEFUENTE_RATE")
            .map(p -> p.getValor())
            .orElse(new BigDecimal("0.0250"));
    BigDecimal retefuenteThreshold =
        systemParamRepository
            .findByClave("RETEFUENTE_THRESHOLD")
            .map(p -> p.getValor())
            .orElse(new BigDecimal("540000"));

    boolean clientAppliesRetefuente =
        order.getClient() != null && Boolean.TRUE.equals(order.getClient().getRetefuenteApplies());
    if (clientAppliesRetefuente && subtotal.compareTo(retefuenteThreshold) > 0) {
      retefuente = subtotal.multiply(retefuenteRate).setScale(2, RoundingMode.HALF_UP);
    }

    BigDecimal totalValue =
        subtotal
            .add(taxAmount)
            .subtract(discountAmount)
            .subtract(additionalDiscountAmount)
            .subtract(retefuente)
            .add(freight)
            .setScale(2, RoundingMode.HALF_UP);

    order.setTaxAmount(taxAmount.setScale(2, RoundingMode.HALF_UP));
    order.setRetefuenteAmount(retefuente);
    order.setFreight(freight);
    order.setTotalValue(totalValue);

    return salesOrderItemMapper.toResponseDTOList(salesOrderItemRepository.saveAll(items));
  }

  private SalesOrderResponseDTO buildResponse(
      SalesOrder order, List<SalesOrderItemResponseDTO> items) {
    SalesOrderResponseDTO response = salesOrderMapper.toResponseDTO(order);
    response.setItems(items);
    invoiceService.findInvoiceNumberByOrderId(order.getId()).ifPresent(response::setInvoiceNumber);
    shipmentService.findTrackingCodeByOrderId(order.getId()).ifPresent(response::setTrackingCode);
    return response;
  }

  private Specification<SalesOrder> buildSpecification(SalesOrderFilterDTO filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Eagerly fetch the associations the mapper reads (client/asesor/shipmentCity/priceList/
      // quote) to avoid one N+1 lazy-load round trip per row per association. Fetches aren't
      // valid on the COUNT query Spring Data issues for pagination.
      boolean isCountQuery =
          Long.class.equals(query.getResultType()) || long.class.equals(query.getResultType());
      if (!isCountQuery) {
        root.fetch("client", JoinType.LEFT);
        root.fetch("asesor", JoinType.LEFT);
        root.fetch("shipmentCity", JoinType.LEFT);
        root.fetch("priceList", JoinType.LEFT);
        root.fetch("quote", JoinType.LEFT);
      }

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      boolean isAdmin =
          auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
      if (!isAdmin) {
        predicates.add(cb.equal(root.get("asesor").get("email"), auth.getName()));
      }

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

package com.dispocol.dispofast.modules.shipping.application.impl;

import com.dispocol.dispofast.modules.invoices.application.interfaces.InvoiceService;
import com.dispocol.dispofast.modules.invoices.domain.Invoice;
import com.dispocol.dispofast.modules.orders.application.interfaces.SalesOrderService;
import com.dispocol.dispofast.modules.orders.domain.SalesOrderItem;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.UpdateShipmentDTO;
import com.dispocol.dispofast.modules.shipping.api.mappers.ShipmentMapper;
import com.dispocol.dispofast.modules.shipping.application.interfaces.ShipmentService;
import com.dispocol.dispofast.modules.shipping.domain.Carrier;
import com.dispocol.dispofast.modules.shipping.domain.Shipment;
import com.dispocol.dispofast.modules.shipping.domain.ShipmentState;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.DuplicateShipmentException;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.ShipmentNotEditableException;
import com.dispocol.dispofast.modules.shipping.infra.exceptions.ShipmentNotFoundException;
import com.dispocol.dispofast.modules.shipping.infra.persistence.CarrierRepository;
import com.dispocol.dispofast.modules.shipping.infra.persistence.ShipmentRepository;
import com.dispocol.dispofast.shared.location.domain.City;
import com.dispocol.dispofast.shared.location.infra.persistence.CityRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

  private static final Set<ShipmentState> EDITABLE_STATES =
      EnumSet.of(ShipmentState.PENDING, ShipmentState.ASSIGNED);

  private final ShipmentRepository shipmentRepository;
  private final CarrierRepository carrierRepository;
  private final CityRepository cityRepository;
  private final ShipmentMapper shipmentMapper;
  private final InvoiceService invoiceService;
  private final SalesOrderItemRepository salesOrderItemRepository;
  private final SalesOrderService salesOrderService;

  @Autowired
  public ShipmentServiceImpl(
      ShipmentRepository shipmentRepository,
      CarrierRepository carrierRepository,
      CityRepository cityRepository,
      ShipmentMapper shipmentMapper,
      InvoiceService invoiceService,
      SalesOrderItemRepository salesOrderItemRepository,
      @Lazy SalesOrderService salesOrderService) {
    this.shipmentRepository = shipmentRepository;
    this.carrierRepository = carrierRepository;
    this.cityRepository = cityRepository;
    this.shipmentMapper = shipmentMapper;
    this.invoiceService = invoiceService;
    this.salesOrderItemRepository = salesOrderItemRepository;
    this.salesOrderService = salesOrderService;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ShipmentResponseDTO> getAll(
      ShipmentState state,
      String clientName,
      String asesorName,
      LocalDate dateFrom,
      LocalDate dateTo,
      Pageable pageable) {

    OffsetDateTime from =
        dateFrom != null ? dateFrom.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
    OffsetDateTime to =
        dateTo != null ? dateTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC) : null;

    String client = (clientName != null && !clientName.isBlank()) ? clientName.trim() : null;
    String asesor = (asesorName != null && !asesorName.isBlank()) ? asesorName.trim() : null;

    PageRequest unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    return shipmentRepository
        .findWithFilters(state != null ? state.name() : null, client, asesor, from, to, unsorted)
        .map(shipmentMapper::toResponseDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public ShipmentResponseDTO getById(UUID id) {
    return shipmentMapper.toResponseDTO(findEntityById(id));
  }

  @Override
  @Transactional(readOnly = true)
  public ShipmentResponseDTO getByInvoiceId(UUID invoiceId) {
    Shipment shipment =
        shipmentRepository
            .findByInvoiceId(invoiceId)
            .orElseThrow(
                () ->
                    new ShipmentNotFoundException(
                        "No se encontró despacho para la factura: " + invoiceId));
    return shipmentMapper.toResponseDTO(shipment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponseDTO> getByCarrierId(UUID carrierId) {
    return shipmentRepository.findByCarrierId(carrierId).stream()
        .map(shipmentMapper::toResponseDTO)
        .toList();
  }

  @Override
  @Transactional
  public ShipmentResponseDTO createFromInvoice(
      UUID invoiceId, String deliveryAddress, String cityCode) {
    if (shipmentRepository.findByInvoiceId(invoiceId).isPresent()) {
      throw new DuplicateShipmentException("Ya existe un despacho para la factura: " + invoiceId);
    }

    Shipment shipment = new Shipment();
    shipment.setInvoiceId(invoiceId);
    shipment.setDeliveryAddress(deliveryAddress != null ? deliveryAddress : "");
    shipment.setState(ShipmentState.PENDING);

    if (cityCode != null) {
      cityRepository.findById(cityCode).ifPresent(shipment::setCity);
    }

    Invoice invoice = invoiceService.findEntityById(invoiceId);
    shipment.setInvoiceNumber(invoice.getInvoiceNumber());
    var order = invoice.getSalesOrder();
    if (order != null) {
      if (order.getClient() != null) {
        shipment.setClientName(order.getClient().getDisplayName());
      }
      if (order.getAsesor() != null) {
        shipment.setAsesorName(order.getAsesor().getFullName());
      }
      List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(order.getId());
      shipment.setProductCount(items.size());
    }

    return shipmentMapper.toResponseDTO(shipmentRepository.save(shipment));
  }

  @Override
  @Transactional
  public ShipmentResponseDTO update(UUID id, UpdateShipmentDTO dto) {
    Shipment shipment = findEntityById(id);

    if (!EDITABLE_STATES.contains(shipment.getState())) {
      throw new ShipmentNotEditableException(
          "Solo se pueden editar despachos en estado Pendiente o Asignado.");
    }

    if (dto.getDeliveryAddress() != null) {
      shipment.setDeliveryAddress(dto.getDeliveryAddress());
    }

    if (dto.getCityCode() != null) {
      City city =
          cityRepository
              .findById(dto.getCityCode())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Ciudad no encontrada con código: " + dto.getCityCode()));
      shipment.setCity(city);
    }

    if (dto.getCarrierId() == null) {
      shipment.setCarrier(null);
      shipment.setState(ShipmentState.PENDING);
    } else {
      Carrier carrier =
          carrierRepository
              .findById(dto.getCarrierId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Transportista no encontrado con id: " + dto.getCarrierId()));
      shipment.setCarrier(carrier);
      shipment.setState(ShipmentState.ASSIGNED);
    }

    if (dto.getEstimatedDeliveryDate() != null) {
      shipment.setEstimatedDeliveryDate(dto.getEstimatedDeliveryDate());
    }

    return shipmentMapper.toResponseDTO(shipmentRepository.save(shipment));
  }

  @Override
  @Transactional
  public ShipmentResponseDTO updateState(UUID id, ShipmentState state) {
    Shipment shipment = findEntityById(id);
    shipment.setState(state);
    Shipment saved = shipmentRepository.save(shipment);

    if (state == ShipmentState.DELAYED) {
      cancelAssociatedOrder(shipment);
    }

    return shipmentMapper.toResponseDTO(saved);
  }

  @Override
  @Transactional
  public void delayByOrderId(UUID orderId) {
    invoiceService
        .findEntityByOrderId(orderId)
        .flatMap(invoice -> shipmentRepository.findByInvoiceId(invoice.getId()))
        .ifPresent(
            shipment -> {
              if (shipment.getState() != ShipmentState.DELIVERED) {
                shipment.setState(ShipmentState.DELAYED);
                shipmentRepository.save(shipment);
              }
            });
  }

  @Override
  @Transactional(readOnly = true)
  public Shipment findEntityById(UUID id) {
    return shipmentRepository
        .findById(id)
        .orElseThrow(() -> new ShipmentNotFoundException("Despacho no encontrado con id: " + id));
  }

  private void cancelAssociatedOrder(Shipment shipment) {
    Invoice invoice = invoiceService.findEntityById(shipment.getInvoiceId());
    if (invoice.getSalesOrder() != null) {
      salesOrderService.cancelOrderByDelayedShipment(invoice.getSalesOrder().getId());
    }
  }
}

package com.dispocol.dispofast.modules.shipping.api.mappers;

import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentItemResponseDTO;
import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Shipment;
import com.dispocol.dispofast.modules.shipping.domain.ShipmentItem;
import com.dispocol.dispofast.shared.location.api.mappers.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentMapper {

  private final CarrierMapper carrierMapper;
  private final DriverMapper driverMapper;
  private final CityMapper cityMapper;

  public ShipmentResponseDTO toResponseDTO(Shipment shipment) {
    if (shipment == null) {
      return null;
    }
    return new ShipmentResponseDTO(
        shipment.getId(),
        shipment.getInvoiceId(),
        shipment.getInvoiceNumber(),
        shipment.getCreatedAt(),
        shipment.getClientName(),
        shipment.getAsesorName(),
        shipment.getState().name(),
        shipment.getState().getValue(),
        shipment.getDeliveryAddress(),
        shipment.getAddressDetail(),
        shipment.getDepartureDate(),
        shipment.getDeliveryDate(),
        carrierMapper.toResponseDTO(shipment.getCarrier()),
        driverMapper.toResponseDTO(shipment.getDriver()),
        shipment.getCity() != null ? cityMapper.toCityDTO(shipment.getCity()) : null,
        shipment.getEstimatedDeliveryDate(),
        shipment.getProductCount(),
        shipment.getDeliveryType(),
        shipment.getTrackingCode(),
        null);
  }

  public ShipmentItemResponseDTO toItemDTO(ShipmentItem item) {
    return new ShipmentItemResponseDTO(
        item.getId(),
        item.getProductSku(),
        item.getProductName(),
        item.getQuantity(),
        item.getUnitPrice(),
        item.getTaxPercent(),
        item.getLineTotal());
  }
}

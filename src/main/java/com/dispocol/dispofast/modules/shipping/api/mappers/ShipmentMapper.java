package com.dispocol.dispofast.modules.shipping.api.mappers;

import com.dispocol.dispofast.modules.shipping.api.dtos.ShipmentResponseDTO;
import com.dispocol.dispofast.modules.shipping.domain.Shipment;
import com.dispocol.dispofast.shared.location.api.mappers.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShipmentMapper {

  private final CarrierMapper carrierMapper;
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
        shipment.getDepartureDate(),
        shipment.getDeliveryDate(),
        carrierMapper.toResponseDTO(shipment.getCarrier()),
        shipment.getCity() != null ? cityMapper.toCityDTO(shipment.getCity()) : null,
        shipment.getEstimatedDeliveryDate(),
        shipment.getProductCount());
  }
}

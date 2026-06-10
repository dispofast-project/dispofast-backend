package com.dispocol.dispofast.modules.cartera.api.mappers;

import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentReceiptMapper {

  @Mapping(target = "arEntryId", source = "arEntry.id")
  @Mapping(target = "createdByName", expression = "java(receipt.getCreatedBy().getFullName())")
  PaymentReceiptResponseDTO toResponseDTO(PaymentReceipt receipt);

  List<PaymentReceiptResponseDTO> toResponseDTOList(List<PaymentReceipt> receipts);
}

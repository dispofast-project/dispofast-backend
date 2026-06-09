package com.dispocol.dispofast.modules.cartera.application.impl;

import com.dispocol.dispofast.modules.cartera.api.dtos.CreatePaymentReceiptRequestDTO;
import com.dispocol.dispofast.modules.cartera.api.dtos.PaymentReceiptResponseDTO;
import com.dispocol.dispofast.modules.cartera.api.mappers.PaymentReceiptMapper;
import com.dispocol.dispofast.modules.cartera.application.interfaces.PaymentReceiptService;
import com.dispocol.dispofast.modules.cartera.domain.ArEntry;
import com.dispocol.dispofast.modules.cartera.domain.ArEntryState;
import com.dispocol.dispofast.modules.cartera.domain.PaymentReceipt;
import com.dispocol.dispofast.modules.cartera.infra.persistence.ArEntryRepository;
import com.dispocol.dispofast.modules.cartera.infra.persistence.PaymentReceiptRepository;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

  private final PaymentReceiptRepository paymentReceiptRepository;
  private final ArEntryRepository arEntryRepository;
  private final UserRepository userRepository;
  private final PaymentReceiptMapper paymentReceiptMapper;

  @Override
  @Transactional
  public PaymentReceiptResponseDTO createReceipt(
      UUID arEntryId, CreatePaymentReceiptRequestDTO request) {

    ArEntry arEntry =
        arEntryRepository
            .findById(arEntryId)
            .orElseThrow(() -> new ResourceNotFoundException("Cartera no encontrada: " + arEntryId));

    if (arEntry.getState() == ArEntryState.PAID) {
      throw new IllegalStateException("Esta cartera ya se encuentra pagada");
    }

    BigDecimal balance = arEntry.getValue().subtract(arEntry.getPaidAmount());
    if (request.getValue().compareTo(balance) > 0) {
      throw new IllegalArgumentException(
          "El valor del recibo (" + request.getValue() + ") supera el saldo pendiente (" + balance + ")");
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AppUser createdBy =
        userRepository
            .findByEmailIgnoreCase(auth.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    PaymentReceipt receipt = new PaymentReceipt();
    receipt.setArEntry(arEntry);
    receipt.setCreatedBy(createdBy);
    receipt.setValue(request.getValue());
    receipt.setPaymentDate(request.getPaymentDate());
    receipt.setPaymentMethod(request.getPaymentMethod());
    receipt.setDocumentNumber(request.getDocumentNumber());
    receipt.setVoucherS3Key(request.getVoucherS3Key());
    receipt.setObservations(request.getObservations());

    paymentReceiptRepository.save(receipt);

    BigDecimal newPaidAmount = arEntry.getPaidAmount().add(request.getValue());
    arEntry.setPaidAmount(newPaidAmount);
    if (newPaidAmount.compareTo(arEntry.getValue()) >= 0) {
      arEntry.setState(ArEntryState.PAID);
    }
    arEntryRepository.save(arEntry);

    return paymentReceiptMapper.toResponseDTO(receipt);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentReceiptResponseDTO> getReceiptsByArEntry(UUID arEntryId) {
    if (!arEntryRepository.existsById(arEntryId)) {
      throw new ResourceNotFoundException("Cartera no encontrada: " + arEntryId);
    }
    return paymentReceiptMapper.toResponseDTOList(
        paymentReceiptRepository.findByArEntryIdOrderByPaymentDateDesc(arEntryId));
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentReceiptResponseDTO getReceiptById(UUID id) {
    return paymentReceiptRepository
        .findById(id)
        .map(paymentReceiptMapper::toResponseDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + id));
  }

  @Override
  public double getTotalPaidValue() {
    return paymentReceiptRepository.sumTotalPaidValue();
  }
}

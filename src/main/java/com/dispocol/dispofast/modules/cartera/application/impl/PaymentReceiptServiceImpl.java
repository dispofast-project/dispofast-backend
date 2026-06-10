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
import com.dispocol.dispofast.shared.S3.application.interfaces.S3Service;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PaymentReceiptServiceImpl implements PaymentReceiptService {

  private static final String VOUCHERS_BUCKET = "dispofast-payment-vouchers";

  private final PaymentReceiptRepository paymentReceiptRepository;
  private final ArEntryRepository arEntryRepository;
  private final UserRepository userRepository;
  private final PaymentReceiptMapper paymentReceiptMapper;
  private final S3Service s3Service;

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

  @Override
  public String uploadVoucher(MultipartFile file) {
    String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
    String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
    String key = UUID.randomUUID() + ext;
    try {
      s3Service.uploadFile(
          VOUCHERS_BUCKET, key, file.getInputStream(),
          file.getContentType() != null ? file.getContentType() : "application/octet-stream",
          file.getSize());
    } catch (IOException e) {
      throw new RuntimeException("Error al subir el comprobante", e);
    }
    return key;
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] downloadVoucher(UUID receiptId) {
    PaymentReceipt receipt = paymentReceiptRepository
        .findById(receiptId)
        .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + receiptId));
    if (receipt.getVoucherS3Key() == null) {
      throw new ResourceNotFoundException("Este recibo no tiene comprobante adjunto");
    }
    return s3Service.downloadFile(VOUCHERS_BUCKET, receipt.getVoucherS3Key());
  }

  @Override
  @Transactional(readOnly = true)
  public String getVoucherFilename(UUID receiptId) {
    PaymentReceipt receipt = paymentReceiptRepository
        .findById(receiptId)
        .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + receiptId));
    String key = receipt.getVoucherS3Key();
    String ext = (key != null && key.contains(".")) ? key.substring(key.lastIndexOf('.')) : "";
    String docRef = receipt.getDocumentNumber() != null
        ? receipt.getDocumentNumber()
        : receipt.getReceiptCode();
    return "comprobante_" + docRef + ext;
  }

  private static String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) return "";
    return filename.substring(filename.lastIndexOf('.'));
  }
}

package com.dispocol.dispofast.modules.iam.application.impl;

import com.dispocol.dispofast.modules.iam.api.dtos.CreateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UpdateUserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.UserProductAllocationDTO;
import com.dispocol.dispofast.modules.iam.application.interfaces.UserProductAllocationService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;
import com.dispocol.dispofast.modules.iam.domain.UserProductAllocation;
import com.dispocol.dispofast.modules.iam.infra.exceptions.InsufficientAllocationException;
import com.dispocol.dispofast.modules.iam.infra.exceptions.UserNotFoundException;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserProductAllocationRepository;
import com.dispocol.dispofast.modules.iam.infra.persistence.UserRepository;
import com.dispocol.dispofast.modules.inventory.domain.Product;
import com.dispocol.dispofast.modules.inventory.infra.exceptions.ProductNotFoundException;
import com.dispocol.dispofast.modules.inventory.infra.persistence.ProductRepository;
import com.dispocol.dispofast.shared.error.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProductAllocationServiceImpl implements UserProductAllocationService {

  private final UserProductAllocationRepository allocationRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public List<UserProductAllocationDTO> getAllocations(UUID userId) {
    return allocationRepository.findByUserId(userId).stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserProductAllocationDTO createAllocation(
      UUID userId, CreateUserProductAllocationDTO dto) {
    AppUser user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("No se encontró el usuario solicitado."));

    Product product =
        productRepository
            .findById(dto.getProductId())
            .orElseThrow(
                () ->
                    new ProductNotFoundException(
                        "El producto seleccionado no existe: " + dto.getProductId()));

    if (allocationRepository.findByUser_IdAndProduct_Id(userId, dto.getProductId()).isPresent()) {
      throw new IllegalArgumentException(
          "Ya existe un cupo asignado a este vendedor para el producto " + product.getName());
    }

    UserProductAllocation allocation = new UserProductAllocation();
    allocation.setUser(user);
    allocation.setProduct(product);
    allocation.setAssignedQuantity(dto.getAssignedQuantity());
    allocation.setConsumedQuantity(0);

    return toDTO(allocationRepository.save(allocation));
  }

  @Override
  @Transactional
  public UserProductAllocationDTO updateAllocation(
      UUID userId, UUID allocationId, UpdateUserProductAllocationDTO dto) {
    UserProductAllocation allocation = findOwnedAllocation(userId, allocationId);
    allocation.setAssignedQuantity(dto.getAssignedQuantity());
    return toDTO(allocationRepository.save(allocation));
  }

  @Override
  @Transactional
  public void deleteAllocation(UUID userId, UUID allocationId) {
    UserProductAllocation allocation = findOwnedAllocation(userId, allocationId);
    allocationRepository.delete(allocation);
  }

  @Override
  @Transactional
  public void reserveAllocation(UUID userId, UUID productId, BigDecimal quantity) {
    allocationRepository
        .findByUser_IdAndProduct_Id(userId, productId)
        .ifPresent(
            allocation -> {
              int qty = quantity.intValue();
              int remaining = allocation.getAssignedQuantity() - allocation.getConsumedQuantity();
              if (remaining < qty) {
                throw new InsufficientAllocationException(
                    "El vendedor "
                        + allocation.getUser().getFullName()
                        + " no tiene cupo suficiente para el producto "
                        + allocation.getProduct().getName()
                        + ". Cupo restante: "
                        + remaining
                        + ", solicitado: "
                        + qty);
              }
              allocation.setConsumedQuantity(allocation.getConsumedQuantity() + qty);
              allocationRepository.save(allocation);
            });
  }

  @Override
  @Transactional
  public void releaseAllocation(UUID userId, UUID productId, BigDecimal quantity) {
    allocationRepository
        .findByUser_IdAndProduct_Id(userId, productId)
        .ifPresent(
            allocation -> {
              int qty = quantity.intValue();
              allocation.setConsumedQuantity(Math.max(0, allocation.getConsumedQuantity() - qty));
              allocationRepository.save(allocation);
            });
  }

  private UserProductAllocation findOwnedAllocation(UUID userId, UUID allocationId) {
    return allocationRepository
        .findById(allocationId)
        .filter(a -> a.getUser().getId().equals(userId))
        .orElseThrow(
            () -> new ResourceNotFoundException("El cupo de inventario no fue encontrado."));
  }

  private UserProductAllocationDTO toDTO(UserProductAllocation allocation) {
    return new UserProductAllocationDTO(
        allocation.getId(),
        allocation.getProduct().getId(),
        allocation.getProduct().getName(),
        allocation.getProduct().getSku(),
        allocation.getAssignedQuantity(),
        allocation.getConsumedQuantity(),
        allocation.getAssignedQuantity() - allocation.getConsumedQuantity());
  }
}

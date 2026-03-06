package com.dispocol.dispofast.modules.orders.application.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dispocol.dispofast.modules.orders.api.dtos.AttachInvoiceRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.CreateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderFilterDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.SalesOrderResponseDTO;
import com.dispocol.dispofast.modules.orders.api.dtos.UpdateSalesOrderRequestDTO;
import com.dispocol.dispofast.modules.orders.api.mappers.SalesOrderMapper;
import com.dispocol.dispofast.modules.orders.application.interfaces.SalesOrderService;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderItemRepository;
import com.dispocol.dispofast.modules.orders.infra.persistence.SalesOrderRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SalesOrderServiceImpl implements SalesOrderService{

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemRepository salesOrderItemRepository;

    @Override
    public SalesOrderResponseDTO createSalesOrder(CreateSalesOrderRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSalesOrder'");
    }

    @Override
    public SalesOrderResponseDTO createSalesOrderFromQuote(UUID quoteId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createSalesOrderFromQuote'");
    }

    @Override
    public SalesOrderResponseDTO getSalesOrderById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSalesOrderById'");
    }

    @Override
    public Page<SalesOrderResponseDTO> getAllSalesOrders(Pageable pageable, SalesOrderFilterDTO filter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllSalesOrders'");
    }

    @Override
    public SalesOrderResponseDTO updateSalesOrder(UUID id, UpdateSalesOrderRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateSalesOrder'");
    }

    @Override
    public SalesOrderResponseDTO attachInvoice(UUID id, AttachInvoiceRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'attachInvoice'");
    }

    @Override
    public void deleteSalesOrder(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteSalesOrder'");
    }
    
}

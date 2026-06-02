package com.project.backend.mapper.host;

import com.project.backend.entity.Invoice;
import com.project.backend.entity.ContractService;
import com.project.backend.dto.host.invoice.InvoiceDetailDTO;
import com.project.backend.dto.host.invoice.InvoiceResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ invoice giữa entity và DTO.
 */
@Component("hostInvoiceMapper")
public class InvoiceMapper {

        /**
     * Chức năng: Chuyển đổi response dto.
     */
public InvoiceResponseDTO toResponseDTO(Invoice invoice) {
        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setTenantName(invoice.getContract().getTenant().getFullName());
        dto.setRoomCode(invoice.getContract().getRoom().getRoomCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentProofUrl(invoice.getPaymentProofUrl());
        dto.setPaymentSubmittedAt(invoice.getPaymentSubmittedAt());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        return dto;
    }

        /**
     * Chức năng: Chuyển đổi detail dto.
     */
public InvoiceDetailDTO toDetailDTO(Invoice invoice, List<ContractService> services) {
        InvoiceDetailDTO dto = new InvoiceDetailDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setTenantName(invoice.getContract().getTenant().getFullName());
        dto.setRoomCode(invoice.getContract().getRoom().getRoomCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setDueDate(invoice.getDueDate());
        dto.setRentAmount(invoice.getRentAmount());
        dto.setElecOld(invoice.getElecOld());
        dto.setElecNew(invoice.getElecNew());
        dto.setElecPrice(invoice.getElecPrice());
        dto.setElecAmount(invoice.getElecAmount());
        dto.setWaterOld(invoice.getWaterOld());
        dto.setWaterNew(invoice.getWaterNew());
        dto.setWaterPrice(invoice.getWaterPrice());
        dto.setWaterAmount(invoice.getWaterAmount());
        dto.setServiceAmount(invoice.getServiceAmount());
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentProofUrl(invoice.getPaymentProofUrl());
        dto.setPaymentSubmittedAt(invoice.getPaymentSubmittedAt());
        dto.setPaymentNote(invoice.getPaymentNote());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setPaidAt(invoice.getPaidAt());
        return dto;
    }
}

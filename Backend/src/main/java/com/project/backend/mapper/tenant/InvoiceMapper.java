package com.project.backend.mapper.tenant;

import com.project.backend.entity.ContractService;
import com.project.backend.entity.Invoice;
import com.project.backend.dto.tenant.invoice.MyInvoiceDTO;
import com.project.backend.dto.tenant.invoice.MyInvoiceDetailDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ invoice giữa entity và DTO.
 */
@Component("tenantInvoiceMapper")
public class InvoiceMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public MyInvoiceDTO toDTO(Invoice invoice) {
        MyInvoiceDTO dto = new MyInvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setRoomCode(invoice.getContract().getRoom().getRoomCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentSubmittedAt(invoice.getPaymentSubmittedAt());
        dto.setPaymentProofUrl(invoice.getPaymentProofUrl());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        return dto;
    }

        /**
     * Chức năng: Chuyển đổi detail dto.
     */
public MyInvoiceDetailDTO toDetailDTO(Invoice invoice,
                                          List<ContractService> services) {
        MyInvoiceDetailDTO dto = new MyInvoiceDetailDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setInvoiceCode(invoice.getInvoiceCode());
        dto.setRoomCode(invoice.getContract().getRoom().getRoomCode());
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setDueDate(invoice.getDueDate());
        dto.setElecOld(invoice.getElecOld());
        dto.setElecNew(invoice.getElecNew());
        dto.setElecPrice(invoice.getElecPrice());
        dto.setElecAmount(invoice.getElecAmount());
        dto.setWaterOld(invoice.getWaterOld());
        dto.setWaterNew(invoice.getWaterNew());
        dto.setWaterPrice(invoice.getWaterPrice());
        dto.setWaterAmount(invoice.getWaterAmount());
        dto.setRentAmount(invoice.getRentAmount());
        dto.setServiceAmount(invoice.getServiceAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setPaidAt(invoice.getPaidAt());
        dto.setServiceNames(services.stream()
                .map(cs -> cs.getService().getServiceName())
                .toList());
        dto.setPaymentProofUrl(invoice.getPaymentProofUrl());
        dto.setPaymentSubmittedAt(invoice.getPaymentSubmittedAt());
        dto.setPaymentNote(invoice.getPaymentNote());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        return dto;
    }
}

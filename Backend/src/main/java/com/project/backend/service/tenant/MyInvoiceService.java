package com.project.backend.service.tenant;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Invoice;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.dto.tenant.invoice.MyInvoiceDTO;
import com.project.backend.dto.tenant.invoice.MyInvoiceDetailDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.tenant.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyInvoiceService {

    private static final Set<String> INVOICE_SORT_FIELDS = Set.of(
            "createdAt", "billingMonth", "billingYear", "totalAmount", "status"
    );
    private static final String PAYMENT_STATUS_PENDING_REVIEW = "PENDING_REVIEW";

    private final InvoiceRepository invoiceRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final PaymentProofStorageService paymentProofStorageService;

    public List<MyInvoiceDTO> getMyInvoices(Long tenantId) {
        return invoiceRepository
                .findByContract_Tenant_UserId(tenantId).stream()
                .map(invoiceMapper::toDTO)
                .toList();
    }

    public PagedResponse<MyInvoiceDTO> getMyInvoicesPage(
            Long tenantId,
            String status,
            String search,
            int page,
            int size,
            String sort
    ) {
        Page<Invoice> invoicePage = invoiceRepository.findPageWithRelationsByTenantId(
                tenantId,
                normalize(status),
                normalize(search),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), buildSort(sort))
        );
        List<MyInvoiceDTO> items = invoicePage.getContent().stream()
                .map(invoiceMapper::toDTO)
                .toList();
        return PagedResponse.from(invoicePage, items);
    }

    public MyInvoiceDetailDTO getInvoiceDetail(Long invoiceId, Long tenantId) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay hoa don: " + invoiceId));

        if (!invoice.getContract().getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Khong tim thay hoa don: " + invoiceId);
        }

        var services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());

        return invoiceMapper.toDetailDTO(invoice, services);
    }

    public MyInvoiceDetailDTO submitPaymentProof(
            Long invoiceId,
            Long tenantId,
            MultipartFile file,
            String note
    ) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay hoa don: " + invoiceId));

        if (!invoice.getContract().getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Khong tim thay hoa don: " + invoiceId);
        }

        if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException("Hoa don nay da duoc thanh toan");
        }

        if (!"UNPAID".equalsIgnoreCase(invoice.getStatus())
                && !"OVERDUE".equalsIgnoreCase(invoice.getStatus())) {
            throw new IllegalStateException("Chi co the gui minh chung cho hoa don cho thanh toan hoac qua han");
        }

        String proofUrl = paymentProofStorageService.uploadPaymentProof(file, invoiceId);
        invoice.setPaymentProofUrl(proofUrl);
        invoice.setPaymentSubmittedAt(LocalDateTime.now());
        invoice.setPaymentNote(normalize(note));
        invoice.setPaymentStatus(PAYMENT_STATUS_PENDING_REVIEW);
        invoiceRepository.save(invoice);

        var services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());

        return invoiceMapper.toDetailDTO(invoice, services);
    }

    private Sort buildSort(String sort) {
        String[] sortParts = (sort == null ? "" : sort).split(",", 2);
        String requestedField = sortParts.length > 0 ? sortParts[0].trim() : "";
        String field = INVOICE_SORT_FIELDS.contains(requestedField) ? requestedField : "createdAt";
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

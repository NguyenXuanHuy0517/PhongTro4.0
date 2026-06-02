package com.project.backend.service.host;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Contract;
import com.project.backend.entity.ContractService;
import com.project.backend.entity.Invoice;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.InvoiceRepository;
import com.project.backend.dto.host.invoice.InvoiceDetailDTO;
import com.project.backend.dto.host.invoice.InvoiceResponseDTO;
import com.project.backend.dto.host.invoice.MeterReadingDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private static final Set<String> INVOICE_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "billingMonth", "billingYear", "totalAmount", "status"
    );
    private static final String PAYMENT_STATUS_APPROVED = "APPROVED";

    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final InvoiceMapper invoiceMapper;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<InvoiceResponseDTO> getInvoicesByHost(Long hostId) {
        return invoiceRepository.findWithRelationsByHostId(hostId).stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public PagedResponse<InvoiceResponseDTO> getInvoicesPage(
            Long hostId,
            String status,
            String search,
            Integer month,
            Integer year,
            int page,
            int size,
            String sort
    ) {
        Page<Invoice> invoicePage = invoiceRepository.findPageWithRelationsByHostId(
                hostId,
                normalize(status),
                normalize(search),
                month,
                year,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), buildSort(sort))
        );

        List<InvoiceResponseDTO> items = invoicePage.getContent().stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
        return PagedResponse.from(invoicePage, items);
    }

    public InvoiceDetailDTO getInvoiceDetail(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoa don: " + invoiceId));
        List<ContractService> services = contractServiceRepository
                .findByContract_ContractId(invoice.getContract().getContractId());
        return invoiceMapper.toDetailDTO(invoice, services);
    }

    public List<InvoiceResponseDTO> getOverdueInvoices(Long hostId) {
        return invoiceRepository.findWithRelationsByHostIdAndStatus(hostId, "OVERDUE").stream()
                .map(invoiceMapper::toResponseDTO)
                .toList();
    }

    public InvoiceDetailDTO updateMeterReading(Long invoiceId, MeterReadingDTO request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoa don: " + invoiceId));

        Contract contract = invoice.getContract();
        List<ContractService> services = contractServiceRepository
                .findByContract_ContractId(contract.getContractId());

        BigDecimal elecPrice = contract.getElecPriceOverride() != null
                ? contract.getElecPriceOverride()
                : contract.getRoom().getElecPrice();

        BigDecimal waterPrice = contract.getWaterPriceOverride() != null
                ? contract.getWaterPriceOverride()
                : contract.getRoom().getWaterPrice();

        BigDecimal elecAmount = elecPrice.multiply(
                BigDecimal.valueOf(request.getElecNew() - request.getElecOld()));
        BigDecimal waterAmount = waterPrice.multiply(
                BigDecimal.valueOf(request.getWaterNew() - request.getWaterOld()));
        BigDecimal serviceAmount = services.stream()
                .map(cs -> cs.getPriceSnapshot().multiply(BigDecimal.valueOf(cs.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setElecOld(request.getElecOld());
        invoice.setElecNew(request.getElecNew());
        invoice.setElecPrice(elecPrice);
        invoice.setWaterOld(request.getWaterOld());
        invoice.setWaterNew(request.getWaterNew());
        invoice.setWaterPrice(waterPrice);
        invoice.setElecAmount(elecAmount);
        invoice.setWaterAmount(waterAmount);
        invoice.setServiceAmount(serviceAmount);
        invoice.setRentAmount(contract.getActualRentPrice());
        invoice.setTotalAmount(contract.getActualRentPrice()
                .add(elecAmount).add(waterAmount).add(serviceAmount));
        invoice.setStatus("UNPAID");

        invoiceRepository.save(invoice);
        
        // Cảnh báo bất thường bằng AI (Chạy ngầm)
        double elecUsage = request.getElecNew() - request.getElecOld();
        if (elecUsage > 0) {
            CompletableFuture.runAsync(() -> {
                try {
                    Map<String, Object> aiReq = Map.of(
                            "room_code", contract.getRoom().getRoomCode(),
                            "billing_month", invoice.getBillingMonth(),
                            "billing_year", invoice.getBillingYear(),
                            "elec_usage", elecUsage,
                            "water_usage", request.getWaterNew() - request.getWaterOld()
                    );
                    restTemplate.postForObject(aiServiceUrl + "/api/v1/anomaly/check", aiReq, String.class);
                } catch (Exception e) {
                    log.error("Error calling AI Anomaly Detection: {}", e.getMessage());
                }
            });
        }
        
        return invoiceMapper.toDetailDTO(invoice, services);
    }

    public void confirmPayment(Long invoiceId, User paidBy) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoa don: " + invoiceId));
        invoice.setStatus("PAID");
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPaidBy(paidBy);
        invoice.setPaymentStatus(PAYMENT_STATUS_APPROVED);
        invoiceRepository.save(invoice);
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

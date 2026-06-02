package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.User;
import com.project.backend.dto.host.invoice.InvoiceDetailDTO;
import com.project.backend.dto.host.invoice.InvoiceResponseDTO;
import com.project.backend.dto.host.invoice.MeterReadingDTO;
import com.project.backend.service.host.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/host/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final BillingService billingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InvoiceResponseDTO>>> getInvoices(@RequestParam Long hostId) {
        return ResponseEntity.ok(ApiResponse.success(billingService.getInvoicesByHost(hostId)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceResponseDTO>>> getInvoicesPaged(
            @RequestParam Long hostId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                billingService.getInvoicesPage(hostId, status, search, month, year, page, size, sort)
        ));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceDetailDTO>> getInvoiceDetail(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(ApiResponse.success(billingService.getInvoiceDetail(invoiceId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<InvoiceResponseDTO>>> getOverdueInvoices(@RequestParam Long hostId) {
        return ResponseEntity.ok(ApiResponse.success(billingService.getOverdueInvoices(hostId)));
    }

    @PutMapping("/{invoiceId}/meters")
    public ResponseEntity<ApiResponse<InvoiceDetailDTO>> updateMeters(
            @PathVariable Long invoiceId,
            @RequestBody MeterReadingDTO request) {
        return ResponseEntity.ok(ApiResponse.success(billingService.updateMeterReading(invoiceId, request)));
    }

    @PatchMapping("/{invoiceId}/pay")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(
            @PathVariable Long invoiceId,
            @RequestParam Long paidById) {
        User paidBy = new User();
        paidBy.setUserId(paidById);
        billingService.confirmPayment(invoiceId, paidBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

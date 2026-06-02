package com.project.backend.controller.tenant;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.common.PagedResponse;
import com.project.backend.dto.tenant.invoice.MyInvoiceDTO;
import com.project.backend.dto.tenant.invoice.MyInvoiceDetailDTO;
import com.project.backend.service.tenant.MyInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tenant/invoices")
@RequiredArgsConstructor
public class MyInvoiceController {

    private final MyInvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MyInvoiceDTO>>> getMyInvoices(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getMyInvoices(userId)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<MyInvoiceDTO>>> getMyInvoicesPaged(
            @RequestParam Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.getMyInvoicesPage(userId, status, search, page, size, sort)
        ));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<MyInvoiceDetailDTO>> getInvoiceDetail(
            @PathVariable Long invoiceId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getInvoiceDetail(invoiceId, userId)));
    }

    @PostMapping(value = "/{invoiceId}/payment-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MyInvoiceDetailDTO>> submitPaymentProof(
            @PathVariable Long invoiceId,
            @RequestParam Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(ApiResponse.success(
                invoiceService.submitPaymentProof(invoiceId, userId, file, note)
        ));
    }
}

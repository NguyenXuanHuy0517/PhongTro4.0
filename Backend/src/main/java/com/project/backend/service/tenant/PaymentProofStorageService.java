package com.project.backend.service.tenant;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProofStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public String uploadPaymentProof(MultipartFile file, Long invoiceId) {
        validateImageFile(file);

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/payment-proofs",
                            "public_id", "invoice_" + invoiceId + "_proof",
                            "overwrite", true,
                            "resource_type", "image",
                            "transformation", "w_1280,c_limit,q_auto,f_auto"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Upload payment proof thất bại cho invoiceId {}: {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Loi upload minh chung thanh toan: " + e.getMessage(), e);
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File khong duoc rong");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chi chap nhan file anh cho minh chung thanh toan");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kich thuoc file khong duoc vuot qua 5 MB");
        }
    }
}

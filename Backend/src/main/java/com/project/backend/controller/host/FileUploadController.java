package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.service.host.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Vai trò: REST controller của module host-service.
 * Chức năng: Tiếp nhận request HTTP cho nghiệp vụ file upload và điều phối xử lý sang tầng bên dưới.
 */
@Slf4j
@RestController
@RequestMapping("/api/host/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    
    
    

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload avatar.
     * URL: POST /api/host/upload/avatar
     */
@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/host/upload/avatar - fileName: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        validateImageFile(file);

        String url = fileUploadService.uploadAvatar(file);

        log.info("POST /api/host/upload/avatar - upload thành công: {}", url);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    

        /**
     * Chức năng: Xóa avatar.
     * URL: DELETE /api/host/upload/avatar
     */
@DeleteMapping("/avatar")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar(
            @RequestParam String publicId) {

        log.info("DELETE /api/host/upload/avatar - publicId: {}", publicId);
        fileUploadService.deleteFile(publicId);
        log.info("DELETE /api/host/upload/avatar - xoá thành công: {}", publicId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    
    
    

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload room image.
     * URL: POST /api/host/upload/room
     */
@PostMapping(value = "/room", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadRoomImage(
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/host/upload/room - fileName: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());

        validateImageFile(file);

        String url = fileUploadService.uploadRoomImage(file);

        log.info("POST /api/host/upload/room - upload thành công: {}", url);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload room images.
     * URL: POST /api/host/upload/room/batch
     */
@PostMapping(value = "/room/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> uploadRoomImages(
            @RequestParam("files") List<MultipartFile> files) {

        log.info("POST /api/host/upload/room/batch - {} files", files.size());

        if (files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách file không được rỗng");
        }
        if (files.size() > 10) {
            throw new IllegalArgumentException("Tối đa 10 ảnh mỗi lần upload");
        }

        files.forEach(this::validateImageFile);

        List<String> urls = fileUploadService.uploadRoomImages(files);

        log.info("POST /api/host/upload/room/batch - upload thành công {} ảnh", urls.size());
        return ResponseEntity.ok(ApiResponse.success(urls));
    }

    

        /**
     * Chức năng: Xóa room image.
     * URL: DELETE /api/host/upload/room
     */
@DeleteMapping("/room")
    public ResponseEntity<ApiResponse<Void>> deleteRoomImage(
            @RequestParam String publicId) {

        log.info("DELETE /api/host/upload/room - publicId: {}", publicId);
        fileUploadService.deleteFile(publicId);
        log.info("DELETE /api/host/upload/room - xoá thành công: {}", publicId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    
    
    

        /**
     * Chức năng: Kiểm tra image file.
     * URL: REQUEST /api/host/upload
     */
private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Chỉ chấp nhận file ảnh (image/*). File nhận được: " + contentType);
        }
        
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 5 MB");
        }
    }
}

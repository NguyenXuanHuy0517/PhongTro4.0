package com.project.backend.service.host;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến file upload.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;

    
    
    

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload avatar.
     */
public String uploadAvatar(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/avatars",
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            
                            "transformation", "c_fill,g_face,h_400,w_400,q_auto,f_auto"
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadAvatar thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadAvatar thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload avatar: " + e.getMessage(), e);
        }
    }

    
    
    

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload room image.
     */
public String uploadRoomImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/rooms",
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            
                            "transformation", "w_1280,c_limit,q_auto,f_auto"
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadRoomImage thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadRoomImage thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload ảnh phòng: " + e.getMessage(), e);
        }
    }

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload room images.
     */
public List<String> uploadRoomImages(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadRoomImage(file));
        }
        return urls;
    }

    
    
    

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload document.
     */
public String uploadDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        if (!isValidDocumentType(file.getContentType())) {
            throw new IllegalArgumentException("Định dạng tài liệu không được hỗ trợ");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/documents",
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("uploadDocument thành công: {} → {}", file.getOriginalFilename(), url);
            return url;
        } catch (IOException e) {
            log.error("uploadDocument thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload tài liệu: " + e.getMessage(), e);
        }
    }

    
    
    

    

        /**
     * Chức năng: Xóa file.
     */
public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("deleteFile thành công: {}", publicId);
        } catch (Exception e) {
            log.error("deleteFile thất bại: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi xoá file: " + e.getMessage(), e);
        }
    }

    
    
    

        /**
     * Chức năng: Thực hiện nghiệp vụ is valid document type.
     */
private boolean isValidDocumentType(String contentType) {
        if (contentType == null) return false;
        return contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml")
                || contentType.equals("application/vnd.ms-excel")
                || contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml")
                || contentType.equals("text/plain");
    }
}

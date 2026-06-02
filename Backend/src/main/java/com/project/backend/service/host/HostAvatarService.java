package com.project.backend.service.host;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.backend.entity.User;
import com.project.backend.repository.UserRepository;
import com.project.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến avatar.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HostAvatarService {

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; 

        /**
     * Chức năng: Cập nhật avatar.
     */
public String updateAvatar(Long userId, MultipartFile file) {
        
        validateImageFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        
        String newUrl = uploadToCloudinary(file, userId);

        
        String oldUrl = user.getAvatarUrl();
        if (oldUrl != null && oldUrl.contains("res.cloudinary.com")) {
            try {
                String publicId = extractPublicId(oldUrl);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Xoá avatar cũ thành công: {}", publicId);
            } catch (Exception e) {
                
                log.warn("Không xoá được avatar cũ {}: {}", oldUrl, e.getMessage());
            }
        }

        
        user.setAvatarUrl(newUrl);
        userRepository.save(user);

        return newUrl;
    }

        /**
     * Chức năng: Loại bỏ avatar.
     */
public void removeAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + userId));

        String oldUrl = user.getAvatarUrl();
        if (oldUrl != null && oldUrl.contains("res.cloudinary.com")) {
            try {
                cloudinary.uploader().destroy(extractPublicId(oldUrl), ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.warn("Không xoá được avatar {}: {}", oldUrl, e.getMessage());
            }
        }

        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    

        /**
     * Chức năng: Thực hiện nghiệp vụ upload to cloudinary.
     */
private String uploadToCloudinary(MultipartFile file, Long userId) {
        try {
            Map result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smartroom/avatars",
                            "public_id", "user_" + userId,  
                            "overwrite", true,
                            "resource_type", "image",
                            
                            "transformation", "c_fill,g_face,h_400,w_400,q_auto,f_auto"
                    )
            );
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Upload avatar thất bại cho userId {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Lỗi upload avatar: " + e.getMessage(), e);
        }
    }

        /**
     * Chức năng: Kiểm tra image file.
     */
private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được rỗng");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Chỉ chấp nhận file ảnh (image/*). Nhận được: " + contentType);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Kích thước file không được vượt quá 5 MB. File hiện tại: "
                            + (file.getSize() / 1024 / 1024) + " MB");
        }
    }

    

        /**
     * Chức năng: Trích xuất public id.
     */
private String extractPublicId(String url) {
        
        String[] parts = url.split("/upload/");
        if (parts.length < 2) return url;
        String afterUpload = parts[1];
        
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
        }
        
        int dotIdx = afterUpload.lastIndexOf('.');
        return dotIdx > 0 ? afterUpload.substring(0, dotIdx) : afterUpload;
    }
}

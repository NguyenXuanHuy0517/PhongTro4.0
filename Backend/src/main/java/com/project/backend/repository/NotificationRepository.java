package com.project.backend.repository;

import com.project.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUser_UserIdAndIsRead(Long userId, boolean isRead);
    long countByUser_UserIdAndIsReadFalse(Long userId);

    @Query(
            value = "SELECT n FROM Notification n " +
                    "WHERE n.user.userId = :userId " +
                    "AND (:isRead IS NULL OR n.isRead = :isRead) " +
                    "AND (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(n.body) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(n.type) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(n) FROM Notification n " +
                    "WHERE n.user.userId = :userId " +
                    "AND (:isRead IS NULL OR n.isRead = :isRead) " +
                    "AND (:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(n.body) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(n.type) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Notification> findPageByUserId(
            @Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            @Param("search") String search,
            Pageable pageable
    );
}

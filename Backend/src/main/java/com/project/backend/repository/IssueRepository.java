package com.project.backend.repository;

import com.project.backend.entity.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByRoom_Area_Host_UserId(Long hostId);
    List<Issue> findByRoom_Area_Host_UserIdAndIssueType(Long hostId, String issueType);
    List<Issue> findByTenant_UserId(Long tenantId);
    List<Issue> findByStatus(String status);
    long countByTenant_UserIdAndStatusIn(Long tenantId, List<String> statuses);
    long countByRoom_Area_Host_UserIdAndStatusIn(Long hostId, List<String> statuses);

    @Query("SELECT i FROM Issue i " +
           "JOIN FETCH i.tenant " +
           "JOIN FETCH i.room r " +
           "LEFT JOIN FETCH i.area " +
           "WHERE r.area.host.userId = :hostId")
    List<Issue> findWithRelationsByHostId(@Param("hostId") Long hostId);

    @Query(
            value = "SELECT i FROM Issue i " +
                    "JOIN FETCH i.tenant t " +
                    "JOIN FETCH i.room r " +
                    "LEFT JOIN FETCH i.area a " +
                    "WHERE r.area.host.userId = :hostId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:issueType IS NULL OR i.issueType = :issueType) " +
                    "AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(i) FROM Issue i " +
                    "JOIN i.tenant t " +
                    "JOIN i.room r " +
                    "WHERE r.area.host.userId = :hostId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:issueType IS NULL OR i.issueType = :issueType) " +
                    "AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Issue> findPageWithRelationsByHostId(
            @Param("hostId") Long hostId,
            @Param("status") String status,
            @Param("issueType") String issueType,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT i FROM Issue i " +
           "JOIN FETCH i.tenant " +
           "JOIN FETCH i.room r " +
           "LEFT JOIN FETCH i.area " +
           "WHERE r.area.host.userId = :hostId AND i.issueType = :issueType")
    List<Issue> findWithRelationsByHostIdAndIssueType(
            @Param("hostId") Long hostId,
            @Param("issueType") String issueType
    );

    @Query(
            value = "SELECT i FROM Issue i " +
                    "JOIN FETCH i.tenant t " +
                    "JOIN FETCH i.room r " +
                    "LEFT JOIN FETCH i.area a " +
                    "WHERE t.userId = :tenantId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(i) FROM Issue i " +
                    "JOIN i.tenant t " +
                    "JOIN i.room r " +
                    "WHERE t.userId = :tenantId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:search IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Issue> findPageWithRelationsByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );
}

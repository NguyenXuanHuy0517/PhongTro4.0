package com.project.backend.repository;

import com.project.backend.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByContract_Room_Area_Host_UserId(Long hostId);
    List<Invoice> findByContract_ContractId(Long contractId);
    List<Invoice> findByContract_Tenant_UserId(Long tenantId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStatusIn(List<String> statuses);
    List<Invoice> findByContract_Room_Area_Host_UserIdAndStatus(Long hostId, String status);
    long countByContract_Room_Area_Host_UserIdAndStatus(Long hostId, String status);
    long countByContract_Tenant_UserIdAndStatus(Long tenantId, String status);
    long countByContract_Tenant_UserIdAndStatusIn(Long tenantId, List<String> statuses);
    boolean existsByContract_ContractIdAndBillingMonthAndBillingYear(Long contractId, int month, int year);

    @Query("SELECT i.contract.contractId FROM Invoice i " +
           "WHERE i.contract.contractId IN :contractIds " +
           "AND i.billingMonth = :month " +
           "AND i.billingYear = :year")
    List<Long> findExistingContractIdsForPeriod(
            @Param("contractIds") List<Long> contractIds,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT i FROM Invoice i " +
           "JOIN FETCH i.contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE a.host.userId = :hostId")
    List<Invoice> findWithRelationsByHostId(@Param("hostId") Long hostId);

    @Query(
            value = "SELECT i FROM Invoice i " +
                    "JOIN FETCH i.contract c " +
                    "JOIN FETCH c.tenant t " +
                    "JOIN FETCH c.room r " +
                    "JOIN FETCH r.area a " +
                    "WHERE a.host.userId = :hostId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:month IS NULL OR i.billingMonth = :month) " +
                    "AND (:year IS NULL OR i.billingYear = :year) " +
                    "AND (:search IS NULL OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(i) FROM Invoice i " +
                    "JOIN i.contract c " +
                    "JOIN c.tenant t " +
                    "JOIN c.room r " +
                    "JOIN r.area a " +
                    "WHERE a.host.userId = :hostId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:month IS NULL OR i.billingMonth = :month) " +
                    "AND (:year IS NULL OR i.billingYear = :year) " +
                    "AND (:search IS NULL OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Invoice> findPageWithRelationsByHostId(
            @Param("hostId") Long hostId,
            @Param("status") String status,
            @Param("search") String search,
            @Param("month") Integer month,
            @Param("year") Integer year,
            Pageable pageable
    );

    @Query("SELECT i FROM Invoice i " +
           "JOIN FETCH i.contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE a.host.userId = :hostId AND i.status = :status")
    List<Invoice> findWithRelationsByHostIdAndStatus(
            @Param("hostId") Long hostId,
            @Param("status") String status
    );

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.contract.room.area.host.userId = :hostId " +
           "AND i.status != 'PAID' AND i.dueDate < CURRENT_DATE")
    Long countOverdueByHostId(@Param("hostId") Long hostId);

    @Query(
            value = "SELECT i FROM Invoice i " +
                    "JOIN FETCH i.contract c " +
                    "JOIN FETCH c.room r " +
                    "JOIN FETCH r.area a " +
                    "WHERE c.tenant.userId = :tenantId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:search IS NULL OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(i) FROM Invoice i " +
                    "JOIN i.contract c " +
                    "JOIN c.room r " +
                    "WHERE c.tenant.userId = :tenantId " +
                    "AND (:status IS NULL OR i.status = :status) " +
                    "AND (:search IS NULL OR LOWER(i.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Invoice> findPageWithRelationsByTenantId(
            @Param("tenantId") Long tenantId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status != 'PAID' AND i.dueDate < CURRENT_DATE")
    Long countOverdueInvoices();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = 'PAID'")
    BigDecimal sumRevenueAllTime();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
           "WHERE i.status = 'PAID' AND i.billingMonth = :month AND i.billingYear = :year")
    BigDecimal sumRevenueByPeriod(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i " +
           "WHERE i.contract.room.area.host.userId = :hostId " +
           "AND i.status = 'PAID' " +
           "AND i.billingMonth = :month " +
           "AND i.billingYear = :year")
    BigDecimal sumRevenueByHostAndPeriod(
            @Param("hostId") Long hostId,
            @Param("month") int month,
            @Param("year") int year
    );
}

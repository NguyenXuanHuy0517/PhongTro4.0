package com.project.backend.repository;

import com.project.backend.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByRoom_Area_Host_UserId(Long hostId);
    List<Contract> findByTenant_UserId(Long tenantId);
    Optional<Contract> findByRoom_RoomIdAndStatus(Long roomId, String status);
    Optional<Contract> findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(Long tenantId, String status);
    List<Contract> findByStatusAndEndDateBefore(String status, LocalDate date);
    List<Contract> findByStatus(String status);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.room.area.host.userId = :hostId AND c.status = :status")
    Long countByRoom_Area_Host_UserIdAndStatus(@Param("hostId") Long hostId, @Param("status") String status);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = 'ACTIVE'")
    Long countActiveContracts();

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE a.host.userId = :hostId")
    List<Contract> findWithRelationsByHostId(@Param("hostId") Long hostId);

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE a.host.userId = :hostId AND c.status = :status")
    List<Contract> findWithRelationsByHostIdAndStatus(
            @Param("hostId") Long hostId,
            @Param("status") String status
    );

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE a.areaId = :areaId AND c.status = :status")
    List<Contract> findWithRelationsByAreaIdAndStatus(
            @Param("areaId") Long areaId,
            @Param("status") String status
    );

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE c.tenant.userId = :tenantId")
    List<Contract> findWithRelationsByTenantId(@Param("tenantId") Long tenantId);

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE c.status = :status")
    List<Contract> findWithRelationsByStatus(@Param("status") String status);

    @Query("SELECT DISTINCT c FROM Contract c " +
           "JOIN FETCH c.tenant " +
           "JOIN FETCH c.room r " +
           "JOIN FETCH r.area a " +
           "WHERE c.status = :status AND r.roomId IN :roomIds")
    List<Contract> findWithRelationsByRoomIdsAndStatus(
            @Param("roomIds") List<Long> roomIds,
            @Param("status") String status
    );

    @Query("SELECT COUNT(DISTINCT c.room.roomId) FROM Contract c " +
           "WHERE c.status = 'ACTIVE' " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Invoice i " +
           "  WHERE i.contract.contractId = c.contractId " +
           "  AND i.billingMonth = :month " +
           "  AND i.billingYear = :year" +
           ")")
    long countRoomsWithoutInvoice(@Param("month") int month, @Param("year") int year);
}

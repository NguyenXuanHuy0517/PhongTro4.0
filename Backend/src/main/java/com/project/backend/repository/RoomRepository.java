package com.project.backend.repository;

import com.project.backend.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByArea_AreaId(Long areaId);
    List<Room> findByArea_Host_UserId(Long hostId);
    List<Room> findByArea_AreaIdAndStatus(Long areaId, String status);
    int countByArea_AreaIdAndStatus(Long areaId, String status);
    long countByArea_Host_UserId(Long hostId);

    @Query("SELECT DISTINCT r FROM Room r " +
           "JOIN FETCH r.area a " +
           "LEFT JOIN FETCH a.host " +
           "WHERE a.host.userId = :hostId")
    List<Room> findWithAreaAndHostByHostId(@Param("hostId") Long hostId);

    @Query("SELECT DISTINCT r FROM Room r " +
           "JOIN FETCH r.area a " +
           "LEFT JOIN FETCH a.host " +
           "WHERE a.areaId = :areaId")
    List<Room> findWithAreaAndHostByAreaId(@Param("areaId") Long areaId);

    @Query("SELECT DISTINCT r FROM Room r " +
           "JOIN FETCH r.area a " +
           "LEFT JOIN FETCH a.host")
    List<Room> findAllWithAreaAndHost();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r " +
           "JOIN FETCH r.area a " +
           "LEFT JOIN FETCH a.host " +
           "WHERE r.roomId = :roomId")
    Optional<Room> findByIdForUpdate(@Param("roomId") Long roomId);

    @Query(
            value = "SELECT r FROM Room r " +
                    "JOIN FETCH r.area a " +
                    "LEFT JOIN FETCH a.host " +
                    "WHERE a.host.userId = :hostId " +
                    "AND (:areaId IS NULL OR a.areaId = :areaId) " +
                    "AND (:status IS NULL OR r.status = :status) " +
                    "AND (:search IS NULL OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(a.areaName) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(r) FROM Room r " +
                    "JOIN r.area a " +
                    "WHERE a.host.userId = :hostId " +
                    "AND (:areaId IS NULL OR a.areaId = :areaId) " +
                    "AND (:status IS NULL OR r.status = :status) " +
                    "AND (:search IS NULL OR LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(a.areaName) LIKE LOWER(CONCAT('%', :search, '%')))"
    )
    Page<Room> findPageWithAreaAndHostByHostId(
            @Param("hostId") Long hostId,
            @Param("areaId") Long areaId,
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT r.status, COUNT(r) FROM Room r " +
           "WHERE r.area.host.userId = :hostId " +
           "GROUP BY r.status")
    List<Object[]> countRoomStatusByHost(@Param("hostId") Long hostId);

    @Query("SELECT COUNT(DISTINCT r.roomId) FROM Room r " +
           "JOIN Contract c ON r.roomId = c.room.roomId " +
           "WHERE r.area.host.userId = :hostId " +
           "AND c.status = 'ACTIVE' " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Invoice i " +
           "  WHERE i.contract.contractId = c.contractId " +
           "  AND i.billingMonth = :month " +
           "  AND i.billingYear = :year" +
           ")")
    Long countRoomsWithoutInvoiceByHostId(
            @Param("hostId") Long hostId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status IN ('DEPOSITED', 'RENTED')")
    Long countRentedRooms();

    @Query("SELECT COUNT(r) FROM Room r")
    Long countTotalRooms();

    @Query("SELECT a.host.userId, COUNT(r) " +
           "FROM Room r " +
           "JOIN r.area a " +
           "WHERE a.host.userId IN :hostIds " +
           "GROUP BY a.host.userId")
    List<Object[]> countRoomsByHostIds(@Param("hostIds") List<Long> hostIds);

    @Query(
            value = "SELECT r.room_id, r.room_code, a.area_name, h.full_name, r.status, r.base_price, t.full_name " +
                    "FROM rooms r " +
                    "JOIN motel_areas a ON a.area_id = r.area_id " +
                    "JOIN users h ON h.user_id = a.host_id " +
                    "LEFT JOIN contracts c ON c.room_id = r.room_id AND c.status = 'ACTIVE' " +
                    "LEFT JOIN users t ON t.user_id = c.tenant_id",
            nativeQuery = true
    )
    List<Object[]> findAdminRoomAuditRows();

    @Query(
            value = "SELECT DISTINCT r.room_id, r.room_code, a.area_name, h.full_name, r.status, r.base_price, t.full_name " +
                    "FROM contracts c " +
                    "JOIN rooms r ON r.room_id = c.room_id " +
                    "JOIN motel_areas a ON a.area_id = r.area_id " +
                    "JOIN users h ON h.user_id = a.host_id " +
                    "JOIN users t ON t.user_id = c.tenant_id " +
                    "WHERE c.status = 'ACTIVE' " +
                    "AND NOT EXISTS (" +
                    "  SELECT 1 FROM invoices i " +
                    "  WHERE i.contract_id = c.contract_id " +
                    "  AND i.billing_month = :month " +
                    "  AND i.billing_year = :year" +
                    ")",
            nativeQuery = true
    )
    List<Object[]> findAdminRoomsMissingInvoiceRows(
            @Param("month") int month,
            @Param("year") int year
    );
}

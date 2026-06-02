package com.project.backend.repository;

import com.project.backend.entity.MotelArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MotelAreaRepository extends JpaRepository<MotelArea, Long> {
    List<MotelArea> findByHost_UserId(Long hostId);
    long countByHost_UserId(Long hostId);

    @Query("SELECT a.host.userId, COUNT(a) " +
           "FROM MotelArea a " +
           "WHERE a.host.userId IN :hostIds " +
           "GROUP BY a.host.userId")
    List<Object[]> countAreasByHostIds(@Param("hostIds") List<Long> hostIds);
}

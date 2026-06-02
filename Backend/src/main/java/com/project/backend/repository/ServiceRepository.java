package com.project.backend.repository;

import com.project.backend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByArea_AreaId(Long areaId);
    List<Service> findByArea_AreaIdAndIsActive(Long areaId, boolean isActive);
}

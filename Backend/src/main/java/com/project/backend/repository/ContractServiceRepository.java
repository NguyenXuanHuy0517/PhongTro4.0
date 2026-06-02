package com.project.backend.repository;

import com.project.backend.entity.ContractService;
import com.project.backend.entity.ContractServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractServiceRepository extends JpaRepository<ContractService, ContractServiceId> {
    List<ContractService> findByContract_ContractId(Long contractId);
    List<ContractService> findByContract_ContractIdIn(List<Long> contractIds);
    void deleteByContract_ContractIdAndService_ServiceId(Long contractId, Long serviceId);
}

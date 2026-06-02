package com.project.backend.service.tenant;

import com.project.backend.entity.Contract;
import com.project.backend.entity.ContractService;
import com.project.backend.entity.ContractServiceId;
import com.project.backend.entity.Service;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.ServiceRepository;
import com.project.backend.dto.tenant.service.AddServiceToContractDTO;
import com.project.backend.dto.tenant.service.TenantServiceDTO;
import com.project.backend.dto.tenant.service.UpdateServiceQuantityDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.tenant.TenantServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module tenant-service.
 * Chức năng: Chứa logic xử lý liên quan đến my service.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class MyServiceService {

    private final ServiceRepository serviceRepository;
    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final TenantServiceMapper serviceMapper;

    

        /**
     * Chức năng: Lấy dữ liệu available services.
     */
public List<TenantServiceDTO> getAvailableServices(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));

        Long areaId = contract.getRoom().getArea().getAreaId();

        return serviceRepository.findByArea_AreaIdAndIsActive(areaId, true).stream()
                .map(serviceMapper::toDTO)
                .toList();
    }

    

        /**
     * Chức năng: Lấy dữ liệu contract services.
     */
public List<TenantServiceDTO> getContractServices(Long contractId) {
        List<ContractService> contractServices = contractServiceRepository.findByContract_ContractId(contractId);

        return contractServices.stream()
                .map(serviceMapper::toDTOFromContractService)
                .toList();
    }

    

        /**
     * Chức năng: Thêm service to contract.
     */
public void addServiceToContract(Long tenantId, AddServiceToContractDTO request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + request.getContractId()));

        
        if (!contract.getTenant().getUserId().equals(tenantId)) {
            throw new IllegalArgumentException("Bạn không có quyền quản lý hợp đồng này");
        }

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + request.getServiceId()));

        
        ContractServiceId id = new ContractServiceId(request.getContractId(), request.getServiceId());
        if (contractServiceRepository.existsById(id)) {
            throw new IllegalArgumentException("Dịch vụ này đã được thêm vào hợp đồng");
        }

        ContractService contractService = new ContractService();
        contractService.setId(id);
        contractService.setContract(contract);
        contractService.setService(service);
        contractService.setQuantity(request.getQuantity());
        contractService.setPriceSnapshot(service.getPrice());

        contractServiceRepository.save(contractService);
        log.info("Dịch vụ {} được thêm vào hợp đồng {}", service.getServiceId(), contract.getContractId());
    }

    

        /**
     * Chức năng: Loại bỏ service from contract.
     */
public void removeServiceFromContract(Long tenantId, Long contractId, Long serviceId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));

        
        if (!contract.getTenant().getUserId().equals(tenantId)) {
            throw new IllegalArgumentException("Bạn không có quyền quản lý hợp đồng này");
        }

        ContractServiceId id = new ContractServiceId(contractId, serviceId);
        ContractService contractService = contractServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ không được tìm thấy trong hợp đồng"));

        contractServiceRepository.deleteById(id);
        log.info("Dịch vụ {} được gỡ khỏi hợp đồng {}", serviceId, contractId);
    }

    

        /**
     * Chức năng: Cập nhật service quantity.
     */
public void updateServiceQuantity(Long tenantId, Long contractId, Long serviceId, UpdateServiceQuantityDTO request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng: " + contractId));

        
        if (!contract.getTenant().getUserId().equals(tenantId)) {
            throw new IllegalArgumentException("Bạn không có quyền quản lý hợp đồng này");
        }

        ContractServiceId id = new ContractServiceId(contractId, serviceId);
        ContractService contractService = contractServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dịch vụ không được tìm thấy trong hợp đồng"));

        contractService.setQuantity(request.getQuantity());
        contractServiceRepository.save(contractService);
        log.info("Cập nhật số lượng dịch vụ {} trong hợp đồng {}: {}", serviceId, contractId, request.getQuantity());
    }
}

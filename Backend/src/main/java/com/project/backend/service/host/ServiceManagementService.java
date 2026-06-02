package com.project.backend.service.host;

import com.project.backend.entity.MotelArea;
import com.project.backend.entity.Service;
import com.project.backend.repository.ContractServiceRepository;
import com.project.backend.repository.MotelAreaRepository;
import com.project.backend.repository.ServiceRepository;
import com.project.backend.dto.host.service.ServiceCreateDTO;
import com.project.backend.dto.host.service.ServiceResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.ServiceMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Vai trò: Service xử lý nghiệp vụ của module host-service.
 * Chức năng: Chứa logic xử lý liên quan đến service management.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final MotelAreaRepository areaRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final ServiceMapper serviceMapper;

        /**
     * Chức năng: Lấy dữ liệu services by area.
     */
public List<ServiceResponseDTO> getServicesByArea(Long areaId) {
        return serviceRepository.findByArea_AreaId(areaId).stream()
                .map(service -> {
                    int usageCount = contractServiceRepository.findAll().stream()
                            .filter(cs -> cs.getService().getServiceId().equals(service.getServiceId()))
                            .toList().size();
                    return serviceMapper.toDTO(service, usageCount);
                })
                .toList();
    }

        /**
     * Chức năng: Tạo service.
     */
public ServiceResponseDTO createService(Long areaId, ServiceCreateDTO request) {
        MotelArea area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khu trọ: " + areaId));

        Service service = new Service();
        service.setArea(area);
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnitName(request.getUnitName());
        service.setDescription(request.getDescription());
        service.setActive(true);

        serviceRepository.save(service);
        return serviceMapper.toDTO(service, 0);
    }

        /**
     * Chức năng: Cập nhật service.
     */
public ServiceResponseDTO updateService(Long serviceId, ServiceCreateDTO request) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + serviceId));

        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnitName(request.getUnitName());
        service.setDescription(request.getDescription());

        serviceRepository.save(service);
        return serviceMapper.toDTO(service, 0);
    }

        /**
     * Chức năng: Xóa service.
     */
public void deleteService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ: " + serviceId));
        service.setActive(false);
        serviceRepository.save(service);
    }
}

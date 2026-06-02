package com.project.backend.mapper.tenant;

import com.project.backend.entity.Issue;
import com.project.backend.dto.tenant.issue.IssueResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module tenant-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ issue giữa entity và DTO.
 */
@Component("tenantIssueMapper")
public class IssueMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public IssueResponseDTO toDTO(Issue issue) {
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setIssueId(issue.getIssueId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setRoomCode(issue.getRoom().getRoomCode());
        dto.setPriority(issue.getPriority());
        dto.setStatus(issue.getStatus());
        dto.setHandlerNote(issue.getHandlerNote());
        dto.setRating(issue.getRating());
        dto.setTenantFeedback(issue.getTenantFeedback());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setResolvedAt(issue.getResolvedAt());

        
        dto.setIssueType(issue.getIssueType());
        dto.setSuggestedServiceName(issue.getSuggestedServiceName());
        dto.setSuggestionNote(issue.getSuggestionNote());
        return dto;
    }
}

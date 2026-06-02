package com.project.backend.mapper.host;

import com.project.backend.entity.Issue;
import com.project.backend.dto.host.issue.IssueResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Vai trò: Mapper của module host-service.
 * Chức năng: Chuyển đổi dữ liệu cho nghiệp vụ issue giữa entity và DTO.
 */
@Component("hostIssueMapper")
public class IssueMapper {

        /**
     * Chức năng: Chuyển đổi dto.
     */
public IssueResponseDTO toDTO(Issue issue) {
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setIssueId(issue.getIssueId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setTenantName(issue.getTenant().getFullName());
        dto.setRoomCode(issue.getRoom().getRoomCode());
        dto.setPriority(issue.getPriority());
        dto.setStatus(issue.getStatus());
        dto.setHandlerNote(issue.getHandlerNote());
        dto.setRating(issue.getRating());
        dto.setTenantFeedback(issue.getTenantFeedback());
        dto.setCreatedAt(issue.getCreatedAt());

        
        dto.setIssueType(issue.getIssueType());
        if (issue.getArea() != null) {
            dto.setAreaName(issue.getArea().getAreaName());
            dto.setAreaId(issue.getArea().getAreaId());
        }
        dto.setSuggestedServiceName(issue.getSuggestedServiceName());
        dto.setSuggestionNote(issue.getSuggestionNote());
        return dto;
    }
}

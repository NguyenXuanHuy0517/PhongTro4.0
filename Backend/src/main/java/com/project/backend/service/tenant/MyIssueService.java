package com.project.backend.service.tenant;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Issue;
import com.project.backend.entity.Room;
import com.project.backend.entity.User;
import com.project.backend.repository.ContractRepository;
import com.project.backend.repository.IssueRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.dto.tenant.issue.IssueCreateDTO;
import com.project.backend.dto.tenant.issue.IssueRatingDTO;
import com.project.backend.dto.tenant.issue.IssueResponseDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.tenant.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MyIssueService {

    private static final Set<String> ISSUE_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "priority", "status"
    );

    private final IssueRepository issueRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final IssueMapper issueMapper;

    public List<IssueResponseDTO> getMyIssues(Long tenantId) {
        return issueRepository.findByTenant_UserId(tenantId).stream()
                .map(issueMapper::toDTO)
                .toList();
    }

    public PagedResponse<IssueResponseDTO> getMyIssuesPage(
            Long tenantId,
            String status,
            String search,
            int page,
            int size,
            String sort
    ) {
        Page<Issue> issuePage = issueRepository.findPageWithRelationsByTenantId(
                tenantId,
                normalize(status),
                normalize(search),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), buildSort(sort))
        );
        List<IssueResponseDTO> items = issuePage.getContent().stream()
                .map(issueMapper::toDTO)
                .toList();
        return PagedResponse.from(issuePage, items);
    }

    public IssueResponseDTO createIssue(Long tenantId, IssueCreateDTO request) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay nguoi dung: " + tenantId));

        Room room = contractRepository.findFirstByTenant_UserIdAndStatusOrderByStartDateDesc(tenantId, "ACTIVE")
                .map(contract -> contract.getRoom())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ban khong co hop dong dang hieu luc"));

        Issue issue = new Issue();
        issue.setTenant(tenant);
        issue.setRoom(room);
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setImages(request.getImages());
        issue.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        issue.setStatus("OPEN");
        issue.setIssueType(request.getIssueType() != null ? request.getIssueType() : "GENERAL");

        if ("SERVICE_SUGGESTION".equals(request.getIssueType())) {
            issue.setSuggestedServiceName(request.getSuggestedServiceName());
            issue.setSuggestionNote(request.getSuggestionNote());
            issue.setArea(room.getArea());
        }

        issueRepository.save(issue);
        return issueMapper.toDTO(issue);
    }

    public IssueResponseDTO getIssueDetail(Long issueId, Long tenantId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay khieu nai: " + issueId));

        if (!issue.getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Khong tim thay khieu nai: " + issueId);
        }

        return issueMapper.toDTO(issue);
    }

    public IssueResponseDTO rateIssue(Long issueId, Long tenantId, IssueRatingDTO request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay khieu nai: " + issueId));

        if (!issue.getTenant().getUserId().equals(tenantId)) {
            throw new ResourceNotFoundException("Khong tim thay khieu nai: " + issueId);
        }

        if (!"RESOLVED".equals(issue.getStatus())) {
            throw new IllegalStateException(
                    "Chi co the danh gia khieu nai da duoc giai quyet");
        }

        issue.setRating(request.getRating());
        issue.setTenantFeedback(request.getTenantFeedback());
        issue.setStatus("CLOSED");
        issue.setClosedAt(LocalDateTime.now());

        issueRepository.save(issue);
        return issueMapper.toDTO(issue);
    }

    private Sort buildSort(String sort) {
        String[] sortParts = (sort == null ? "" : sort).split(",", 2);
        String requestedField = sortParts.length > 0 ? sortParts[0].trim() : "";
        String field = ISSUE_SORT_FIELDS.contains(requestedField) ? requestedField : "createdAt";
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

package com.project.backend.service.host;

import com.project.backend.dto.common.PagedResponse;
import com.project.backend.entity.Issue;
import com.project.backend.repository.IssueRepository;
import com.project.backend.dto.host.issue.IssueResponseDTO;
import com.project.backend.dto.host.issue.IssueStatusUpdateDTO;
import com.project.backend.exception.ResourceNotFoundException;
import com.project.backend.mapper.host.IssueMapper;
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
public class IssueService {

    private static final Set<String> ISSUE_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "priority", "status"
    );

    private final IssueRepository issueRepository;
    private final IssueMapper issueMapper;

    public List<IssueResponseDTO> getIssuesByHost(Long hostId) {
        return issueRepository.findWithRelationsByHostId(hostId).stream()
                .map(issueMapper::toDTO)
                .toList();
    }

    public PagedResponse<IssueResponseDTO> getIssuesPage(
            Long hostId,
            String status,
            String issueType,
            String search,
            int page,
            int size,
            String sort
    ) {
        Page<Issue> issuePage = issueRepository.findPageWithRelationsByHostId(
                hostId,
                normalize(status),
                normalize(issueType),
                normalize(search),
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), buildSort(sort))
        );
        List<IssueResponseDTO> items = issuePage.getContent().stream()
                .map(issueMapper::toDTO)
                .toList();
        return PagedResponse.from(issuePage, items);
    }

    public List<IssueResponseDTO> getIssuesByHostAndType(Long hostId, String issueType) {
        return issueRepository.findWithRelationsByHostIdAndIssueType(hostId, issueType).stream()
                .map(issueMapper::toDTO)
                .toList();
    }

    public List<IssueResponseDTO> getServiceSuggestions(Long hostId) {
        return getIssuesByHostAndType(hostId, "SERVICE_SUGGESTION");
    }

    public IssueResponseDTO getIssueDetail(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay khieu nai: " + issueId));
        return issueMapper.toDTO(issue);
    }

    public void updateStatus(Long issueId, IssueStatusUpdateDTO request) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay khieu nai: " + issueId));
        issue.setStatus(request.getStatus());
        issue.setHandlerNote(request.getHandlerNote());
        if ("RESOLVED".equals(request.getStatus())) {
            issue.setResolvedAt(LocalDateTime.now());
        }
        issueRepository.save(issue);
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

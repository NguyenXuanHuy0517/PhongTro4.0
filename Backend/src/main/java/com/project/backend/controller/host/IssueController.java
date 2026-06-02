package com.project.backend.controller.host;

import com.project.backend.dto.common.ApiResponse;
import com.project.backend.dto.common.PagedResponse;
import com.project.backend.dto.host.issue.IssueResponseDTO;
import com.project.backend.dto.host.issue.IssueStatusUpdateDTO;
import com.project.backend.service.host.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/host/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<IssueResponseDTO>>> getIssues(
            @RequestParam Long hostId,
            @RequestParam(required = false) String issueType) {
        List<IssueResponseDTO> result = issueType != null && !issueType.isBlank()
                ? issueService.getIssuesByHostAndType(hostId, issueType)
                : issueService.getIssuesByHost(hostId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<IssueResponseDTO>>> getIssuesPaged(
            @RequestParam Long hostId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                issueService.getIssuesPage(hostId, status, issueType, search, page, size, sort)
        ));
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<ApiResponse<IssueResponseDTO>> getIssueDetail(@PathVariable Long issueId) {
        return ResponseEntity.ok(ApiResponse.success(issueService.getIssueDetail(issueId)));
    }

    @PatchMapping("/{issueId}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long issueId,
            @RequestBody IssueStatusUpdateDTO request) {
        issueService.updateStatus(issueId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

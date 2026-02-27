package com.pms.home.issue.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.pms.config.CustomUserDetails;
import com.pms.issue.service.IssueService;
import com.pms.issue.web.IssueSelectDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class IssueRestController {

    private final IssueService issueService;

    @GetMapping("/{projectCode}/issues")
    public List<IssueSelectDto> getDashboardIssues(
            @PathVariable String projectCode,
            @RequestParam(value = "showOnlyMe", defaultValue = "N") String showOnlyMe,
            @AuthenticationPrincipal CustomUserDetails customUser) {

        IssueSelectDto selectDto = new IssueSelectDto();
        String userId = customUser.getUserEntity().getUserId();
        
        // 🚨 일반 유저가 'all'을 누를 때 쿼리에서 본인 프로젝트만 나오도록 
        // 서비스에서 추가 처리가 필요할 수 있습니다. 여기서는 우선 null로 넘깁니다.
        if ("all".equals(projectCode)) {
            selectDto.setProjectCode(null); 
        } else {
            selectDto.setProjectCode(projectCode);
        }

        // '내 것만' 체크 시 userId 세팅
        if ("Y".equals(showOnlyMe)) {
            selectDto.setUserId(userId);
        }

        // ✅ 수정된 서비스 메서드 호출
        return issueService.selectDashboardIssueList(selectDto);
    }
}
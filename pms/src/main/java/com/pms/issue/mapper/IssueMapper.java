package com.pms.issue.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.pms.issue.web.IssueDto;
import com.pms.issue.web.IssueSelectDto;

@Mapper
public interface IssueMapper {

	List<IssueSelectDto> selectIssue(IssueSelectDto issueSelectDto);
	// 일감 상태 목록 조회
	List<IssueDto> selectIssueStatus(IssueDto issueDto);
	// 일감 유형 목록 조회
	List<IssueDto> selectIssueType(IssueDto issueDto);
	// 일감 우선순위 목록 조회
	List<IssueDto> selectIssuePriority(IssueDto issueDto);
	// 일감 담당자 선택
	List<IssueDto> selectIssueManager(IssueDto issueDto);
	// 상위 일감 조회
	List<IssueDto> selectParentIssue(IssueDto issueDto);
	
	Integer insertIssue(IssueDto issueDto);

}
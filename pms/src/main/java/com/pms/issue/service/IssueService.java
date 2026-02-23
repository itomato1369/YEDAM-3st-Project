package com.pms.issue.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.pms.files.service.FilesUploadService;
import com.pms.issue.mapper.IssueMapper;
import com.pms.issue.web.IssueDto;
import com.pms.issue.web.IssueSelectDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {

	private final FilesUploadService filesUploadService;
	private final IssueMapper issueMapper;

	// 일감 리스트 전체 조회 + 조건 검색기능
	public List<IssueSelectDto> findIssueList(IssueSelectDto issueSelectDto) {
		List<IssueSelectDto> issueList = issueMapper.selectIssue(issueSelectDto);
		System.out.println(issueList.toString());
		return issueList;
	}

	// 일감 등록 화면에 필요한 데이터들을 각각 조회
	// 일감 상태
	public List<IssueDto> getStatusList(IssueDto issueDto) {
		return issueMapper.selectIssueStatus(issueDto);
	}
	// 일감 유형
	public List<IssueDto> getTypeList(IssueDto issueDto) {
		return issueMapper.selectIssueType(issueDto);
	}
	// 우선순위
	public List<IssueDto> getPriorityList(IssueDto issueDto) {
		return issueMapper.selectIssuePriority(issueDto);
	}
	// 프로젝트에 소속된 멤버 조회
	public List<IssueDto> getManagerList(IssueDto issueDto) {
		return issueMapper.selectIssueManager(issueDto);
	}
	// 상위 일감 조회
	public List<IssueDto> getParentIssueList(IssueDto issueDto) {
		return issueMapper.selectParentIssue(issueDto);
	}

	// 일감 등록
	@Transactional
	public Integer addIssue(IssueDto issueDto, List<MultipartFile> files) {
		if (!StringUtils.hasText(issueDto.getTitle())) {
			throw new RuntimeException("제목은 필수 입력 사항입니다.");
		}

		try {
			String userId = issueDto.getUserId();
			Integer filesNo = filesUploadService.uploadFiles(files, userId);
			issueDto.setFilesNo(filesNo);
		} catch (Exception e) {
			throw new RuntimeException("파일 저장 중 에러가 발생하였습니다.");
		}

		issueMapper.insertIssue(issueDto);
		Integer jobNo = issueDto.getJobNo();

		return jobNo;
	}

}

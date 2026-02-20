package com.pms.work;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pms.work.dto.WorkSelectDto;
import com.pms.work.mapper.WorkMapper;
import com.pms.work.service.impl.WorkServiceImpl;

@ExtendWith(MockitoExtension.class)
public class WorkTest {
	
	// Mock  하고 클래스 가져오고 Test 랑 DisplayName
	// 시작 	System.out.println("[Mock]  TEST START");
	
	// 종료 System.out.println("[Mock] TEST SUCCESS");

	@Mock
	private WorkMapper workMapper; // 가짜 객체 생성
	
	@InjectMocks
	private WorkServiceImpl workService; // 가짜 서비스 주입
	
	@BeforeEach
	void setUp() {
		// 각 테스트 시작 전에 실행 
		System.out.println("[Mock]  TEST START");
	}

	
	@Test
	@DisplayName("소요시간 전체 조회 테스트")
        void findWorkEntriesTest() {
		WorkSelectDto dto = new WorkSelectDto();
		dto.setProjectCode("PMS100");
		
		List<WorkSelectDto> mockList = new ArrayList<>();
		// 가짜 결과 데이터 추가
		mockList.add(new WorkSelectDto()); 
		// 매퍼 호출되면 가짜 List 리턴
		given(workMapper.selectWorkEntries(any(WorkSelectDto.class))).willReturn(mockList);
		
		// when
		List<WorkSelectDto> result = workService.findAllWorkEntries(dto);
		// then
		assert !result.isEmpty();
		verify(workMapper).selectWorkEntries(dto);
		System.out.println("[Mock]   소요시간 전체 조회 테스트 END");
	}

	
}

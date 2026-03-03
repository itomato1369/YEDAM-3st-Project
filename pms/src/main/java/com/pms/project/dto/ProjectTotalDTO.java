package com.pms.project.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@ToString 
@Getter 
@Setter
@Alias("ProjectTotalDTO")
public class ProjectTotalDTO {
	private Integer totalMemberCount;  // 하위 포함 프로젝트 총 인원
    private Integer totalJobCount;     // 하위 포함 프로젝트 총 일감
    private Double  targetProgress; // 예상 진척도 - project start date - end date
    private Double  currentProgress;    // 실제 진척도 - project => job start date - end date
}

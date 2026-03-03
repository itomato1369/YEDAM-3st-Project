package com.pms.project.dto;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Alias("JobDTO")
public class JobDTO {
    private Integer jobNo;
    private Integer projectNo;
    private String  title;
    private Double  progress;    // 실제 진행률 (0~100)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date    startDate;   // 일감 시작일
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date    endDate;     // 일감 종료일
    private Integer jobStatusNo; // 상태 (삭제 여부 확인용)
}
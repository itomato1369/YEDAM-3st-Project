package com.pms.project.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Alias("MemberDTO")
public class MemberDTO {
    private Integer projectNo;
    private String  userId;
    private String  userName;
    private Integer userStatus; // 권한 등급
}
package com.pms.project.dto;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Alias("ProjectGMemberDTO")
public class ProjectGMemberDTO {
	private String groupname;
	private String username;
}
package com.pms.setting.projects.entity;

import java.time.LocalDateTime;

import com.pms.setting.common.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PROJECT")
@Getter
@Setter
public class ProjectsEntity {

    @Id
    @Column(name = "PROJECT_NO")
    private Long projectNo;

    @Column(name = "PROJECT_NAME")
    private String projectName;	

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS") // DB의 STATUS 컬럼이 COMMON_NO를 가리킴
    private CommonEntity status; 

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @Column(name = "PUBLIC_YN")
    private Integer publicYn;
}
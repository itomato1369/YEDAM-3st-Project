package com.pms.files.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pms.files.entity.FilesEntity;

public interface FilesRepository extends JpaRepository<FilesEntity, Integer> {

}

package com.pms.home.notice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pms.files.service.FilesDeleteService;
import com.pms.files.service.FilesUploadService;
import com.pms.home.notice.dto.HomeNoticeDto;
import com.pms.home.notice.mapper.HomeNoticeMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class HomeNoticeRestController {

    private final HomeNoticeMapper noticeMapper;
    private final FilesUploadService filesUploadService; // 파일 업로드 서비스 주입
    private final FilesDeleteService filesDeleteService;

    // 1. 상세 조회 (모달 데이터용)
    @GetMapping("/{noticeNo}")
    public ResponseEntity<HomeNoticeDto> getNotice(@PathVariable Integer noticeNo) {
        HomeNoticeDto notice = noticeMapper.getNotice(noticeNo);
        return notice != null ? ResponseEntity.ok(notice) : ResponseEntity.notFound().build();
    }

    // 2. 공지사항 등록 (Multipart/FormData 대응)
    @PostMapping
    @Transactional
    public ResponseEntity<?> createNotice(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("privacySettings") Integer privacySettings,
            @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails user) {
        
        Integer filesNo = null;

        try {
            // 파일이 존재할 경우 업로드 서비스 호출
            if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                filesNo = filesUploadService.uploadFiles(files, user.getUsername(), filesNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 저장 실패: " + e.getMessage());
        }

        // Record 생성 (인자 순서 주의: noticeNo, title, content, privacySettings, userId, filesNo, addDate)
        HomeNoticeDto newDto = new HomeNoticeDto(
            null, 
            title, 
            content, 
            privacySettings, 
            user.getUsername(), 
            filesNo, 
            null
        );
        
        int result = noticeMapper.insertNotice(newDto);
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    // 3. 공지사항 수정 (파일 업로드 없이 텍스트만 수정하는 기존 로직 유지 혹은 확장 가능)
    @PutMapping("/{noticeNo}")
    @Transactional
    public ResponseEntity<?> updateNotice(
            @PathVariable Integer noticeNo,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("privacySettings") Integer privacySettings,
            @RequestParam(value = "uploadFiles", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails user) {

        // 기존 공지 정보 가져오기 (파일 번호 유지를 위함)
        HomeNoticeDto existingNotice = noticeMapper.getNotice(noticeNo);
        if (existingNotice == null) return ResponseEntity.notFound().build();
        
        Integer filesNo = existingNotice.filesNo(); // 기본적으로 기존 파일 번호 유지

        try {
            // 새로운 파일이 들어왔을 경우에만 업로드 진행
            if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                filesNo = filesUploadService.uploadFiles(files, user.getUsername(), filesNo);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("파일 수정 실패");
        }

        HomeNoticeDto updateDto = new HomeNoticeDto(
            noticeNo, 
            title, 
            content, 
            privacySettings, 
            user.getUsername(), 
            filesNo, 
            null
        );
        
        int result = noticeMapper.updateNotice(updateDto);
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.status(403).body("수정 권한이 없습니다.");
    }

    // 4. 공지사항 삭제
    @DeleteMapping("/{noticeNo}")
    public ResponseEntity<?> deleteNotice(@PathVariable Integer noticeNo,
                                        @AuthenticationPrincipal UserDetails user) {
        int result = noticeMapper.deleteNotice(noticeNo, user.getUsername());
        return result > 0 ? ResponseEntity.ok().build() : ResponseEntity.status(403).body("삭제 권한이 없습니다.");
    }
    
    @DeleteMapping("/files/{detailsNo}")
    public ResponseEntity<?> deleteFileIndividual(
            @PathVariable Integer detailsNo,
            @AuthenticationPrincipal UserDetails user) { // 유저 정보 추가
    	System.out.println("🚩 요청 도달 성공! 삭제할 번호: " + detailsNo); // 이 로그가 찍히는지 확인!
        try {            
            filesDeleteService.deleteFileDetail(detailsNo); 
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the file.");
        }
    }
}
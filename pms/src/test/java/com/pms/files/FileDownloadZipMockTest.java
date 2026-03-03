package com.pms.files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.pms.config.CustomUserDetails;
import com.pms.files.entity.FilesDetailsEntity;
import com.pms.files.repository.FilesDetailsRepository;
import com.pms.files.service.FilesUploadService;
import com.pms.user.entity.UserEntity;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FileDownloadZipMockTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FilesUploadService filesUploadService; 
	
	@Autowired
	private FilesDetailsRepository filesDetailsRepository;
	
	@Autowired
	private AmazonS3 amazonS3;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	
	private CustomUserDetails createCustomUser(String userId, boolean isAdmin) {
		UserEntity user = UserEntity.builder()
									.userId(userId)
									.passwd("1234")
									.admin(isAdmin)
									.build();
        return new CustomUserDetails(user);
    }

	@Test
	@WithMockUser
	@DisplayName("다중 파일 Zip 압축 다운로드 및 복호화 테스트 확인")
	public void fileDownloadZipMockTest() throws Exception{
		Integer fileNo = preEncFile();
		
		// 저장된 파일 정보 불러오기
		List<FilesDetailsEntity> details = filesDetailsRepository.findByFilesEntity_FilesNo(fileNo);
		String ids = details.stream()
							.map(d -> d.getDetailsNo().toString())
							.collect(Collectors.joining(","));
		
		// 컨트롤러
		MvcResult result = mockMvc.perform(get("/download/zip")
											.with(user(createCustomUser("song", false)))
											.with(csrf())
											.param("detailsNos", ids))
											.andExpect(status().isOk())
											.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/zip"))
							                .andReturn();
		
		// 결과
		byte[] zipBytes = result.getResponse().getContentAsByteArray();
		
		try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
			ZipEntry entry;
			int cnt = 0;
			while ((entry = zis.getNextEntry()) != null) {
				cnt++;
				byte[] content = zis.readAllBytes();
				String text = new String(content, StandardCharsets.UTF_8);
				
				assertTrue(text.contains("Test Data"), "내용이 일치하지 않습니다.");
				zis.closeEntry();
			}
			assertEquals(3, cnt, "파일 개수가 맞지 않습니다.");
		}
	}

	// 저장
	private Integer preEncFile() throws Exception {
		String userId = "testUser";
		
		MockMultipartFile file1 = new MockMultipartFile("files", "test_01.png", "image/png", "Test Data 1".getBytes());
	    MockMultipartFile file2 = new MockMultipartFile("files", "test_02.jpg", "image/jpeg", "Test Data 2".getBytes());
	    List<MultipartFile> files = List.of(file1, file2);
	    
	    Integer filesNo = filesUploadService.uploadFiles(files, userId, null);

	    MockMultipartFile file3 = new MockMultipartFile("files", "test_03.jpg", "image/jpeg", "Test Data 3".getBytes());
	    filesNo = filesUploadService.uploadFiles(List.of(file3), userId, filesNo);
	    
		return filesNo;
	}
	
	// 삭제
	@AfterEach
	public void cleanUpS3() {
	    // 테스트에서 생성된 파일들을 조회하여 S3에서 삭제
	    List<FilesDetailsEntity> allDetails = filesDetailsRepository.findAll();
	    for (FilesDetailsEntity detail : allDetails) {
	        try {
	            amazonS3.deleteObject(bucket, detail.getFilesUuid());
	        } catch (Exception e) {
	            System.err.println("S3 파일 삭제 실패: " + detail.getFilesUuid());
	        }
	    }
	}
}

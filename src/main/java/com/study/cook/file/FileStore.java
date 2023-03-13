package com.study.cook.file;

import com.study.cook.domain.Photo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {
    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }   // 전체 경로

    // 파일 리스트로 저장하기
    public List<Photo> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<Photo> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    // 단일 파일 저장하기
    public Photo storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();  // 업로드 파일명
        String storeFileName = createStoreFileName(originalFilename);   // 서버 파일명 생성
        multipartFile.transferTo(new File(getFullPath(storeFileName))); // 서버에 파일 저장

        return new Photo(originalFilename, storeFileName);
    }

    // 서버에 저장하는 파일명 생성
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);  // 확장자명
        String uuid = UUID.randomUUID().toString(); // 유일한 이름 생성
        return uuid + "." + ext;    // 파일명 + 확장자명
    }

    // 확장자명 변환
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public void deleteFile(String filename) {
        String fullPath = getFullPath(filename);
        File file = new File(fullPath);
        if(file.exists()) {
            file.delete();
        }
    }
}
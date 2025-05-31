package com.kyj.fmk.core.file.s3;

import com.kyj.fmk.core.cst.dto.ResApiDTO;
import com.kyj.fmk.core.cst.enm.FileType;
import com.kyj.fmk.core.file.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * 2025-05-30
 * @author 김용준
 * S3파일 업로드,삭제,다운로드하기 위한 서비스
 */
@Service
public class S3FileService implements FileService {

    @Value("${s3.credentials.bucket}")
    private String bucket;

    private static final long MAX_FILE_SIZE = 5* 1024 * 1024 ;

    @Override
    public void upload(MultipartFile file) {
    }

    @Override
    public void delete(String fileAddress) {

    }

    @Override
    public ResponseEntity<ResApiDTO<Resource>> download(String fileAddress) {
        return null;
    }
}

package com.kyj.fmk.core.file;

import com.kyj.fmk.core.cst.dto.ResApiDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
/**
 * 2025-05-30
 * @author 김용준
 * S3파일 업로드,삭제,다운로드하기 위한 서비스
 */
public class S3FileService implements FileService{
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

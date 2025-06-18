package com.kyj.fmk.core.file;

import com.kyj.fmk.core.model.enm.FileType;
import org.springframework.web.multipart.MultipartFile;

public final class FileVailidator {




    public static boolean valid(MultipartFile file,FileType fileType, long maxSize){

    return false;
    }

    private static boolean validMaxSize(MultipartFile file , long maxSize){
       long fileSize= file.getSize();

        return fileSize <= maxSize;
    }

    private static boolean validExtension(FileType fileType , String extension){

        return fileType.supports(extension);
    }

}


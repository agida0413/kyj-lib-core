package com.kyj.fmk.core.exception.custom;

import lombok.Setter;

@Setter
public class KyjBaseException extends RuntimeException{
    private String msg;

    public KyjBaseException(String msg) {
        super(msg);
        // 상수 에러코드
        this.msg=msg;
    }
}

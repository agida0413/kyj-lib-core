package com.kyj.fmk.core.cst.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseResDTO <T> {
    private int status;
    private String msg;
    private T data;
    private LocalDateTime resTime;

}

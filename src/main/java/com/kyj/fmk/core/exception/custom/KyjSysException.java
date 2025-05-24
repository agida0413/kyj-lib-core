package com.kyj.fmk.core.exception.custom;

public class KyjSysException extends KyjBaseException{
    public KyjSysException(String msg){
        super(msg);
        this.setMsg(msg);

    }
}

package com.kyj.fmk.core.exception.custom;

public class KyjBizException extends KyjBaseException{

    public KyjBizException(String msg){
        super(msg);
        this.setMsg(msg);

    }

}

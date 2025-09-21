package com.kyj.core.exception.custom;

import com.kyj.core.api.ErrCode;

/**
 * 2025-05-29
 * @author 김용준
 * Restful Api에서 사용하는 비즈니스 Exception
 */
public class KyjBizException extends KyjBaseException{
    /**
     *
     * @param apiErrCode
     */
    public KyjBizException(ErrCode apiErrCode){
        super(apiErrCode);
        this.setCode(apiErrCode.getCode());
    }

    /**
     *
     * @param errCode
     * @param msg
     */
    public KyjBizException(ErrCode errCode, String msg){
        super(errCode,msg);
        this.setMsg(msg);
        this.setCode(errCode.getCode());
    }

}

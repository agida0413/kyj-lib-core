package com.kyj.fmk.core.exception.handler;

import com.kyj.fmk.core.model.dto.ResApiErrDTO;
import com.kyj.fmk.core.model.enm.ApiErrCode;
import com.kyj.fmk.core.exception.custom.KyjBaseException;
import com.kyj.fmk.core.exception.custom.KyjBizException;
import com.kyj.fmk.core.exception.custom.KyjSysException;
import org.springframework.http.HttpStatus;

/**
 * 2025-05-30
 * @author 김용준
 * Restful Api에서 사용하는 에러응답객체에 대한 메시지 혹은 상태값 등을 결정해주는 Helper클래스이다.
 */
public class ErrHelper {


    /**
     * 전체에러응답객체값을 세팅해준다.
     * @param ex
     * @return
     */
    public static ResApiErrDTO<Void> determineErrRes(Exception ex){
        String msg = determineErrMsg(ex);
        HttpStatus status = determineErrStatus(ex);

        ResApiErrDTO<Void> resApiErrDTO = new ResApiErrDTO(msg,status.value());

        return resApiErrDTO;
    }
    /**
     * 에러메시지를 결정해준다
     * @param ex
     * @return String
     */
    public static String determineErrMsg(Exception ex ){
        String code = "";
        String msg = "";
        if(ex instanceof KyjBaseException){
            ex = (KyjBaseException)ex;
            code =  ((KyjBaseException) ex).getCode();

            if(code.equals(ApiErrCode.CM001.getCode())){
                return ((KyjBaseException) ex).getMsg();
            }else{
                msg = ApiErrCode.of(code);

            }
        }
        return msg;
    }



    /**
     * 에러스테이터스를 결정해준다.
     * @param ex
     * @return HttpStatus
     */
    private static HttpStatus determineErrStatus(Exception ex  ){

        if(ex instanceof KyjBaseException){
            if(ex instanceof KyjBizException){
                return HttpStatus.BAD_REQUEST;
            } else if (ex instanceof KyjSysException) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }else{
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}

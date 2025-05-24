package com.kyj.fmk.core.cst.dto;

import com.kyj.fmk.core.cst.OutPutConst;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ResApiErrDTO <T> extends BaseResDTO{

    public ResApiErrDTO(String msg){
        this.setMsg(msg);
    }
    public ResApiErrDTO(String msg, T data){
        this.setMsg(msg);
        this.setData(data);
    }

    public ResApiErrDTO(String msg, int status, T data){
        this.setMsg(msg);
        this.setData(data);
        this.setStatus(status);
    }

    private void setDefaultVal(){
        this.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        this.setResTime(LocalDateTime.now());
    }
}

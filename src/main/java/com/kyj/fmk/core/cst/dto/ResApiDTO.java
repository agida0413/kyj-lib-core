package com.kyj.fmk.core.cst.dto;

import com.kyj.fmk.core.cst.OutPutConst;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ResApiDTO <T> extends BaseResDTO{

    private ResApiDTO(){
        setDefaultVal();
    }

    public ResApiDTO (T data){
        setDefaultVal();
        this.setData(data);
    }

    private void setDefaultVal(){
        this.setStatus(HttpStatus.OK.value());
        this.setResTime(LocalDateTime.now());
        this.setMsg(OutPutConst.SucMsg);
    }
}

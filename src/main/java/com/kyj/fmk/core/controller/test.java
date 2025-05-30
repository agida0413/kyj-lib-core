package com.kyj.fmk.core.controller;

import com.kyj.fmk.core.cst.dto.ResApiDTO;
import com.kyj.fmk.core.cst.enm.ApiErrCode;
import com.kyj.fmk.core.exception.custom.KyjBizException;
import com.kyj.fmk.core.exception.custom.KyjSysException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class test {

    @RequestMapping("/test")
    public ResponseEntity<ResApiDTO<Void>> test(){

        return ResponseEntity
                .ok()
                .body(new ResApiDTO<Void>());
    }


    @RequestMapping("/test2")
    public ResponseEntity<ResApiDTO<Map>> test2(){
Map<String,String> map = new HashMap<>();
map.put("ttt","123123");
        return ResponseEntity
                .ok()
                .body(new ResApiDTO<Map>(map));
    }


    @RequestMapping("/test3")
    public ResponseEntity<ResApiDTO<Map>> test3(){
        throw new KyjSysException(ApiErrCode.CM001,"시스템오류");
    }
}

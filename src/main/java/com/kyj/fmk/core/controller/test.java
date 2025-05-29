package com.kyj.fmk.core.controller;

import com.kyj.fmk.core.cst.dto.ResApiDTO;
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
}

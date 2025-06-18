package com.kyj.fmk.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final RedisTemplate<String,Object> redisTemplate;

    @Cacheable(value = "tests", key = "#param")
    public Map cachI(String param){
        Map<Object,Object> map = new HashMap<>();
        map.put("test","!23");
        map.put("test2","1235");


        return map;
    }


    @CacheEvict(value = "tests", key = "#param")
    public String cachD(String param){
        String str =  (String)redisTemplate.opsForValue().get("tests:1235");
        log.info("str={}",str);

        return "ok";
    }
}

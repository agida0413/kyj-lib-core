package com.kyj.core.mail;

import com.kyj.core.api.CmErrCode;
import com.kyj.core.exception.custom.KyjSysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2025-10-03
 * @author 김용준
 * 메일 템플릿을 생성한다.
 */
@Slf4j
public class MailFactory {

    private static final ConcurrentHashMap<String,String> staticTemplate;

    static {
        ConcurrentHashMap<String,String> stringStringHashMap = new ConcurrentHashMap<>();
        stringStringHashMap.put("example","<h1>example</h1>");
        staticTemplate= stringStringHashMap;
    }


    /**
     * 동적 바인딩 템플릿 생성
     * @param html
     * @return
     */
    public static String generateTemplate(String html){
        if(!StringUtils.hasText(html)){
            log.error("동적 메일 생성할 html이 NULL입니다.");
            throw new KyjSysException(CmErrCode.CM002);
        }
        return html;
    }

    /**
     * 정적 템플릿 생성(캐시)
     * @param key
     * @param html
     * @return
     */
    public static String generateTemplate(String key,String html) {


         if(!StringUtils.hasText(key) || !StringUtils.hasText(html)){
             log.error("정적 메일 생성할 html이 NULL이거나 key가 NULL 입니다.");
             throw new KyjSysException(CmErrCode.CM002);
         }


         String returnTemplate =  staticTemplate.get(key);

         if(StringUtils.hasText(returnTemplate)){
            return returnTemplate;
         }

        if(staticTemplate.size()<1000){
            staticTemplate.put(key,html);

        }else{
            log.error("정적메일 MAP의 최대용량을 초과하였습니다.");
            throw new KyjSysException(CmErrCode.CM002);
        }

            return html;

    }


}

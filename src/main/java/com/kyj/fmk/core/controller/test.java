package com.kyj.fmk.core.controller;

import com.kyj.fmk.core.controller.mapper.Repo;
import com.kyj.fmk.core.controller.mapper.TestMapper;
import com.kyj.fmk.core.model.dto.ResApiDTO;
import com.kyj.fmk.core.model.enm.ApiErrCode;
import com.kyj.fmk.core.exception.custom.KyjSysException;
import com.kyj.fmk.core.mail.MailSender;
import com.kyj.fmk.core.util.CookieUtil;
import com.kyj.fmk.core.util.RandomGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class test {

   private final  MailSender mailSender;
//    private final FileService fileService;
    private final RedisTemplate<String ,Object> redisTemplate;
    private final Repo repo;
private final TestService testService;
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


    @RequestMapping("/test4")
    public ResponseEntity<ResApiDTO<Map>> test4(HttpHeaders httpHeaders){

     CookieUtil.createCookie("test","111",3000, "/",httpHeaders);

        return ResponseEntity.ok().
        headers(httpHeaders).body(null);
    }


    @RequestMapping("/test5")
    public String test4(HttpServletRequest request){


     String object=  (String) CookieUtil.getCookie("test",request);
    log.trace("cookie={}",object);
        return object;
    }

    @RequestMapping("/test6")
    public  ResponseEntity<ResApiDTO<Map>>  test6(HttpHeaders httpHeaders){

        CookieUtil.deleteCookie("test",httpHeaders,"/");
        return ResponseEntity.ok().
                headers(httpHeaders).body(null);
    }


    @RequestMapping("/test7")
    public  ResponseEntity<ResApiDTO<?>>  test7(){

    String str = RandomGenerator.generateRandomNumber(10);

     ResApiDTO<String> apiDTO = new ResApiDTO(str);
        return ResponseEntity
                .ok()
                .body(apiDTO);
    }


//    @RequestMapping("/test8")
//    public  ResponseEntity<ResApiDTO<?>>  test8(MultipartFile file){
//        FileType [] fileType = {FileType.DOCUMENT};
//    //  String url =   fileService.upload(file, fileType);
//        fileService.delete("https://kyjs345.s3.ap-northeast-2.amazonaws.com/da75c512-80.%20%E1%84%86%E1%85%A9%E1%86%A8%E1%84%8E%E1%85%A1.pdf");
//      //log.info("url = {}",url);
////            throw new KyjSysException(ApiErrCode.CM006,"DDDDDDD");
//        return ResponseEntity
//                .ok()
//                .body(null);
//    }

//    @RequestMapping("/test9")
//    public  ResponseEntity<byte[]>  test8(String file){
//      return fileService.download(file);
//    }



    @RequestMapping("/test10")
    public  ResponseEntity<ResApiDTO<Map>>  test10(@RequestParam String param){
        Map<Object,Object> map = testService.cachI(param);
        return ResponseEntity.ok(new ResApiDTO<>(map));
    }




    @RequestMapping("/test11")
    public  String test11(@RequestParam String param){

    return testService.cachD(param);
    }

    @RequestMapping("/test15")
    public  String test15(){
        try {
            Test2DTO str= repo.selectTest();
            return  str.getI33();
        } catch (Exception e) {
            log.info("e={}",e);
            log.info("e={}",e.getMessage());
            log.info("e={}",e.getCause());
            log.info("e={}",e.fillInStackTrace());
        }


    return "k";
    }
}

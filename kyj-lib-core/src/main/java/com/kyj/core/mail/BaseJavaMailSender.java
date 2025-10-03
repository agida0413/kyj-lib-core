package com.kyj.core.mail;

import com.kyj.core.async.annotation.AsyncVoidInvoke;
import com.kyj.core.api.CmErrCode;
import com.kyj.core.exception.custom.KyjSysException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 2025-05-30
 * @author 김용준
 * Restful Api에서 사용하는 메일전송클래스이다.
 */
@Slf4j
@RequiredArgsConstructor
public class BaseJavaMailSender implements CustomMailSender {

    private final JavaMailSender javaMailSender;


    @Override
    @AsyncVoidInvoke
    public void send(MailDTO mailDTO) {

        MimeMessage message= javaMailSender.createMimeMessage(); //메일링 객체 생성

        try {
            MimeMessageHelper mimeMessageHelper= new MimeMessageHelper(message,true);

            //정적 메일 생성, 동적생성 구분

            String template = null;

            if(mailDTO instanceof DynamicMailDTO){
               DynamicMailDTO dynamicMailDTO = (DynamicMailDTO)mailDTO;
               template = MailFactory.generateTemplate(dynamicMailDTO.getHtml());
            } else if (mailDTO instanceof StaticMailDTO) {
                StaticMailDTO staticMailDTO = (StaticMailDTO)mailDTO;
                template = MailFactory.generateTemplate(staticMailDTO.getKey(),staticMailDTO.getHtml());
            }else{
                log.error("메일DTO 형식이 올바르지 않습니다.");
                throw new KyjSysException(CmErrCode.CM002);
            }


            mimeMessageHelper.setTo(mailDTO.getReceiverEmail());//보낼 상대
            mimeMessageHelper.setSubject(mailDTO.getSubject()); //제목
            mimeMessageHelper.setText(template,true);

            javaMailSender.send(message);//전송
        } catch (MessagingException e) {
            throw new KyjSysException(CmErrCode.CM005);
        }

    }
}

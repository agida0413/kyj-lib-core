package com.kyj.core.mail;

import lombok.*;

/**
 * 동적 메일 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamicMailDTO extends MailDTO {

    public DynamicMailDTO(String html,String subject,String receiver){
        setHtml(html);
        setSubject(subject);
        setReceiverEmail(receiver);
    }

}

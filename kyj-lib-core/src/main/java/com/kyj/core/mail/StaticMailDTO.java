package com.kyj.core.mail;

import lombok.*;

/**
 * 정적 메일 DTO
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StaticMailDTO extends MailDTO{
    private String key;

    public StaticMailDTO(String html,String subject,String receiver,String key){
        setHtml(html);
        setSubject(subject);
        setReceiverEmail(receiver);
        setKey(key);
    }
}

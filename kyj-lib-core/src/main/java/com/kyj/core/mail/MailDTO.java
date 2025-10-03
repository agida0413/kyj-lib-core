package com.kyj.core.mail;

import lombok.Getter;
import lombok.Setter;

/**
 * 기본 메일DTO
 */
@Getter
@Setter
public abstract class MailDTO {

    private String subject;
    private String html;
    private String receiverEmail;


}

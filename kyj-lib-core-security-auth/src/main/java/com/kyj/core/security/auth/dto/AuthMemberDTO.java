package com.kyj.core.security.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthMemberDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private String nickname;
    private String provider;
    private String providerId;
    private boolean active = true;
}
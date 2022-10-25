package com.example.api_gateway.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Builder
public class ConnValidatorResponse {

    private String username;
    private List<GrantedAuthority> authorities;
    private String code;
    private Boolean isAuthenticated;
}

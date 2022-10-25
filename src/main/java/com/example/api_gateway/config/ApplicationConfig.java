package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean(name = "excludedUrls")
    List<String> excludedUrls() {
        List<String> noSecurityPath = new ArrayList<>();
        noSecurityPath.add("/api/auth/login");
        noSecurityPath.add("/mediaService/api/image/upload");
        noSecurityPath.add("/mediaService/api/image/**");
        return noSecurityPath;
    }
}

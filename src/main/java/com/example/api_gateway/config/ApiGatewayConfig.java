package com.example.api_gateway.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ApiGatewayConfig {

    private String authorizationKey = "Authorization";

    private String authenPrefix = "Bearer";

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    /**
     * Authen SERVICE
     */
    private String authenServicePath = "/api/auth/**";
    private String authenServiceUri = "lb://MICROSERVICE-AUTHEN-SERVICE";

    /**
     * Driver SERVICE
     */
    private String driverServicePath = "/driverService/api/**";
    private String driverServiceUri = "lb://VTIOBYS-DRIVER-SERVICE";

    /**
     * Vehicle SERVICE
     */
    private String kitSetServicePath = "/vehicleService/api/**";
    private String kitSetServiceUri = "lb://VTIOBYS-VEHICLE-SERVICE";


    /**
     * Operator SERVICE
     */
    private String operatorServicePath = "/api/operator/**";
    private String operatorServiceUri = "lb://VTIOBYS-OPERATOR-SERVICE";

    /**
     * FACE SERVICE
     */
    private String faceServicePath = "/api/face/**";
    private String faceServiceUri = "lb://OBYS-SAAS-FACE-SERVICE";

    /**
     * MEDIA SERVICE
     */
    private String mediaServicePath = "/mediaService/api/**";
    private String mediaServiceUri = "lb://VTIOBYS-MEDIA-SERVICE";

    /**
     * Login
     */
    private String authenLoginPath = "/api/auth/login";
    private String authenVerifyTokenPath = "/api/auth/verify-token";
    private String authenRefreshTokenPath = "/api/auth/refresh-token";
    private String authenResetPwdPath = "/api/auth/reset-pwd";
    private String authenRegisterUserPath = "/api/auth/users";

}

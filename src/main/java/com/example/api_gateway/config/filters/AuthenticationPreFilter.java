package com.example.api_gateway.config.filters;

import com.example.api_gateway.config.ApiGatewayConfig;
import com.example.api_gateway.constants.SecurityConstants;
import com.example.api_gateway.response.ConnValidatorResponse;
import com.example.api_gateway.response.ExceptionResponseModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationPreFilter extends
    AbstractGatewayFilterFactory<ApiGatewayConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationPreFilter.class);

    @Resource(name = "excludedUrls")
    private List<String> excludedUrls;

    @Value("${authentication.base.uri}")
    private String authenticationBaseUri;

    private final WebClient.Builder webClientBuilder;

    @Resource
    private ObjectMapper objectMapper;

    public AuthenticationPreFilter(WebClient.Builder webClientBuilder) {
        super(ApiGatewayConfig.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(ApiGatewayConfig apiGatewayConfig) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            LOGGER.info("************************************************************************");
            LOGGER.info("URL is - " + request.getURI().getPath());
            String bearerToken = request.getHeaders().getFirst(SecurityConstants.HEADER);
            LOGGER.info("Bearer Token: " + bearerToken);

            if (isSecured.test(request)) {
                LOGGER.info("URI {}", authenticationBaseUri + "/api/auth/validateToken");
                return webClientBuilder.build().get()
                    .uri(authenticationBaseUri + "/api/auth/validateToken")
                    .header(SecurityConstants.HEADER, bearerToken)
                    .retrieve().bodyToMono(ConnValidatorResponse.class)
                    .map(response -> {
                        String authorities = "";
                        if (response.getAuthorities() != null) {
                            response.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .reduce("", (a, b) -> a + "," + b);
                        }
                        exchange.getRequest().mutate().header("username", response.getUsername());
                        exchange.getRequest().mutate().header("authorities", authorities);

                        return exchange;
                    }).flatMap(chain::filter).onErrorResume(error -> {
                        LOGGER.info("Error Happened");
                        HttpStatus errorCode = null;
                        String errorMsg = "";
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException webCLientException = (WebClientResponseException) error;
                            errorCode = webCLientException.getStatusCode();
                            errorMsg = webCLientException.getStatusText();

                        } else {
                            errorCode = HttpStatus.BAD_GATEWAY;
                            errorMsg = HttpStatus.BAD_GATEWAY.getReasonPhrase();
                        }
                        // AuthorizationFilter.AUTH_FAILED_CODE
                        return onError(exchange, String.valueOf(errorCode.value()), errorMsg,
                            "JWT Authentication Failed", errorCode);
                    });
            }

            return chain.filter(exchange);
        };
    }

    public Predicate<ServerHttpRequest> isSecured = request -> excludedUrls.stream()
        .noneMatch(uri -> request.getURI().getPath().contains(uri));

    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err,
        String errDetails, HttpStatus httpStatus) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        try {
            response.getHeaders().add("Content-Type", "application/json");
            ExceptionResponseModel data = new ExceptionResponseModel(errCode, err, errDetails, null,
                new Date());
            byte[] byteData = objectMapper.writeValueAsBytes(data);
            return response.writeWith(Mono.just(byteData).map(t -> dataBufferFactory.wrap(t)));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response.setComplete();
    }
}

package com.example.api_gateway;

import com.example.api_gateway.config.ApiGatewayConfig;
import com.example.api_gateway.config.filters.AuthenticationPreFilter;


import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ApiGatewayApplication {

	@Resource
	ApiGatewayConfig apiGatewayConfig;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RouteLocator myRoute(RouteLocatorBuilder routeLocatorBuilder,
						 AuthenticationPreFilter authFilter) {
		return routeLocatorBuilder.routes()
				.route(p ->
						p.path(apiGatewayConfig.getAuthenServicePath())
								.filters(f -> f.filter(authFilter.apply(apiGatewayConfig)))
								.uri(apiGatewayConfig.getAuthenServiceUri()))
				.route(p ->
						p.path(apiGatewayConfig.getOperatorServicePath())
								.filters(f -> f.filter(authFilter.apply(apiGatewayConfig)))
								.uri(apiGatewayConfig.getOperatorServiceUri()))
				.route(p ->
						p.path(apiGatewayConfig.getDriverServicePath())
								.filters(f -> f.filter(authFilter.apply(apiGatewayConfig)))
								.uri(apiGatewayConfig.getDriverServiceUri()))
				.route(p ->
						p.path(apiGatewayConfig.getMediaServicePath())
								.filters(f -> f.filter(authFilter.apply(apiGatewayConfig)))
								.uri(apiGatewayConfig.getMediaServiceUri()))
				.route(p ->
						p.path(apiGatewayConfig.getKitSetServicePath())
								.filters(f -> f.filter(authFilter.apply(apiGatewayConfig)))
								.uri(apiGatewayConfig.getKitSetServiceUri()))
				.build();
	}

}

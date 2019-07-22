package com.yucong.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FeignApplicationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignApplicationServiceApplication.class, args);
	}
	
}

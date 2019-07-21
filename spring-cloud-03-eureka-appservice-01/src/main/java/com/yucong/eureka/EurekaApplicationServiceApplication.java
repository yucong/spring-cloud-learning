package com.yucong.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EurekaApplicationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplicationServiceApplication.class, args);
	}
	
}

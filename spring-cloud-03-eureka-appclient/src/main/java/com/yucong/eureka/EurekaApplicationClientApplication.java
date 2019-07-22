package com.yucong.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
public class EurekaApplicationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplicationClientApplication.class, args);
	}
	
	//加入负载均衡能力
    //同时可根据applicationName 来访问服务
    //如http://EUREKA-CLIENT/test
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
}

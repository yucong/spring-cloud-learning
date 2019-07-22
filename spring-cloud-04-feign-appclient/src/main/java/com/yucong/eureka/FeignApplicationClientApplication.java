package com.yucong.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @EnableFeignClients - 启动FeignClient技术。
 * 开启Feign的应用。
 * 
 * @EnableDiscoveryClient - 启动发现机制。
 * 就是辅助Feign技术，发现服务，定义服务动态代理的辅助技术。
 * 
 * @EnableEurekaClient 注解删除。是使用Discovery来发现服务的。discovery是辅助feign技术的一个发现客户端。
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FeignApplicationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignApplicationClientApplication.class, args);
	}
	
}

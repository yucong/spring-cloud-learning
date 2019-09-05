package com.yucong.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @EnableCircuitBreaker - 开启断路器。就是开启hystrix服务容错能力。
 * 当应用启用Hystrix服务容错的时候，必须增加的一个注解。
 */
@EnableCircuitBreaker
/**
 * @EnableCaching - 开启spring cloud对cache的支持。
 * 可以自动的使用请求缓存，访问redis等cache服务。
 */
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
@EnableHystrix
@EnableHystrixDashboard
public class HystrixApplicationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixApplicationClientApplication.class, args);
	}
	
}

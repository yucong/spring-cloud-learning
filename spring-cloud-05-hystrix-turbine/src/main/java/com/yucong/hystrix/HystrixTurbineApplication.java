package com.yucong.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * @EnableTurbine - 开启Turbine功能。
 *  可以实现收集多个App client的Dashboard监控数据。
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableTurbine
public class HystrixTurbineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixTurbineApplication.class, args);
	}
	
}

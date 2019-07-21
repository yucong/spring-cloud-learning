package com.yucong.eureka.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 因为是测试案例，所以所有的代码都定义在了Controller中。
 * 商业开发中，业务逻辑和远程调用Provider的代码逻辑应该封装在Service代码中。
 * 现在的代码编写方式，只是为了看起来方便。
 * 
 * 在这里开发Eureka Client中的Application Client角色。就是consumer服务的消费者。
 * 服务消费者需要在注册中心中发现服务列表的。且同时将自己注册到注册中心的服务列表中。
 * 
 * consumer在消费provider的时候，是通过LoadBalancer来实现的。
 * LoadBalancer后续课程中详细讲解。
 * LoadBalancer简介 ： 是Eureka client内置的一个负载均衡器。复杂在发现的服务列表中选择服务应用，获取服务的IP和端口。
 * 实现服务的远程调用。
 * 
 * application client代码开发相比较dubbo的consumer开发麻烦很多。
 * 
 */
@RestController
public class TestApplicationClientController2 {

	/**
	 * ribbon负载均衡器，其中记录了从Eureka Server中获取的所有服务信息。 
	 * 这些服务的信息是IP和端口等。应用名称，域名，主机名等信息。
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 通过HTTP协议，发起远程服务调用，实现一个远程的服务消费。
	 */
	@GetMapping("test")
	public List<Map<String, Object>> test() {

		/*
		 * 创建一个响应类型模板。
		 * 就是REST请求的响应体中的数据类型。
		 * ParameterizedTypeReference - 代表REST请求的响应体中的数据类型。
		 */
		ParameterizedTypeReference<List<Map<String, Object>>> type = 
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
		};

		/*
		 * ResponseEntity:封装了返回值信息，相当于是HTTP Response中的响应体。
		 * 发起REST请求。
		 */
		ResponseEntity<List<Map<String, Object>>> response = 
				restTemplate.exchange("http://eureka-application-service/test", HttpMethod.GET, null, type);
		/*
		 * ResponseEntity.getBody() - 就是获取响应体中的java对象或返回数据结果。
		 */
		List<Map<String, Object>> result = response.getBody();

		return result;
	}

}

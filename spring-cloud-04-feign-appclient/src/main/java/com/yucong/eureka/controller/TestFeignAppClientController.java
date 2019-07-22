package com.yucong.eureka.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.api.pojo.FeignTestPOJO;
import com.yucong.eureka.service.FirstClientFeignService;

/**
 * Application Client中的控制器。是和用户直接交互的控制器。
 * 像平时开发代码一样。调用本地的一个service接口。通过service接口远程访问Application Service。
 */
@RestController
public class TestFeignAppClientController {

	/**
	 * 本地定义的服务接口。用于实现远程调用application  service的接口。
	 */
	@Autowired
	private FirstClientFeignService service;
	
	/**
	 * 无参
	 */
	@GetMapping("/testFeign")
	public List<String> testFeign(){
		System.out.println(this.service.getClass().getName());
		return this.service.testFeign();
	}
	
	/**
	 * 一个参数，调用远程服务的时候，发起的请求是GET请求
	 */
	@GetMapping("/get/{id}")
	public FeignTestPOJO getById(@PathVariable("id") Long id){
		return this.service.getById(id);
	}
	
	/**
	 * 一个参数，调用远程服务的时候，发起的请求是POST请求
	 */
	@GetMapping("/get")
	public FeignTestPOJO getByIdWithPOST(Long id){
		return this.service.getByIdWithPOST(id);
	}
	
	/**
	 * 多个参数，调用远程服务的时候，发起的请求是GET请求
	 */
	@GetMapping("/add/{id}/{name}")
	public FeignTestPOJO add(@PathVariable("id") Long id, @PathVariable("name") String name){
		return this.service.add(id, name);
	}
	
	/**
	 * 自定义类型参数，调用远程服务的时候，发起的请求是POST请求
	 * feign技术远程访问application service的时候，默认情况下GET请求不能传递自定义类型参数
	 */
	@GetMapping("/add")
	public FeignTestPOJO add(FeignTestPOJO pojo){
		// return this.service.add(pojo);
		return this.service.addWithPOST(pojo);
	}
	
}

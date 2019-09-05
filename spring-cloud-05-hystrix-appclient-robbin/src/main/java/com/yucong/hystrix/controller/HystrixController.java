package com.yucong.hystrix.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.hystrix.service.HystrixService;

@RestController
public class HystrixController {

	@Autowired
	private HystrixService service;

	/**
	 * 服务降级
	 */
	@GetMapping("/testDowngrade")
	public List<Map<String, Object>> testDowngrade() {
		return this.service.testDowngrade();
	}
	
	@GetMapping("/testCache4Get")
	public List<Map<String, Object>> testCache4Get(){
		return this.service.testCache4Get();
	}
	
	@GetMapping("/testCache4Del")
	public List<Map<String, Object>> testCache4Del(){
		return this.service.testCache4Del();
	}
	
	/**
	 * 在一个控制器方法中多次调用服务逻辑。用来模拟高并发。
	 */
	@GetMapping("/testMergeRequest")
	public void testMergeRequest() throws InterruptedException, ExecutionException{
		Future<Map<String, Object>> r1 = this.service.testMergeRequest(1L);
		Future<Map<String, Object>> r2 = this.service.testMergeRequest(5L);
		Future<Map<String, Object>> r3 = this.service.testMergeRequest(10L);
		
		System.out.println("r1 : " + r1.get());
		System.out.println("r2 : " + r2.get());
		System.out.println("r3 : " + r3.get());
		
		Thread.sleep(50);

		Future<Map<String, Object>> r4 = this.service.testMergeRequest(10L);
		System.out.println("r4 : " + r4.get());
	}
	
	@GetMapping("/testBreaker")
	public List<Map<String, Object>> testBreaker() {
		return this.service.testBreaker();
	}
	
	@GetMapping("/testThreadQuarantine")
	public List<Map<String, Object>> testThreadQuarantine() {
		return this.service.testThreadQuarantine();
	}
	
	@GetMapping("/testSemaphoreQuarantine")
	public List<Map<String, Object>> testSemaphoreQuarantine() {
		return this.service.testSemaphoreQuarantine();
	}
	
	@GetMapping("/testThreadName")
	public void testThreadName() {
		this.service.testThreadName();
	}

}

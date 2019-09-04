package com.yucong.eureka.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.api.pojo.FeignTestPOJO;
import com.yucong.eureka.service.FirstClientFeignService;

@RestController
public class TestFeignAppClientController {

	@Autowired
	private FirstClientFeignService service;
	
	@GetMapping("/testFeign")
	public List<String> testFeign(){
		return this.service.testFeign();
	}
	
	@GetMapping("/get/{id}")
	public FeignTestPOJO getById(@PathVariable("id") Long id){
		FeignTestPOJO p1 = this.service.getById(2L);
		FeignTestPOJO p2 = this.service.getById(3L);
		FeignTestPOJO p3 = this.service.getById(4L);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		FeignTestPOJO p4 = this.service.getById(5L);
		System.out.println(p1);
		System.out.println(p2);
		System.out.println(p3);
		System.out.println(p4);
		return this.service.getById(id);
	}
	
	@GetMapping("/get")
	public FeignTestPOJO getByIdWithPOST(Long id){
		return this.service.getByIdWithPOST(id);
	}
	
	@GetMapping("/add/{id}/{name}")
	public FeignTestPOJO add(@PathVariable("id") Long id, @PathVariable("name") String name){
		return this.service.add(id, name);
	}
	
	@GetMapping("/add")
	public FeignTestPOJO add(FeignTestPOJO pojo){
		// return this.service.add(pojo);
		return this.service.addWithPOST(pojo);
	}
	
}

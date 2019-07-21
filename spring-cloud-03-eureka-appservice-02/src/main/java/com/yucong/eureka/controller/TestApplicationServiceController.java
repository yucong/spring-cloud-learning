package com.yucong.eureka.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestApplicationServiceController {

	@RequestMapping(value="/test")
	@ResponseBody
	public List<Map<String, Object>> test(){
		
		List<Map<String, Object>> result = new ArrayList<>();
		
		for(int i = 4; i < 7; i++){
			Map<String, Object> data = new HashMap<>();
			data.put("id", i+1);
			data.put("name", "test name " + i);
			data.put("age", 20+i);
			
			result.add(data);
		}
		
		return result;
	}
	
	@RequestMapping(value="/testMerge")
	@ResponseBody
	public List<Map<String, Object>> test(Long[] ids){
		System.out.println(Thread.currentThread().getName() + " --------- " + Arrays.toString(ids));
		List<Map<String, Object>> result = new ArrayList<>();
		
		for(Long id : ids){
			Map<String, Object> data = new HashMap<>();
			data.put("id", id);
			data.put("name", "test name " + id);
			data.put("age", 20+id);
			
			result.add(data);
		}
		
		return result;
	}
	
	@RequestMapping("/toShutdown")
	public String toShutdown(){
		return "shutdown";
	}
	
}

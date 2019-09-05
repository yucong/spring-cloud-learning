package com.yucong.eureka.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yucong.api.pojo.FeignTestPOJO;

/**
 * 如果在Feign中使用Hystrix，则不能直接继承服务标准接口。
 * 因为继承接口，一般都不会给予实现。会缺少fallback方法。熔断机制链条不完整。
 * 在当前接口中，重复定义服务标准接口中定义的方法。
 * 远程服务调用的时候，是通过@FeignClient实现的。
 * 如果远程服务调用失败，则触发fallback注解属性定义的接口实现类中的对应方法，作为fallback方法。
 * 
 * 在默认的Hystrix配置环境中，使用的是服务降级保护机制。
 * 
 * 服务降级，默认的情况下，包含了请求超时。
 * feign声明式远程服务调用，在启动的时候，初始化过程比较慢。比ribbon要慢很多。
 * 很容易在第一次访问的时候，产生超时。导致返回fallback数据。
 */
@FeignClient(name="test-feign-application-service",
			// fallback=FirstClientFeignServiceImpl.class
			fallbackFactory=FirstClientFeignServiceFallbackFactory.class
		)
public interface FirstClientFeignService{

	@RequestMapping(value="/testFeign", method=RequestMethod.GET)
	public List<String> testFeign();
	
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public FeignTestPOJO getById(@RequestParam(value="id") Long id);
	
	@RequestMapping(value="/get", method=RequestMethod.POST)
	public FeignTestPOJO getByIdWithPOST(@RequestBody Long id);
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public FeignTestPOJO add(@RequestParam("id") Long id, @RequestParam("name") String name);
	
	@RequestMapping(value="/addWithGET", method=RequestMethod.GET)
	public FeignTestPOJO add(@RequestBody FeignTestPOJO pojo);
	
	@RequestMapping(value="/addWithPOST", method=RequestMethod.POST)
	public FeignTestPOJO addWithPOST(@RequestBody FeignTestPOJO pojo);
	
}

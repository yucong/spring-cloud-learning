package com.yucong.eureka.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yucong.api.pojo.FeignTestPOJO;
import com.yucong.api.service.FirstFeignService;

/**
 * 自定义的服务控制器
 * 对外提供服务的Application Service。
 * 不能随便的定义服务了。如果想让Application Client可以通过Feign技术访问当前类型提供的服务，
 * 则必须遵循服务标准。
 */
@RestController
public class TestFeignAppServiceController implements FirstFeignService {

	/**
	 * 因为当前的方法都是实现接口FirstFeignService中的方法。
	 * 而在接口中，已经将请求URL和方法耦合到一起了。
	 * 所以在当前的控制器中，不能重复定义@RequestMapping来约束请求URL。
	 */
	@Override
	public List<String> testFeign() {
		
		List<String> result = new ArrayList<>();
		
		result.add("test feign");
		result.add("this is first spring cloud with feign");
		
		return result;
	}

	@Override
	public FeignTestPOJO getById(Long id) {
		return new FeignTestPOJO(id, "getById");
	}
	
	/**
	 * 如果方法参数是处理POST请求的JSON数据的。
	 * 那么还是需要定义@RequestBody注解来描述方法参数的。
	 */
	@Override
	public FeignTestPOJO getByIdWithPOST(@RequestBody Long id) {
		return new FeignTestPOJO(id, "getByIdWithPOST");
	}

	@Override
	public FeignTestPOJO add(Long id, String name) {
		System.out.println( "add(Long id, String name)" );
		return new FeignTestPOJO(id, name);
	}
	
	/**
	 * 在默认的情况下，Feign不能通过GET请求传递自定义类型的请求参数。
	 */
	@Override
	public FeignTestPOJO add(@RequestBody FeignTestPOJO pojo) {
		System.out.println( "add(@RequestBody FeignTestPOJO pojo)" );
		return pojo;
	}

	@Override
	public FeignTestPOJO addWithPOST(@RequestBody FeignTestPOJO pojo) {
		System.out.println( "addWithPOST(@RequestBody FeignTestPOJO pojo)" );
		return pojo;
	}
	
}

package com.yucong.eureka.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yucong.api.pojo.FeignTestPOJO;

import feign.hystrix.FallbackFactory;

/**
 * 使用Factory方式实现Feign的Hystrix容错处理。
 * 编写的自定义Factory必须实现接口FallbackFactory。
 * FallbackFactory中的方法是
 *  服务接口的类型 create(Throwable 远程服务调用的错误)
 * 
 * 工厂实现方案和服务接口实现类实现方案的区别：
 *  工厂可以提供自定义的异常信息处理逻辑。因为create方法负责传递远程服务调用的异常对象。
 *  实现类可以快速的开发，但是会丢失远程服务调用的异常信息。
 */
@Component
public class FirstClientFeignServiceFallbackFactory implements FallbackFactory<FirstClientFeignService> {

	Logger logger = LoggerFactory.getLogger(FirstClientFeignServiceFallbackFactory.class);
	
	/**
	 * create方法 - 就是工厂的生产产品的方法。
	 *  当前工厂生产的产品就是服务接口的Fallback处理对象。 就是服务接口的实现类的对象。
	 */
	@Override
	public FirstClientFeignService create(final Throwable cause) {
		
		return new FirstClientFeignService() {
			@Override
			public List<String> testFeign() {
				logger.warn("testFeign() - ", cause);
				List<String> result = new ArrayList<>();
				result.add("this is testFeign method fallback datas");
				return result;
			}

			@Override
			public FeignTestPOJO getById(Long id) {
				return new FeignTestPOJO(-1L, "this is getById method fallback datas");
			}

			@Override
			public FeignTestPOJO getByIdWithPOST(Long id) {
				return new FeignTestPOJO(-1L, "this is getByIdWithPOST method fallback datas");
			}

			@Override
			public FeignTestPOJO add(Long id, String name) {
				return new FeignTestPOJO(-1L, "this is add(id, name) method fallback datas");
			}

			@Override
			public FeignTestPOJO add(FeignTestPOJO pojo) {
				return new FeignTestPOJO(-1L, "this is add(pojo) method fallback datas");
			}

			@Override
			public FeignTestPOJO addWithPOST(FeignTestPOJO pojo) {
				return new FeignTestPOJO(-1L, "this is addWithPOST method fallback datas");
			}
		};
	}

}

package com.yucong.eureka.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.yucong.api.pojo.FeignTestPOJO;
import com.yucong.eureka.service.FirstClientFeignService;

/**
 * 实现类中的每个方法，都是对应的接口方法的fallback。
 * 一定要提供spring相关注解（@Component/@Service/@Repository等）。
 * 注解是为了让当前类型的对象被spring容器管理。
 * fallback是本地方法。
 * 是接口的实现方法。
 */
@Component
public class FirstClientFeignServiceImpl implements FirstClientFeignService {

	@Override
	public List<String> testFeign() {
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

}

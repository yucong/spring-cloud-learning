package com.yucong.eureka.service;

import org.springframework.cloud.openfeign.FeignClient;

import com.yucong.api.service.FirstFeignService;

/**
 * 本地接口，继承服务标准接口。
 * 在接口上增加注解@FeignClient，代表当前接口是一个Feign技术中的客户端。
 * 需要发起远程的http请求。
 * 注解有属性name - 代表当前的FeignClient在请求application service的时候，是调用哪一个服务？
 * 所谓的哪一个服务，就是application service全局配置文件中的spring.application.name属性值。
 */
@FeignClient(name="test-feign-application-service")
public interface FirstClientFeignService extends FirstFeignService {

}

package com.yucong.api.service;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yucong.api.pojo.FeignTestPOJO;

/**
 * 微服务标准。
 * 是Application Service要提供的服务的标准。
 * 也是Application Client要调用远程服务的标准。
 * 就是一个普通的接口。
 */
public interface FirstFeignService {
	
	/**
	 * 测试GET请求的方法。
	 * 请求不传递任何的参数。
	 * 请求地址是 - /testFeign  -> http://ip:port/testFeign
	 * @return
	 */
	@RequestMapping(value="/testFeign", method=RequestMethod.GET)
	public List<String> testFeign();
	
	/**
	 * 测试GET请求传递一个普通的参数。  /get?id=xxx
	 * 在为Feign定义服务标准接口的时候，处理请求参数的方法参数，必须使用@RequestParam注解描述。
	 * 且，无论方法参数名和请求参数名是否一致，都需要定义@RequestParam注解的value/name属性。
	 * @return
	 */
	@RequestMapping(value="/get", method=RequestMethod.GET)
	public FeignTestPOJO getById(@RequestParam(value="id") Long id);
	
	/**
	 * 测试使用POST请求传递一个普通参数
	 * 在Feign技术中，默认的发起POST请求的时候，请求的参数，都是在请求体中使用JSON数据传递的。
	 * 不是name=value对传递的。
	 * 必须使用@RequestBody注解来解析请求体中的数据。
	 * 
	 * 如果使用POST方式发起请求，传递多个普通参数，是使用请求头传递的参数。可以使用@RequestParam注解来处理请求参数。
	 * POST请求的请求体类型还是application/json。feign会通过请求头传递多个请求参数： /xxx?a=xxx&b=xxx&c=xxx
	 * @return
	 */
	@RequestMapping(value="/get", method=RequestMethod.POST)
	public FeignTestPOJO getByIdWithPOST(@RequestBody Long id);
	
	/**
	 * 使用GET请求传递多个普通参数。 /add?id=xxx&name=xxx
	 * 必须使用@RequestParam注解处理请求参数。
	 * @return
	 */
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public FeignTestPOJO add(@RequestParam("id") Long id, @RequestParam("name") String name);
	
	/**
	 * 错误案例
	 * 使用GET请求传递特殊参数。自定义类型的参数。
	 * 在Feign发起的默认的请求中，GET请求方式不能传递自定义类型数据。只能通过POST请求传递。
	 * @return
	 */
	@RequestMapping(value="/addWithGET", method=RequestMethod.GET)
	public FeignTestPOJO add(@RequestBody FeignTestPOJO pojo);
	
	/**
	 * 使用POST请求传递特殊参数。自定义类型的参数。
	 * 默认环境中，只要是Feign发起的POST请求，请求参数都是JSON数据。
	 * 必须使用@RequestBody处理。
	 * @return
	 */
	@RequestMapping(value="/addWithPOST", method=RequestMethod.POST)
	public FeignTestPOJO addWithPOST(@RequestBody FeignTestPOJO pojo);
	
}

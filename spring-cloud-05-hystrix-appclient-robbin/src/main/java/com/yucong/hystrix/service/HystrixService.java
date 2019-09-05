package com.yucong.hystrix.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;

/**
 * 在类上，增加@CacheConfig注解，用来描述当前类型可能使用cache缓存。
 * 如果使用缓存，则缓存数据的key的前缀是cacheNames。
 * cacheNames是用来定义一个缓存集的前缀命名的，相当于分组。
 */
@CacheConfig(cacheNames={"test.hystrix.cache"})
@Service
public class HystrixService {

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	/**
	 * 服务降级处理。
	 * 当前方法远程调用application service服务的时候，如果service服务出现了任何错误（超时，异常等）
	 * 不会将异常抛到客户端，而是使用本地的一个fallback（错误返回）方法来返回一个托底数据。
	 * 避免客户端看到错误页面。
	 * 使用注解来描述当前方法的服务降级逻辑。
	 * 
	 * @HystrixCommand - 开启Hystrix命令的注解。代表当前方法如果出现服务调用问题，使用Hystrix逻辑来处理。
	 * 重要属性 - fallbackMethod 错误返回方法名。
	 * 如果当前方法调用服务，远程服务出现问题的时候，
	 * 调用本地的哪个方法得到托底数据。
	 *  
	 * Hystrix会调用fallbackMethod指定的方法，获取结果，并返回给客户端。
	 */
	@HystrixCommand(fallbackMethod="downgradeFallback")
	public List<Map<String, Object>> testDowngrade() {
		System.out.println("testDowngrade method : " + Thread.currentThread().getName());
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	/**
	 * fallback方法。本地定义的。用来处理远程服务调用错误时，返回的基础数据。
	 */
	@SuppressWarnings("unused")
	private List<Map<String, Object>> downgradeFallback(){
		List<Map<String, Object>> result = new ArrayList<>();
		
		Map<String, Object> data = new HashMap<>();
		data.put("id", -1);
		data.put("name", "downgrade fallback datas");
		data.put("age", 0);
		
		result.add(data);
		
		return result;
	}
	
	/**
	 * 请求缓存处理方法。
	 * 使用注解@Cacheable描述方法。配合启动器中的相关注解，实现一个请求缓存逻辑。
	 * 将当期方法的返回值缓存到cache中。
	 * 属性 value | cacheNames - 代表缓存到cache的数据的key的一部分。
	 * 可以使用springEL来获取方法参数数据，定制特性化的缓存key。
	 * 只要方法增加了@Cacheable注解，每次调用当前方法的时候，spring cloud都会先访问cache获取数据，
	 * 如果cache中没有数据，则访问远程服务获取数据。远程服务返回数据，先保存在cache中，再返回给客户端。
	 * 如果cache中有数据，则直接返回cache中的数据，不会访问远程服务。
	 * 
	 * 请求缓存会有缓存数据不一致的可能。
	 * 缓存数据过期、失效、脏数据等情况。
	 * 一旦使用了请求缓存来处理幂等性请求操作。则在非幂等性请求操作中必须管理缓存。避免缓存数据的错误。
	 * @return
	 */
	@Cacheable("testCache4Get")
	public List<Map<String, Object>> testCache4Get() {
		System.out.println("testCache4Get method thread name : " + Thread.currentThread().getName());
		ServiceInstance si =  this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	/**
	 * 非幂等性操作。用于模拟删除逻辑。
	 * 一旦非幂等性操作执行，则必须管理缓存。就是释放缓存中的数据。删除缓存数据。
	 * 使用注解@CacheEvict管理缓存。
	 * 通过数据cacheNames | value来删除对应key的缓存。
	 * 删除缓存的逻辑，是在当前方法执行结束后。
	 * @return
	 */
	@CacheEvict("testCache4Get")
	public List<Map<String, Object>> testCache4Del() {
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	/**
	 * 需要合并请求的方法。
	 * 这种方法的返回结果一定是Future类型的。
	 * 这种方法的处理逻辑都是异步的。
	 * 是application client在一定时间内收集客户端请求，或收集一定量的客户端请求，一次性发给application service。
	 * application service返回的结果，application client会进行二次处理，封装为future对象并返回
	 * future对象需要通过get方法获取最终的结果。 get方法是由控制器调用的。所以控制器调用service的过程是一个异步处理的过程。
	 * 合并请求的方法需要使用@HystrixCollapser注解描述。
	 * batchMethod - 合并请求后，使用的方法是什么。如果当前方法有参数，合并请求后的方法参数是当前方法参数的集合。
	 * scope - 合并请求的请求作用域。可选值有global和request。
	 *     global代表所有的请求线程都可以等待可合并。 常用
	 *     request代表一个请求线程中的多次远程服务调用可合并
	 * collapserProperties - 细致配置。就是配置合并请求的特性。如等待多久，如可合并请求的数量。
	 *     属性的类型是@HystrixProperty类型数组，可配置的属性值可以直接通过字符串或常量类定义。
	 *     timerDelayInMilliseconds - 等待时长
	 *     maxRequestsInBatch - 可合并的请求最大数量。
	 * 
	 * 方法处理逻辑不需要实现，直接返回null即可。
	 * 合并请求一定是可合并的。也就是同类型请求。同URL的请求。
	 * @param id
	 * @return
	 */
	@HystrixCollapser(batchMethod = "mergeRequest", 
			scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL,  
    		collapserProperties = {  
    		// 请求时间间隔在20ms之内的请求会被合并为一个请求,默认为10ms
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "20"),
            // 设置触发批处理执行之前，在批处理中允许的最大请求数。
            @HystrixProperty(name = "maxRequestsInBatch", value = "200"),  
    })  
	public Future<Map<String, Object>> testMergeRequest(Long id){
		return null;
	}
	
	/**
	 * 批量处理方法。就是合并请求后真实调用远程服务的方法。
	 * 必须使用@HystrixCommand注解描述，代表当前方法是一个Hystrix管理的服务容错方法。
	 * 是用于处理请求合并的方法。
	 * @param ids
	 * @return
	 */
	@HystrixCommand
	public List<Map<String, Object>> mergeRequest(List<Long> ids){
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/testMerge?");
		for(int i = 0; i < ids.size(); i++){
			Long id = ids.get(i);
			if(i != 0){
				sb.append("&");
			}
			sb.append("ids=").append(id);
		}
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = 
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
		};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	/**
	 * 熔断机制
	 * 相当于一个强化的服务降级。 服务降级是只要远程服务出错，立刻返回fallback结果。
	 * 熔断是收集一定时间内的错误比例，如果达到一定的错误率。则启动熔断，返回fallback结果。
	 * 间隔一定时间会将请求再次发送给application service进行重试。如果重试成功，熔断关闭。
	 * 如果重试失败，熔断持续开启，并返回fallback数据。
	 * @HystrixCommand 描述方法。
	 *  fallbackMethod - fallback方法名
	 *  commandProperties - 具体的熔断标准。类型是HystrixProperty数组。
	 *   可以通过字符串或常亮类配置。
	 *   CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD - 错误数量。在10毫秒内，出现多少次远程服务调用错误，则开启熔断。
	 *       默认20个。 10毫秒内有20个错误请求则开启熔断。
	 *   CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE - 错误比例。在10毫秒内，远程服务调用错误比例达标则开启熔断。
	 *   CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS - 熔断开启后，间隔多少毫秒重试远程服务调用。默认5000毫秒。
	 */
	@HystrixCommand(fallbackMethod = "breakerFallback",
			commandProperties = {
			  // 默认20个;10s内请求数大于20个时就启动熔断器，当请求符合熔断条件时将触发getFallback()。
		      @HystrixProperty(name=HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, 
		    		  	value="10"),
		      // 请求错误率大于50%时就熔断，然后for循环发起请求，当请求符合熔断条件时将触发getFallback()。
		      @HystrixProperty(name=HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, 
		      			value="50"),
		      // 默认5秒;熔断多少秒后去尝试请求
		      @HystrixProperty(name=HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, 
		      			value="5000")}
	)
	public List<Map<String, Object>> testBreaker() {
		System.out.println("testBreaker method thread name : " + Thread.currentThread().getName());
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = 
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
		};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	@SuppressWarnings("unused")
	private List<Map<String, Object>> breakerFallback(){
		System.out.println("breakerFallback method thread name : " + Thread.currentThread().getName());
		List<Map<String, Object>> result = new ArrayList<>();
		
		Map<String, Object> data = new HashMap<>();
		data.put("id", -1);
		data.put("name", "breaker fallback datas");
		data.put("age", 0);
		result.add(data);
		return result;
	}
	
	/**
	 * 如果使用了@HystrixCommand注解，则Hystrix自动创建独立的线程池。
	 * groupKey和threadPoolKey默认值是当前服务方法所在类型的simpleName
	 * 
	 * 所有的fallback方法，都执行在一个HystrixTimer线程池上。
	 * 这个线程池是Hystrix提供的一个，专门处理fallback逻辑的线程池。
	 * 
	 * 线程池隔离实现
	 * 线程池隔离，就是为某一些服务，独立划分线程池。让这些服务逻辑在独立的线程池中运行。
	 * 不使用tomcat提供的默认线程池。
	 * 线程池隔离也有熔断能力。如果线程池不能处理更多的请求的时候，会触发熔断，返回fallback数据。
	 * groupKey - 分组名称，就是为服务划分分组。如果不配置，默认使用threadPoolKey作为组名。
	 * commandKey - 命令名称，默认值就是当前业务方法的方法名。
	 * threadPoolKey - 线程池命名，真实线程池命名的一部分。Hystrix在创建线程池并命名的时候，会提供完整命名。默认使用gourpKey命名
	 *  如果多个方法使用的threadPoolKey是同名的，则使用同一个线程池。
	 * threadPoolProperties - 为Hystrix创建的线程池做配置。可以使用字符串或HystrixPropertiesManager中的常量指定。
	 *  常用线程池配置：
	 *      coreSize - 核心线程数。最大并发数。1000*（99%平均响应时间 + 适当的延迟时间）
	 *      maxQueueSize - 阻塞队列长度。如果是-1这是同步队列。如果是正数这是LinkedBlockingQueue。如果线程池最大并发数不足，
	 *          提供多少的阻塞等待。
	 *      keepAliveTimeMinutes - 心跳时间，超时时长。单位是分钟。
	 *      queueSizeRejectionThreshold - 拒绝临界值，当最大并发不足的时候，超过多少个阻塞请求，后续请求拒绝。
	 */
	@HystrixCommand(groupKey="test-thread-quarantine", 
		commandKey = "testThreadQuarantine",
	    threadPoolKey="test-thread-quarantine", 
		threadPoolProperties = {
	        @HystrixProperty(name="coreSize", value="30"),
	        @HystrixProperty(name="maxQueueSize", value="100"),
	        @HystrixProperty(name="keepAliveTimeMinutes", value="2"),
	        @HystrixProperty(name="queueSizeRejectionThreshold", value="15")
	    },
		fallbackMethod = "threadQuarantineFallback")
	public List<Map<String, Object>> testThreadQuarantine() {
		System.out.println("testQuarantine method thread name : " + Thread.currentThread().getName());
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	@SuppressWarnings("unused")
	private List<Map<String, Object>> threadQuarantineFallback(){
		System.out.println("threadQuarantineFallback method thread name : " + Thread.currentThread().getName());
		List<Map<String, Object>> result = new ArrayList<>();
		
		Map<String, Object> data = new HashMap<>();
		data.put("id", -1);
		data.put("name", "thread quarantine fallback datas");
		data.put("age", 0);
		result.add(data);
		return result;
	}
	
	public void testThreadName(){
		System.out.println("testThreadName method thread name : " + Thread.currentThread().getName());
	}
	
	/**
	 * 信号量隔离实现
	 * 不会使用Hystrix管理的线程池处理请求。使用容器（Tomcat）的线程处理请求逻辑。
	 * 不涉及线程切换，资源调度，上下文的转换等，相对效率高。
	 * 信号量隔离也会启动熔断机制。如果请求并发数超标，则触发熔断，返回fallback数据。
	 * commandProperties - 命令配置，HystrixPropertiesManager中的常量或字符串来配置。
	 * 	execution.isolation.strategy - 隔离的种类，可选值只有THREAD（线程池隔离）和SEMAPHORE（信号量隔离）。
	 *      默认是THREAD线程池隔离。
	 *      设置信号量隔离后，线程池相关配置失效。
	 *  execution.isolation.semaphore.maxConcurrentRequests - 信号量最大并发数。默认值是10。常见配置500~1000。
	 *      如果并发请求超过配置，其他请求进入fallback逻辑。
	 */
	@HystrixCommand(fallbackMethod="semaphoreQuarantineFallback",
			commandProperties={
		      @HystrixProperty(
		    		  name=HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, 
		    		  value="SEMAPHORE"), // 信号量隔离
		      @HystrixProperty(
		    		  name=HystrixPropertiesManager.EXECUTION_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, 
		    		  value="100") // 信号量最大并发数
	})
	public List<Map<String, Object>> testSemaphoreQuarantine() {
		System.out.println("testSemaphoreQuarantine method thread name : " + Thread.currentThread().getName());
		ServiceInstance si = this.loadBalancerClient.choose("eureka-application-service");
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(si.getHost()).append(":").append(si.getPort()).append("/test");
		System.out.println("request application service URL : " + sb.toString());
		RestTemplate rt = new RestTemplate();
		ParameterizedTypeReference<List<Map<String, Object>>> type = new ParameterizedTypeReference<List<Map<String, Object>>>() {};
		ResponseEntity<List<Map<String, Object>>> response = rt.exchange(sb.toString(), HttpMethod.GET, null, type);
		List<Map<String, Object>> result = response.getBody();
		return result;
	}
	
	@SuppressWarnings("unused")
	private List<Map<String, Object>> semaphoreQuarantineFallback(){
		System.out.println("threadQuarantineFallback method thread name : " + Thread.currentThread().getName());
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> data = new HashMap<>();
		data.put("id", -1);
		data.put("name", "thread quarantine fallback datas");
		data.put("age", 0);
		result.add(data);
		return result;
	}

}

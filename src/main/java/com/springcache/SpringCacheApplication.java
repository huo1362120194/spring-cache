package com.springcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Cache	       缓存接口，定义缓存操作。实现有：RedisCache、EhCacheCache、ConcurrentMapCache等
 * CacheManager	   缓存管理器，管理各种缓存（cache）组件
 * @Cacheable      主要针对方法配置，能够根据方法的请求参数对其进行缓存
 * @CacheEvict     清空缓存
 * @CachePut       保证方法被调用，又希望结果被缓存与@Cacheable区别在于是否每次都调用方法，常用于更新
 * @EnableCaching  开启基于注解的缓存
 * keyGenerator	   缓存数据时key生成策略
 * serialize	   缓存数据时value序列化策略
 * @CacheConfig    统一配置本类的缓存注解的属性
 */
@SpringBootApplication
@EnableCaching //统一配置本类的缓存注解的属性
@EnableAsync //开启异步
public class SpringCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCacheApplication.class, args);
    }

}

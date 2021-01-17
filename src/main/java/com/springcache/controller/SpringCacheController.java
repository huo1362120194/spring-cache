package com.springcache.controller;

import com.springcache.entity.User;
import com.springcache.entity.UserEvent;
import com.springcache.redis.util.RedisUtil;
import com.springcache.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SpringCacheController {

    private static Logger logger = LoggerFactory.getLogger(SpringCacheController.class);

    @Autowired
    UserService userService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Resource
    private CacheManager cacheManager;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/create")
    public Map saveUser(@RequestParam String name, @RequestParam Integer age){
        Map map = new HashMap();
        int lines = userService.create(new User(name,age));
        map.put("lines",lines);
        return map;
    }

    //@Cacheable(value = "user",key = "#id")
    @GetMapping("/{id}")
    public User findUserById(@PathVariable Integer id){
        //User user = userService.findUserById(id);
        User user = new User("xiaoxiao",18);
        applicationContext.publishEvent(user);
        logger.info("11111111111111111111111");
        UserEvent userEvent = new UserEvent(user);
        applicationContext.publishEvent(userEvent);
        logger.info("22222222222222222222222");
        return user;
    }

    /**
     * 不用注解操作缓存
     */
    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Integer id){
        User user = userService.findUserById(id);
        Cache cache = cacheManager.getCache("user");
        cache.put(user.getId(),user);
        User o = cache.get(user.getId(), User.class);
        logger.info("cache user:{}",o);
        return user;
    }

    @CachePut(value = "user",key = "#user.id")
    @GetMapping("/update")
    public Map updateUser(@RequestParam Integer id, @RequestParam String name, @RequestParam Integer age){
        Map map = new HashMap();
        User user = userService.updateUser(new User(id,name,age));
        map.put("user",user);
        return map;
    }

    @CacheEvict(value = "user",key = "#id")
    @GetMapping("/del/{id}")
    public Map deleteUser(@PathVariable Integer id){
        Map map = new HashMap();
        int lines = userService.deleteUserById(id);
        map.put("lines",lines);
        return map;
    }

    /**
     * 发布消息
     */
    @RequestMapping("/send/message")
    public void sendMessage() {
        for(int i = 1; i <= 5; i++) {
            stringRedisTemplate.convertAndSend("channel:redis",String.format("第%s条消息",i));
            stringRedisTemplate.convertAndSend("channel:test",String.format("第%s条消息",i*10));
        }
    }

    /**
     * 批量删除redis缓存
     */
    @RequestMapping("/batch/clear/{i}")
    public void batchClearRedis(@PathVariable(name = "i") Integer i){
        String pattern = "";
        if(i % 2 == 0){
            pattern = "name:xiaoxiao:*";
        }else if(i % 2 == 1){
            pattern = "name:qiqi:*";
        }
        redisUtil.removePattern(pattern);
    }
}

package com.springcache.controller;

import com.springcache.entity.User;
import com.springcache.entity.UserEvent;
import com.springcache.redis.util.BloomFilterUtil;
import com.springcache.redis.util.RedisUtil;
import com.springcache.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import java.util.Objects;
import java.util.UUID;

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

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BloomFilterUtil bloomFilterUtil;

    private final static String LOCK_PREFIX_KEY = "plk";

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
        //先查缓存
        User user = (User)redisUtil.getKey(id.toString());
        if(Objects.nonNull(user)){
            return user;
        }
        /**
         * 缓存穿透：redis中没有数据，数据库中也没有数据，黑客恶意为之
         * 1、使用布隆过滤器，2、返回null
         */
        if(!bloomFilterUtil.mightContain(id)){
            //如果布隆过滤器中没有
            return null;
        }
        /**
         * 缓存击穿：redis中没有数据，数据库中有数据
         * 这里需要防止缓存击穿，即某一个或多个热点key失效了，瞬间所有的请求全部落到数据库
         * 1、加分布式锁，排队处理  2、设置的key的过期时间足够长，甚至永不过期
         */
        RLock lock = null;
        try{
            String key = LOCK_PREFIX_KEY + "_" + id;
            lock = redissonClient.getLock(key);
            //加锁，生产环境可以针对热门的数据加锁，非热门的数据不需要加
            boolean flag = lock.tryLock();
            if(!flag){
                //加锁失败了，自旋查询，这里可以合理设定次数
                return getUserById(id);
            }
            //缓存没有查到，去数据库查
            user = userService.findUserById(id);
            if(Objects.nonNull(user)){
                //数据库查到，设置进缓存
                redisUtil.setKey(id.toString(),user,30000000l);
            }else{
                //布隆过滤器可能误算，数据库中还是没有数据，缓存中设置null
                redisUtil.setKey(id.toString(),null,60l);
            }
        }finally {
            lock.unlock();
        }
        //Cache cache = cacheManager.getCache("user");
        //cache.put(user.getId(),user);
        //User u = cache.get(user.getId(), User.class);
        //logger.info("cache user:{}",u);
        return user;
    }

    //@CachePut(value = "user",key = "#user.id")
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

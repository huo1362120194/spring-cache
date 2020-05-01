package com.springcache;

import com.springcache.entity.User;
import com.springcache.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
class SpringCacheApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;

    @Test
    public void test1() {
        stringRedisTemplate.opsForValue().append("name","xiaoxiao");
        stringRedisTemplate.opsForList().leftPush("country","China");
        stringRedisTemplate.opsForList().leftPush("country","Philippine");
        stringRedisTemplate.opsForSet().add("age","18");
        stringRedisTemplate.opsForSet().add("age","18");
        stringRedisTemplate.opsForSet().add("age","119");
        for (int i = 0; i < 1000; i++){
            stringRedisTemplate.opsForValue().append("name:xiaoxiao:" + i,"xiaoxiao:" + i);
        }
        for (int i = 0; i < 1000; i++){
            stringRedisTemplate.opsForValue().append("name:qiqi:" + i,"qiqi:" + i);
        }
    }

    @Test
    public void test2(){
        User user1 = userMapper.findUserById(1);
        User user2 = userMapper.findUserById(2);
        redisTemplate.opsForValue().set(user1.getName(),user1);
        redisTemplate.opsForValue().set(user2.getName(),user2);
    }
}

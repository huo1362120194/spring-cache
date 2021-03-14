package com.springcache.redis.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BloomFilterUtil {

    private final static BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(),1000000);

    /**
     * 布隆过滤器不是100%，会放行一部分错误的数据过来，默认3%
     * 不是在集合中的元素可能判断为处在集合中
     */
    public static boolean mightContain(Integer i){
        return bloomFilter.mightContain(i);
    }

    @PostConstruct
    private void init(){
        for(int i = 0; i < 10; i++ ){
            bloomFilter.put(i);
        }
    }
}

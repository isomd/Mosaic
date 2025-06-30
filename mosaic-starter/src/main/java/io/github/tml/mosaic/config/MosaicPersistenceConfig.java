package io.github.tml.mosaic.config;

import io.github.tml.mosaic.core.persistence.adapter.file.JSONFileAdapter;
import io.github.tml.mosaic.core.persistence.adapter.mysql.MybatisAdapter;
import io.github.tml.mosaic.core.persistence.adapter.redis.RedisAdapter;
import io.github.tml.mosaic.core.persistence.adapter.redis.SpringRedisAdapter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
@Configuration
public class MosaicPersistenceConfig {

//    @Bean
//    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
//    public RedisAdapter springRedisAdapter(RedisTemplate<String, String> redisTemplate) {
//        return new SpringRedisAdapter(redisTemplate);
//    }

//    @Bean
//    @ConditionalOnClass(name = "redis.clients.jedis.Jedis")
//    public RedisAdapter jedisAdapter(@Value("${jedis.host:localhost}") String host,
//                                     @Value("${jedis.port:6379}") int port) {
//        return new JedisAdapter(host, port);
//    }

//    @Bean
//    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
//    public RedisAdapter redissonAdapter(RedissonClient redissonClient) {
//        return new RedissonAdapter(redissonClient);
//    }

//    @Bean
//    @ConditionalOnClass(name = "org.mybatis.spring.SqlSessionFactoryBean")
//    public Object mybatisAdapter(org.mybatis.spring.SqlSessionTemplate sqlSessionTemplate) {
//        return new MybatisAdapter(sqlSessionTemplate);
//    }

    @Bean
    @ConditionalOnClass(name = "com.alibaba.fastjson.JSON")
    public Object fastjsonAdapter() {
        return new JSONFileAdapter();
    }
}
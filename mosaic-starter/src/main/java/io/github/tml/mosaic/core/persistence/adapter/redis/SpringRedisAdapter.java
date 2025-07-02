package io.github.tml.mosaic.core.persistence.adapter.redis;

import org.springframework.data.redis.core.RedisTemplate;


/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public class SpringRedisAdapter implements RedisAdapter{

    private final RedisTemplate<String, String> redisTemplate;

    public SpringRedisAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value.toString());
    }

    @Override
    public Object get(String key) {
        String value = redisTemplate.opsForValue().get(key);

        return value;
    }
}
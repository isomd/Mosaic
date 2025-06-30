package io.github.tml.mosaic.core.persistence.adapter.redis;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public interface RedisAdapter {

    void set(String key, Object value);
    Object get(String key);

}
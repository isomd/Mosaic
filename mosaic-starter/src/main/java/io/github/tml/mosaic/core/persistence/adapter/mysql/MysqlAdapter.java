package io.github.tml.mosaic.core.persistence.adapter.mysql;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public interface MysqlAdapter {

    <T> void save(T doObject);
    <T> T get(Class<T> clazz, Object primaryKey);

}
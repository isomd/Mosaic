package io.github.tml.mosaic.core.persistence.adapter.mysql;

import org.mybatis.spring.SqlSessionTemplate;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public class MybatisAdapter implements MysqlAdapter {

    private final SqlSessionTemplate sqlSessionTemplate;

    public MybatisAdapter(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Override
    public <T> void save(T doObject) {
        Class<?> doClass = doObject.getClass();
        Class<?> mapperClass = getMapperClass(doClass);
        Object mapper = sqlSessionTemplate.getMapper(mapperClass);
        invoke(mapper, "insert", doObject);
    }

    @Override
    public <T> T get(Class<T> doClass, Object id) {
        Class<?> mapperClass = getMapperClass(doClass);
        Object mapper = sqlSessionTemplate.getMapper(mapperClass);
        Object res = invoke(mapper, "selectById", id);
        return doClass.cast(res);
    }

    private Class<?> getMapperClass(Class<?> doClass) {
        try {
            return Class.forName(doClass.getName() + "Mapper");
        } catch (Exception e) {
            throw new RuntimeException("Mapper not found for " + doClass.getName(), e);
        }
    }
    private Object invoke(Object mapper, String method, Object arg) {
        try {
            return mapper.getClass().getMethod(method, arg.getClass()).invoke(mapper, arg);
        } catch (Exception e) {
            throw new RuntimeException("Invoke error: " + method, e);
        }
    }
}
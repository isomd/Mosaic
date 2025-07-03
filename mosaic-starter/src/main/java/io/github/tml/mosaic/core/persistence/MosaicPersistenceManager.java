package io.github.tml.mosaic.core.persistence;

import io.github.tml.mosaic.core.persistence.adapter.file.LocalFileAdapter;
import io.github.tml.mosaic.core.persistence.adapter.mysql.MysqlAdapter;
import io.github.tml.mosaic.core.persistence.adapter.redis.RedisAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
@Slf4j
@Component
public class MosaicPersistenceManager {

    @Autowired(required = false)
    private List<RedisAdapter> redisAdapter;
    @Autowired(required = false)
    private List<MysqlAdapter> mysqlAdapter;
    @Autowired(required = false)
    private List<LocalFileAdapter> localFileAdapter;


    public RedisAdapter getRedisAdapter() {
        if (redisAdapter == null || redisAdapter.isEmpty()) {
            return null;
        }
        return redisAdapter.get(0);
    }


    public MysqlAdapter getMysqlAdapter() {
        if (mysqlAdapter ==null || mysqlAdapter.isEmpty()) {
            return null;
        }
        return mysqlAdapter.get(0);
    }


    public LocalFileAdapter getLocalFileAdapter() {
        if (localFileAdapter == null ||localFileAdapter.isEmpty()) {
            return null;
        }
        return localFileAdapter.get(0);
    }



}
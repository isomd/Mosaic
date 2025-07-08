package io.github.tml.mosaic.infrastructure;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.core.persistence.MosaicPersistenceManager;
import io.github.tml.mosaic.domain.hotswap.HotSwapDomain;
import io.github.tml.mosaic.hotSwap.HotSwapRepository;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
@Slf4j
@Component
public class HotSwapRepositoryImpl implements HotSwapRepository {

    @Autowired
    private MosaicPersistenceManager persistenceManager;
    @Autowired
    private HotSwapDomain hotSwapDomain;

    @Override
    public void save() {
        try {
            List<HotSwapPoint> allHotSwapPoints = hotSwapDomain.getAllHotSwapPoints();
            if(persistenceManager.getLocalFileAdapter()!=null){
                saveLocalFile(allHotSwapPoints);
            }else if(persistenceManager.getRedisAdapter()!=null){
                saveRedisFile(allHotSwapPoints);
            }else if(persistenceManager.getMysqlAdapter()!=null){
                saveMysqlFile(allHotSwapPoints);
            }else
                throw new RuntimeException("无法识别序列化方式，请确认依赖是否引入");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HotSwapPoint getHotSwapPoint() {
        HotSwapPoint hotSwapPoint;
        hotSwapPoint = persistenceManager.getLocalFileAdapter().get(getPath(), HotSwapPoint.class);
        if(hotSwapPoint != null) {
            return hotSwapPoint;
        }
        Object resp = persistenceManager.getRedisAdapter().get(getPath());
        if(Objects.nonNull(resp)) {
            if(resp instanceof HotSwapPoint) {
                return (HotSwapPoint) resp;
            }
            throw new RuntimeException("对象类型转换失败");
        }

        HotSwapPoint swapPoint = persistenceManager.getMysqlAdapter().get(HotSwapPoint.class, "id");
        if(Objects.nonNull(swapPoint)) {
            return swapPoint;
        }
        throw new RuntimeException("热更新点实例获取失败");
    }

    @Override
    public List<HotSwapPoint> getHotSwapPoints() {

        try {
            Object content = persistenceManager.getLocalFileAdapter().get(getPath(), String.class);
            if (content == null || content.toString().trim().isEmpty()) {
                return List.of(); // 返回不可变的空列表
            }
            return JSON.parseArray(content.toString(), HotSwapPoint.class);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void saveLocalFile(List<HotSwapPoint> hotSwapPoint) throws IOException {

        String filePath = getPath();

        persistenceManager.getLocalFileAdapter().save(filePath, hotSwapPoint);
    }

    private void saveRedisFile(List<HotSwapPoint> hotSwapPoint) {
        String hotSwapPointKey = getPath();
        String json = JSON.toJSONString(hotSwapPoint);
        persistenceManager.getRedisAdapter().set(hotSwapPointKey, json);
    }

    private void saveMysqlFile(List<HotSwapPoint> hotSwapPoint) {

    }

    @Override
    public String getPath() {
        return "/hotswap/hotswapPoint.json";
    }
}
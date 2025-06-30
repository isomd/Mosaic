package io.github.tml.mosaic.infrastructure;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.core.persistence.MosaicPersistenceManager;
import io.github.tml.mosaic.domain.HotSwapDomain;
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
            }
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
            throw new RuntimeException("获取 HotSwapPoint 数据失败: " + getPath(), e);

        }
    }

    private void saveLocalFile(List<HotSwapPoint> hotSwapPoint) throws IOException {
        String filePath = getPath();
        List<HotSwapPoint> existingList = getHotSwapPoints();

        Set<HotSwapPoint> uniqueSet = new HashSet<>(existingList);
        uniqueSet.addAll(hotSwapPoint);
        log.info("保存的热更新点:{}",uniqueSet.toArray());
        persistenceManager.getLocalFileAdapter().save(filePath, uniqueSet.toArray(new HotSwapPoint[0]));
    }

    private void saveRedisFile(List<HotSwapPoint> hotSwapPoint) {
        String hotSwapPointKey = getPath();
        Object o = persistenceManager.getRedisAdapter().get(hotSwapPointKey);
        List<HotSwapPoint> hotSwapPointList = new ArrayList<>();
        if (o != null) {
            String jsonArray = o.toString();
            if (!jsonArray.trim().isEmpty()) {
                hotSwapPointList = JSON.parseArray(jsonArray, HotSwapPoint.class);
            }
        }
        Set<HotSwapPoint> uniqueSet = new HashSet<>(hotSwapPointList);
        uniqueSet.addAll(hotSwapPoint);
        String json = JSON.toJSONString(uniqueSet.toArray(new HotSwapPoint[0]));
        persistenceManager.getRedisAdapter().set(hotSwapPointKey, json);
    }

    private void saveMysqlFile(List<HotSwapPoint> hotSwapPoint) {

    }

    @Override
    public String getPath() {
        return "/hotswap/";
    }
}
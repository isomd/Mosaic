package io.github.tml.mosaic.core.persistence.init;

import io.github.tml.mosaic.core.persistence.MosaicPersistenceManager;
import io.github.tml.mosaic.core.persistence.adapter.file.LocalFileAdapter;
import io.github.tml.mosaic.domain.HotSwapDomain;
import io.github.tml.mosaic.hotSwap.HotSwapRepository;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author welsir
 * @description :
 * @date 2025/6/30
 */
@Component
@Slf4j
public class GlobalFlushManager {

    @Resource
    HotSwapRepository hotSwapRepository;
    @Resource
    HotSwapDomain hotSwapDomain;
    @Resource
    ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        //获取热更新点存储文件并赋值到内存对象
        List<HotSwapPoint> hotSwapPoints = hotSwapRepository.getHotSwapPoints();
        hotSwapPoints.forEach(hotSwapDomain::setHotSwapPoint);

        //---------------------
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            hotSwapRepository.save();
        }, 0, 5, TimeUnit.SECONDS);
    }


}
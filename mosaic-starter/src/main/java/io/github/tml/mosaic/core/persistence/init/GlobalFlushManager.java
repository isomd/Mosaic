package io.github.tml.mosaic.core.persistence.init;

import io.github.tml.mosaic.domain.hotswap.HotSwapDomain;
import io.github.tml.mosaic.hotSwap.HotSwapRepository;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author welsir
 * @description :
 * @date 2025/6/30
 */
@Component
@Slf4j
@DependsOn("mosaicHotSwapLoaderInstaller")
public class GlobalFlushManager implements CommandLineRunner {

    @Resource
    HotSwapRepository hotSwapRepository;
    @Resource
    HotSwapDomain hotSwapDomain;
    @Resource
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        //获取热更新点存储文件并赋值到内存对象
        List<HotSwapPoint> hotSwapPoints = hotSwapRepository.getHotSwapPoints();
        hotSwapPoints.forEach(hotSwapDomain::setHotSwapPoint);
        hotSwapDomain.recoverHotSwapPoint(hotSwapPoints);
        //---------------------
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            hotSwapRepository.save();
        }, 0, 5, TimeUnit.SECONDS);
    }
}
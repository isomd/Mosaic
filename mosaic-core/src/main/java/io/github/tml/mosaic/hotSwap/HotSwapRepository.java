package io.github.tml.mosaic.hotSwap;

import io.github.tml.mosaic.MosaicRepository;
import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public interface HotSwapRepository extends MosaicRepository {

    void save();
    HotSwapPoint getHotSwapPoint();
    List<HotSwapPoint> getHotSwapPoints();
}
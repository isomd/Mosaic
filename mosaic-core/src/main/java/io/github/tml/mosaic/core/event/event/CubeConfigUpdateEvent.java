package io.github.tml.mosaic.core.event.event;

import io.github.tml.mosaic.cube.external.MosaicCube;
import lombok.Data;

import java.util.Map;

/**
 * 描述: CubeConfig更新事件
 * @author suifeng
 * 日期: 2025/6/13
 */
@Data
public class CubeConfigUpdateEvent extends MosaicEvent {

    private final MosaicCube mosaicCube;
    private final Map<String, Object> before;
    private final Map<String, Object> after;

    public CubeConfigUpdateEvent(MosaicCube mosaicCube, Map<String, Object> before, Map<String, Object> after) {
        super(null);
        this.mosaicCube = mosaicCube;
        this.before = before;
        this.after = after;
    }
    
    @Override
    public String getEventType() {
        return "CUBE_CONFIG_UPDATE";
    }
}
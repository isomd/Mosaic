package io.github.tml.mosaic.core.event.event;

import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Data;

/**
 * 描述: CubeConfig更新事件
 * @author suifeng
 * 日期: 2025/6/13
 */
@Data
public class CubeConfigUpdateEvent extends MosaicEvent {

    private final CubeDefinition cubeDefinition;
    private final ConfigInfo before;
    private final ConfigInfo after;

    public CubeConfigUpdateEvent(CubeDefinition cubeDefinition, ConfigInfo before, ConfigInfo after) {
        super(null);
        this.cubeDefinition = cubeDefinition;
        this.before = before;
        this.after = after;
    }
    
    @Override
    public String getEventType() {
        return "CUBE_CONFIG_UPDATE";
    }
}
package io.github.tml.mosaic.core.event.event;

import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;
import lombok.Data;

/**
 * 描述: CubeDefinition注册事件
 * @author suifeng
 * 日期: 2025/6/13
 */
@Data
public class CubeDefinitionRegisteredEvent extends CubeEvent {

    private final GUID cubeId;
    private final CubeDefinition cubeDefinition;
    
    public CubeDefinitionRegisteredEvent(Object source, GUID cubeId, CubeDefinition cubeDefinition) {
        super(source);
        this.cubeId = cubeId;
        this.cubeDefinition = cubeDefinition;
    }
    
    @Override
    public String getEventType() {
        return "CUBE_DEFINITION_REGISTERED";
    }
}
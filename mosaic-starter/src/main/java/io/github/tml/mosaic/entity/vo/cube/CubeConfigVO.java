package io.github.tml.mosaic.entity.vo.cube;

import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 描述: Cube配置信息前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeConfigVO {

    private ConfigInfo configInfo;

    /**
     * 从CubeDefinition转换为CubeConfigVO
     */
    public static CubeConfigVO fromDefinition(CubeDefinition definition) {
        CubeConfigVO cubeConfigVO = new CubeConfigVO();
        if (definition != null) {
            cubeConfigVO.setConfigInfo(definition.getConfigInfo());
        }
        return cubeConfigVO;
    }
}
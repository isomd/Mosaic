package io.github.tml.mosaic.entity.vo.cube;

import io.github.tml.mosaic.cube.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.install.domian.info.PointsResultInfo;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 扩展点结果定义前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResultVO{

    private List<PointItemVO> pointItems = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointItemVO{
        private String itemName;
        private Class<?> itemClass;
        private String description;
    }
}
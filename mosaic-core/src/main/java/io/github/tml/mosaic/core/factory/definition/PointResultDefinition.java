package io.github.tml.mosaic.core.factory.definition;

import io.github.tml.mosaic.install.support.info.PointsResultInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PointResultDefinition {

    @Getter
    private List<PointResultItemDefinition> pointsResultInfoList = new ArrayList<>();

    public void addPointResultInfo(PointResultItemDefinition pointResultInfo){
        this.pointsResultInfoList.add(pointResultInfo);
    }

    public static PointResultDefinition convertByInfoContext(PointsResultInfo pointsResultInfo){
        PointResultDefinition pointResultDefinition = new PointResultDefinition();
        if(Objects.isNull(pointsResultInfo)){
            return pointResultDefinition;
        }
        for (PointsResultInfo.PointResultItemInfo pointResultItemInfo : pointsResultInfo.getPointsResultInfoList()) {
            PointResultItemDefinition itemDefinition = new PointResultItemDefinition();
            itemDefinition.setItemName(pointResultItemInfo.getItemName());
            itemDefinition.setItemClass(pointResultItemInfo.getItemClass());
            itemDefinition.setDescription(pointResultItemInfo.getDescription());
            pointResultDefinition.addPointResultInfo(itemDefinition);
        }
        return pointResultDefinition;
    }

    @Data
    public static class PointResultItemDefinition{
        private String itemName;
        private Class<?> itemClass;
        private String description;
    }
}

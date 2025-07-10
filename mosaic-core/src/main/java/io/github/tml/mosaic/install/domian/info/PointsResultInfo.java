package io.github.tml.mosaic.install.domian.info;

import io.github.tml.mosaic.cube.external.MResultItem;
import io.github.tml.mosaic.cube.external.MosaicVoid;
import io.github.tml.mosaic.util.ClassUtils;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;

import static io.github.tml.mosaic.core.CubeConstant.DEFAULT_RETURN_NAME;


@Getter
public class PointsResultInfo {

    private final List<PointResultItemInfo> pointsResultInfoList = new ArrayList<>();

    public void addPointResultInfo(PointResultItemInfo pointResultInfo){
        this.pointsResultInfoList.add(pointResultInfo);
    }

    public void addByMResultItemAnno(MResultItem mResultItem){
        if(Objects.isNull(mResultItem)){
            return;
        }
        String[] name = mResultItem.name();
        String[] description = mResultItem.description();
        Class<?>[] type = mResultItem.type();
        for (int i = 0; i < name.length; i++) {
            String descriptionStr = "";
            if(i>=type.length){
                break;
            }
            if(i < description.length){
                descriptionStr = description[i];
            }
            addPointResultInfo(new PointResultItemInfo(name[i], type[i], descriptionStr));
        }
    }

    public void addByReturnType(Class<?> returnType){
        if (MosaicVoid.isVoid(returnType)) {
            addPointResultInfo(new PointResultItemInfo(DEFAULT_RETURN_NAME, MosaicVoid.class, ""));
        }else if (ClassUtils.isBasicType(returnType)){
            addPointResultInfo(new PointResultItemInfo(DEFAULT_RETURN_NAME, returnType, ""));
        }else if(ClassUtils.isCustomEntityClass(returnType)){
            Field[] fields = returnType.getFields();
            for (Field field : fields) {
                addPointResultInfo(new PointResultItemInfo(field.getName(), field.getType(), ""));
            }
        }
    }

    @Data
    public static class PointResultItemInfo{
        private String itemName;
        private Class<?> itemClass;
        private String description;

        public PointResultItemInfo(String itemName, Class<?> itemClass, String description) {
            this.itemName = itemName;
            this.itemClass = itemClass;
            this.description = description;
        }
    }
}

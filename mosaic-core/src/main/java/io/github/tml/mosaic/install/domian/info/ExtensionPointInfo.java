package io.github.tml.mosaic.install.domian.info;

import io.github.tml.mosaic.cube.external.MExtension;
import io.github.tml.mosaic.cube.external.MResultItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

// 扩展点信息
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtensionPointInfo {
    private String id;
    private String methodName;
    private String extensionName;
    private int priority;
    private String description;
    private boolean asyncFlag;
    private Class<?> returnType;
    private Class<?>[] parameterTypes;
    private PointsResultInfo pointsResultInfo;

    public void setInfoByMExtensionPoint(MExtension anno) {
        this.setAsyncFlag(false);
        this.setId(anno.extPointId());
        this.setExtensionName(anno.name());
        this.setPriority(anno.priority());
        this.setDescription(anno.description());
    }

    public void setInfoByMethod(Method method) {
        this.setMethodName(method.getName());
        this.setReturnType(method.getReturnType());
        this.setParameterTypes(method.getParameterTypes());

        Class<?> returnType = method.getReturnType();
        PointsResultInfo pointsResultInfo = new PointsResultInfo();
        MResultItem mResultItem = method.getAnnotation(MResultItem.class);
        if (mResultItem != null) {
            pointsResultInfo.addByMResultItemAnno(mResultItem);
        } else {
            pointsResultInfo.addByReturnType(returnType);
        }
        this.setPointsResultInfo(pointsResultInfo);
    }
}
package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.cube.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 扩展点抽象基类
 */
@Setter
@Getter
public class ExtensionPoint extends UniqueEntity {

    private String methodName;

    private Class<?> returnType;

    private Class<?>[] parameterTypes;

    private String description;

    private boolean asyncFlag;

    private String extensionName;

    private int priority = 100;

    private ExtPointResult returnResult;

    public ExtensionPoint(String id, String name, String description) {
        super(new DotNotationId(id));
        this.methodName = name;
        this.description = description;
    }

    public ExtensionPoint(GUID id) {
        super(id);
    }

    // 获取扩展点ID的便捷方法
    public GUID getExtensionId() {
        return getId();
    }

    public static ExtensionPoint convertByDefinition(ExtensionPointDefinition epd) {
        ExtensionPoint extensionPoint = new ExtensionPoint(new DotNotationId(epd.getId()));
        extensionPoint.setExtensionName(epd.getExtensionName());
        extensionPoint.setDescription(epd.getDescription());
        extensionPoint.setMethodName(epd.getMethodName());
        extensionPoint.setParameterTypes(epd.getParameterTypes());
        extensionPoint.setReturnType(epd.getReturnType());
        return extensionPoint;
    }

}
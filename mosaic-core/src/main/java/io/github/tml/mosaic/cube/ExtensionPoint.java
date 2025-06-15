package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.core.factory.definition.ExtensionPointDefinition;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.guid.UniqueEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 扩展点抽象基类
 */
public class ExtensionPoint extends UniqueEntity {

    @Getter @Setter
    private String methodName;

    @Getter @Setter
    private Class<?> returnType;

    @Getter @Setter
    private Class<?>[] parameterTypes;

    @Getter @Setter
    private String description;

    @Getter @Setter
    private boolean asyncFlag;

    @Getter @Setter
    private String extensionName;

    @Getter @Setter
    private int priority = 100;

    @Getter
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

    public void setReturnResult(ExtPointResult result) {
        this.returnResult = result;
    }
}
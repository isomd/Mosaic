package io.github.tml.mosaic.cube.factory.definition;

import io.github.tml.mosaic.install.domian.info.ExtensionPointInfo;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ExtensionPointDefinition {

    private final String id;
    private final String methodName;
    private final String extensionName;
    private final int priority;
    private final String description;
    private final boolean asyncFlag;
    private final Class<?> returnType;
    private final Class<?>[] parameterTypes;
    private PointResultDefinition pointResultDefinitions;
    
    @Override
    public String toString() {
        return "ExtensionPointDefinition{" +
                "id='" + id + '\'' +
                ", methodName='" + methodName + '\'' +
                ", extensionName='" + extensionName + '\'' +
                ", priority=" + priority +
                '}';
    }

    public static ExtensionPointDefinition convertByInfoContext(ExtensionPointInfo extensionPackageInfo){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.createTypeMap(extensionPackageInfo, ExtensionPointDefinition.class)
                .map(extensionPackageInfo);
    }
}
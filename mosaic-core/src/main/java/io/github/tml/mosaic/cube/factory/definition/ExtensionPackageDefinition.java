package io.github.tml.mosaic.cube.factory.definition;

import io.github.tml.mosaic.install.domian.info.ExtensionPackageInfo;
import lombok.Data;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExtensionPackageDefinition {

    private final String id;
    private final String name;
    private final String description;
    private final String className;
    private final String cubeId;
    private final List<ExtensionPointDefinition> extensionPoints = new ArrayList<>();
    
    public void addExtensionPoint(ExtensionPointDefinition epd) {
        extensionPoints.add(epd);
    }

    public static ExtensionPackageDefinition convertByInfoContext(ExtensionPackageInfo extensionPackageInfo){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.createTypeMap(extensionPackageInfo, ExtensionPackageDefinition.class)
                .map(extensionPackageInfo);
    }
}
package io.github.tml.mosaic.core.factory.definition;

import io.github.tml.mosaic.install.support.info.InfoContext;
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

    public static ExtensionPackageDefinition convertByInfoContext(InfoContext.ExtensionPackageInfo extensionPackageInfo){
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.createTypeMap(extensionPackageInfo, ExtensionPackageDefinition.class)
                .map(extensionPackageInfo);
    }
}
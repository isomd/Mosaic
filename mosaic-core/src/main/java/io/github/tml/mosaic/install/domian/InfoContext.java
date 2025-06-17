package io.github.tml.mosaic.install.domian;

import io.github.tml.mosaic.cube.factory.io.resource.Resource;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.support.ResourceFileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InfoContext {

    private Resource resource;

    /**
     * 来源信息，用于描述当前信息的来源
     */
    private ResourceFileType resourceType;

    /**
     * 外部来源插件中的所有java类
     */
    private List<Class<?>> allClazz = new ArrayList<>();

    private List<CubeInfo> cubeInfoList = new ArrayList<>();

    /**
     * 类加载器
     */
    private transient ClassLoader classLoader;

    public void addCubeInfo(CubeInfo cubeInfo){
        this.cubeInfoList.add(cubeInfo);
    }
}

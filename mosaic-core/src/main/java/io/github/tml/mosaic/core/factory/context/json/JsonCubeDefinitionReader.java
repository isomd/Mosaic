package io.github.tml.mosaic.core.factory.context.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.support.AbstractCubeDefinitionReader;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.adpter.ResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.support.InfoContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 描述: 从Json的配置文件中去加载
 * @author suifeng
 * 日期: 2025/6/7
 */
public class JsonCubeDefinitionReader extends AbstractCubeDefinitionReader {

    private final ResourceFileAdapterRegistry fileAdapterRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCubeDefinitionReader(CubeDefinitionRegistry registry, ResourceFileAdapterRegistry fileAdapterRegistry) {
        super(registry);
        this.fileAdapterRegistry = fileAdapterRegistry;
    }

    public JsonCubeDefinitionReader(CubeDefinitionRegistry registry, ResourceLoader resourceLoader, ResourceFileAdapterRegistry fileAdapterRegistry) {
        super(registry, resourceLoader);
        this.fileAdapterRegistry = fileAdapterRegistry;
    }

    @Override
    public void loadCubeDefinitions(Resource resource) throws CubeException {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                doLoadCubeDefinitions(inputStream);
            }
        } catch (IOException e) {
            throw new CubeException("IOException parsing JSON document from " + resource, e);
        }
    }

    private void doLoadCubeDefinitions(InputStream inputStream) throws CubeException {
        try {
            // 解析JSON配置
            InstallationConfig config = objectMapper.readValue(inputStream, InstallationConfig.class);

            // 处理每个安装项
            for (InstallationItem item : config.getInstallations()) {
                processInstallationItem(item);
            }
        } catch (IOException e) {
            throw new CubeException("Failed to parse installation config", e);
        }
    }

    private void processInstallationItem(InstallationItem item) throws CubeException {
        ResourceFileAdapter adapter = fileAdapterRegistry.getAdapter(item.getType());
        if (adapter == null) throw new CubeException("No adapter for type: " + item.getType());

        InfoContext infoContext = adapter.adapter(item.getLocation());

        // 关键转换步骤
        List<CubeDefinition> cubeDefs = CubeDefinitionConverter.convertToCubeDefinitions(infoContext);

        // 注册到CubeDefinitionRegistry
        CubeDefinitionRegistry registry = getRegistry();
        for (CubeDefinition cubeDef : cubeDefs) {
            registry.registerCubeDefinition(cubeDef.getId(), cubeDef);
        }
    }
}

package io.github.tml.mosaic.core.factory.context.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.support.AbstractCubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 描述: 从Json的配置文件中去加载CubeInfo
 * @author suifeng
 * 日期: 2025/6/7
 */
public class JsonCubeInstallationItemReader extends AbstractCubeInstallationItemReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCubeInstallationItemReader() {
        super();
    }

    public JsonCubeInstallationItemReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource resource) throws CubeException {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                return doLoadCubeInstallationItem(inputStream);
            }
        } catch (IOException e) {
            throw new CubeException("IOException parsing JSON document from " + resource, e);
        }
    }

    private InstallationConfig doLoadCubeInstallationItem(InputStream inputStream) throws CubeException {
        try {
            // 解析JSON配置
            return objectMapper.readValue(inputStream, InstallationConfig.class);
        } catch (IOException e) {
            throw new CubeException("Failed to parse installation config", e);
        }
    }
}

package io.github.tml.mosaic.core.factory.io.loader;

import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.core.factory.io.resource.UrlResource;
import io.github.tml.mosaic.core.factory.io.resource.ClassPathResource;
import io.github.tml.mosaic.core.factory.io.resource.FileSystemResource;
import io.github.tml.mosaic.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 描述: 默认资源加载器
 * @author suifeng
 * 日期: 2025/5/29
 */
public class DefaultResourceLoader implements ResourceLoader {

    public static String CLASSPATH_URL_PREFIX = "classpath:";

    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location 不得为空");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        }
        else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                return new FileSystemResource(location);
            }
        }
    }
}

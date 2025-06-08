package io.github.tml.config;

import java.net.URL;

public interface PluginDescriptor {
    String getId();
    String getName();
    String getViewBasePath(); // 如：io/github/tml/plugin/chart/view
    URL getResourceBaseUrl();
}
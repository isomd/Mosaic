package io.github.tml.mosaic.cube.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class YamlConfigReader implements ConfigReader {

    private String yamlFilePath;

    // 默认构造方法，指定默认 YAML 文件路径
    public YamlConfigReader() {
        this.yamlFilePath = "application.yml"; // 默认 YAML 文件路径
    }

    // 自定义 YAML 文件路径的构造方法
    public YamlConfigReader(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    // 设置新的 YAML 文件路径
    public void setYamlFilePath(String yamlFilePath) {
        this.yamlFilePath = yamlFilePath;
    }

    // 获取当前的 YAML 文件路径
    public String getYamlFilePath() {
        return yamlFilePath;
    }

    // 读取 YAML 文件并转换为 JSONObject
    public JSONObject readYamlAsJson() {
        // 使用类加载器从 classpath 加载资源文件
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(yamlFilePath)) {
            if (inputStream == null) {
                log.error("YAML file not found in classpath: {}",yamlFilePath);
                return new JSONObject();
            }

            // 使用 SnakeYAML 解析 YAML 文件
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);

            // 转换为 JSONObject
            return new JSONObject(yamlData);
        } catch (Exception e) {
            log.error("Failed to read YAML file: {} error:{}",yamlFilePath, e.getMessage());
        }
        return new JSONObject();
    }

    @Override
    public JSONObject readConfig() {
        return readYamlAsJson();
    }
}

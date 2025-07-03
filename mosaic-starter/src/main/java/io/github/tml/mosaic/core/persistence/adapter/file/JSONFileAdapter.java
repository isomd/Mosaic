package io.github.tml.mosaic.core.persistence.adapter.file;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.MosaicRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public class JSONFileAdapter implements LocalFileAdapter{

    private final String BASE_PATH;

    public JSONFileAdapter() {
        // 获取项目根目录的父目录，然后拼 mosaic
        String projectDir = System.getProperty("user.dir"); // /home/user/myproject
        Path mosaic = Paths.get(projectDir, "mosaic");
        try {
            // 创建 mosaic 文件夹（如果不存在）
            if (!Files.exists(mosaic)) {
                Files.createDirectories(mosaic);
            }
            this.BASE_PATH = mosaic.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new RuntimeException("文件操作失败", e);
        }
    }

    @Override
    public void save(String filePath, Object obj) {
        try {
            // 构造完整路径
            Path fullPath = Paths.get(BASE_PATH + filePath);

            // 检查并创建父目录
            Path parentDir = fullPath.getParent(); // 获取父目录路径
            if (parentDir != null && !Files.exists(parentDir)) { // 如果父目录不存在
                Files.createDirectories(parentDir); // 创建父目录及其所有不存在的父级目录
            }

            String json = JSON.toJSONString(obj, true); // true 表示格式化输出
            Files.write(fullPath, json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("保存到本地文件失败: " + filePath, e);
        }
    }

    @Override
    public <T> T get(String filePath, Class<T> clazz) {
        File file = new File(BASE_PATH+filePath);
        if (!file.exists()) return null;
        try {
            // 读取文件内容为字符串
            String json = Files.readString(file.toPath());
            // 将 JSON 字符串解析为指定类型的对象
            return JSON.parseObject(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + filePath, e);
        } catch (Exception e) {
            throw new RuntimeException("解析 JSON 失败: " + filePath, e);
        }
    }
}
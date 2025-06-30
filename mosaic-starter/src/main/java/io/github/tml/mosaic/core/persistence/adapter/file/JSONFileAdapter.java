package io.github.tml.mosaic.core.persistence.adapter.file;

import com.alibaba.fastjson.JSON;
import io.github.tml.mosaic.MosaicRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        File parentDir = new File(projectDir).getParentFile(); // /home/user
        this.BASE_PATH = new File(parentDir, "mosaic").getAbsolutePath();
        // 确保 mosaic 目录存在
        File mosaicDir = new File(BASE_PATH);
        if (!mosaicDir.exists()) {
            mosaicDir.mkdirs();
        }
    }

    @Override
    public void save(String filePath, Object obj) {
        try {
            String json = JSON.toJSONString(obj, true); // true 表示格式化输出
            Files.write(Paths.get(BASE_PATH+filePath), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("保存到本地文件失败: " + filePath, e);
        }
    }

    @Override
    public <T> T get(String filePath, Class<T> clazz) {
        try {
            File file = new File(BASE_PATH+filePath);
            if (!file.exists()) return null;
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            return JSON.parseObject(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("读取本地文件失败: " + filePath, e);
        }
    }
}
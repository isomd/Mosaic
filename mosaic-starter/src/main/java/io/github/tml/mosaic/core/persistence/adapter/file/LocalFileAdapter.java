package io.github.tml.mosaic.core.persistence.adapter.file;

import io.github.tml.mosaic.MosaicRepository;

/**
 * @author welsir
 * @description :
 * @date 2025/6/28
 */
public interface LocalFileAdapter {

    void save(String filePath, Object obj);
    <T> T get(String filePath, Class<T> clazz);

}
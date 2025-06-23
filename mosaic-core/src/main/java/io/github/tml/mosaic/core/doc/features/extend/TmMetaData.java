package io.github.tml.mosaic.core.doc.features.extend;

import io.github.tml.mosaic.core.doc.core.TmlDoc;
import io.github.tml.mosaic.core.doc.part.MetaData;

import java.util.Map;
import java.util.Optional;

/**
 * 描述: 扩展特征（元数据）
 *
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmMetaData extends TmlDoc {

    String METADATA_PROPERTY = "metadata";

    @SuppressWarnings("unchecked")
    default Optional<MetaData> getMetaData() {
        return Optional.ofNullable((Map<String, Object>) get(METADATA_PROPERTY)).map(MetaData::new);
    }
}

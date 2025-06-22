package io.github.tml.mosaic.hotSwap.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author welsir
 * @description :
 * @date 2025/6/21
 */
public class VersionId {

    private final String id;

    public VersionId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static VersionId generate() {
        return new VersionId("v" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    }

    @Override
    public String toString() {
        return "VersionId{id='" + id + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionId)) return false;
        VersionId versionId = (VersionId) o;
        return Objects.equals(id, versionId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
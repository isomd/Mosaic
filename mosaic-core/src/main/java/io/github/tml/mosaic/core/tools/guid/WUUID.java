package io.github.tml.mosaic.core.tools.guid;

import java.util.Objects;

public class WUUID implements GUID{
    private String worldPath;

    private String worldName;

    private Integer version;

    public WUUID(String worldPath, String worldName, Integer version) {
        this.worldPath = worldPath;
        this.worldName = worldName;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WUUID){
            WUUID worldUuid = (WUUID) o;
            return this.worldName.equals(worldUuid.worldName) && this.version.equals(worldUuid.version) && this.worldPath.equals(worldUuid.worldPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        String s = worldPath +
                worldName +
                version;
        return Objects.hashCode(s);
    }

    @Override
    public String toString() {
        return worldPath +
                worldName +
                version;
    }
}

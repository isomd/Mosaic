package io.github.tml.mosaic.cube.external;

import java.util.Objects;

public class MosaicVoid extends AbstractMosaicResult {
    public final static MosaicVoid VOID = new MosaicVoid();

    public static boolean isVoid(Class<?> clazz){
        if(Objects.isNull(clazz)){
            return false;
        }
        return clazz.isAssignableFrom(MosaicVoid.class) || clazz.equals(Void.class);
    }

    @Override
    public <T> T getResult(String key, Class<T> clazz) {
        return null;
    }
}

package io.github.tml.mosaic.core.tools.copy;

import java.io.*;

public class DeepCopyUtil {

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deepCopy(Object object) {
        if (object instanceof Serializable){
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(bos);
                oos.writeObject(object);
                oos.flush();
                oos.close();
                // 反序列化
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis);
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
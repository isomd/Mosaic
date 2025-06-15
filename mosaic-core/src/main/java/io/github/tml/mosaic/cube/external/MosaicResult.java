package io.github.tml.mosaic.cube.external;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Cube插件返回参数默认返回类
 */
public class MosaicResult extends AbstractMosaicResult {

    public MosaicResult(JSONObject result) {
        this.result = result;
    }

    private JSONObject result;

    public static Builder build(){
        return new Builder();
    }

    @Override
    public <T> T getResult(String key, Class<T> clazz) {
        if(result == null){
            return null;
        }
        return result.getObject(key, clazz);
    }

    public static class Builder{
        private JSONObject result;

        public Builder() {
            this.result = new JSONObject();
        }

        public Builder put(String key, Object value){
            result.put(key, value);
            return this;
        }

        public Builder map(Map<String, Object> map){
            result.putAll(map);
            return this;
        }

        public MosaicResult build(){
            return new MosaicResult(result);
        }
    }
}

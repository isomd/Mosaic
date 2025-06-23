package io.github.tml.mosaic.core.doc.core;

import com.alibaba.fastjson.JSONObject;

public class ValueBlockItem implements BlockItem{
    @Override
    public boolean assembly(JSONObject jsonObject) {
        return false;
    }
}

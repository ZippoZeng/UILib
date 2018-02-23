package com.github.zippo.uilib.entity;

import org.json.JSONObject;

/**
 * Created by Zippo on 2018/2/23.
 * Date: 2018/2/23
 * Time: 11:16:26
 */

public abstract class BaseModel implements IParse {
    @Override
    public abstract void decode(JSONObject object);

    @Override
    public abstract JSONObject encode(Object o);

    @Override
    public void release() {

    }
}

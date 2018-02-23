package com.github.zippo.uilib.entity;


import org.json.JSONObject;

/**
 * Created by eddy on 2015/4/24.
 */
public interface IParse extends IInterface {

    /**
     * jsonObject to javabean
     * @param object
     */
    void decode(JSONObject object);

    /**
     * javabean to jsonObject
     * @param o
     */
    JSONObject encode(Object o);

}

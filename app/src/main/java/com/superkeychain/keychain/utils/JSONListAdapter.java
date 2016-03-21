package com.superkeychain.keychain.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by taofeng on 3/20/16.
 */
public class JSONListAdapter<E> {
    private List<E> list;
    private JSONArray array;

    public JSONListAdapter(List<E> list) {
        this.list = list;
        array = new JSONArray();
        for (E l : list) {
            try {
                array.put(new JSONObject(l.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray getJSONArray() {
        return array;
    }

    @Override
    public String toString() {
        return array.toString();
    }
}

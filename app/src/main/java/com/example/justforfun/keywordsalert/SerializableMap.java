package com.example.justforfun.keywordsalert;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Jinghao Qiao on 2018/2/3.
 */

public class SerializableMap implements Serializable {
    private Map<String, String> map;

    public Map<String, String> getMap(){
        return map;
    }

    public void setMap(Map<String, String> map){
        this.map = map;
    }
}

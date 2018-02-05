package com.example.justforfun.keywordsalert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jinghao Qiao on 2018/2/3.
 */

public class SerializableMap implements Serializable {
    private HashMap<String, String> map;

    public HashMap<String, String> getMap(){
        return map;
    }

    public void setMap(HashMap<String, String> map){
        this.map = map;
    }
}

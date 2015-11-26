package com.example.firstproject.compactdrive;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class GoogleChildrenTree {
    private static Map<String,JSONArray> childrenByParent = new HashMap<String,JSONArray>();

    public static void populateTree(JSONObject rawResp){
        try {
            childrenByParent.clear();
            JSONArray allFiles = rawResp.getJSONArray("items");
            for(int i=0;i<allFiles.length();i++){
                JSONObject eachFile = (JSONObject)allFiles.get(i);
                JSONArray parents = eachFile.getJSONArray("parents");
                for (int j=0;j<parents.length();j++){
                    JSONObject eachParent = (JSONObject)parents.get(j);
                    String eachParentId;
                    if (eachParent.getBoolean("isRoot")){
                        eachParentId = "root";
                    }else{
                        eachParentId = eachParent.getString("id");
                    }
                    JSONArray children = childrenByParent.get(eachParentId);
                    if(children == null){
                        children = new JSONArray();
                        childrenByParent.put(eachParentId,children);
                        children = childrenByParent.get(eachParentId);
                    }
                    children.put(eachFile);
                }
            }
        } catch (JSONException je){
            Log.i("tree framing exception",je.getMessage());
        }
    }

    public static JSONArray getChildrenByParent(String parentId) {
        JSONArray temp =childrenByParent.get(parentId);
        return temp;
    }
}

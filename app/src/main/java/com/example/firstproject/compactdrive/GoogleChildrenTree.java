package com.example.firstproject.compactdrive;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleChildrenTree {
    private static Map<String,List<JSONArray>> childrenByParent = new HashMap<String,List<JSONArray>>();

    public static void populateTree(JSONObject rawResp){
        try {
            childrenByParent.clear();
            JSONArray allFiles = rawResp.getJSONArray("items");
            for(int i=0;i<allFiles.length();i++){
                JSONObject eachFile = (JSONObject)allFiles.get(i);
                Boolean isDir = eachFile.getString("mimeType").equals("application/vnd.google-apps.folder")?true:false;
                JSONArray parents = eachFile.getJSONArray("parents");
                for (int j=0;j<parents.length();j++){
                    JSONObject eachParent = (JSONObject)parents.get(j);
                    String eachParentId;
                    if (eachParent.getBoolean("isRoot")){
                        eachParentId = "root";
                    }else{
                        eachParentId = eachParent.getString("id");
                    }
                    List<JSONArray> children = childrenByParent.get(eachParentId);
                    if(children == null){
                        children = new ArrayList<JSONArray>();
                        childrenByParent.put(eachParentId,children);
                        children = childrenByParent.get(eachParentId);
                    }
                    if(isDir){
                        JSONArray dirChildren;
                        try {
                             dirChildren = children.get(0);
                        }catch(IndexOutOfBoundsException iobe){
                            dirChildren = new JSONArray();
                            children.add(dirChildren);
                            dirChildren = children.get(0);
                        }
                        dirChildren.put(eachFile);
                    }else{
                        JSONArray fileChildren;
                        try {
                            fileChildren = children.get(1);
                        }catch(IndexOutOfBoundsException iobe){
                            try {
                                fileChildren = children.get(0);
                            }catch(IndexOutOfBoundsException iob) {
                                fileChildren = new JSONArray();
                                children.add(fileChildren);
                            }
                            fileChildren = new JSONArray();
                            children.add(fileChildren);
                            fileChildren = children.get(1);
                        }
                        fileChildren.put(eachFile);
                    }
                }
            }
        } catch (JSONException je){
            Log.i("tree framing exception",je.getMessage());
        }
    }

    public static JSONArray getChildrenByParent(String parentId) {
        List<JSONArray> children =childrenByParent.get(parentId);
        JSONArray finalChildren = new JSONArray();
        if(children != null) {
            try {
                JSONArray dirChildren = children.get(0);
                JSONArray fileChildren = children.get(1);
                for (int i=0;i<dirChildren.length();i++){
                    finalChildren.put(dirChildren.getJSONObject(i));
                }
                for (int i=0;i<fileChildren.length();i++){
                    finalChildren.put(fileChildren.getJSONObject(i));
                }

            } catch (Exception e) {
                Log.e("GoogleChildrenTreeFraming", e.getMessage());
            }
        }
        return finalChildren;

    }
}

package com.example.firstproject.compactdrive;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class Children_Population{

    protected static ArrayList<GfileObject> getChilds(String parent,JSONArray fileList)
    {
        ArrayList<GfileObject> resultList = new ArrayList<>();
        try {
            int fileCount = 0;
            while (fileCount < fileList.length()) {
                JSONObject temp = (JSONObject) fileList.get(fileCount);

                JSONObject label = (JSONObject) temp.get("labels");
                JSONArray parents = (JSONArray) temp.get("parents");
                boolean isParent = false;
                if(parent.equals("root")){
                    for (int i = 0; i < parents.length(); i++) {
                        if (((JSONObject) parents.get(0)).getBoolean("isRoot"))
                            isParent = true;
                    }
                    if (label.getString("trashed").equals("false") && isParent) {
                        GfileObject tem = new GfileObject();
                        tem.setID(temp.getString("id"));
                        tem.setTitle(temp.getString("title"));
                        tem.setMimeType(temp.getString("mimeType"));
                        tem.setOwners(temp.getJSONArray("ownerNames"));
                        tem.setDom(temp.getString("modifiedDate"));
                        if(!tem.getMimeType().equals("application/vnd.google-apps.folder"))
                                tem.setUrl(temp.getString("downloadUrl"));
                        tem.setDoc(temp.getString("createdDate"));
                        tem.setParentId(parent);
                        resultList.add(tem);
                    }
                }
                else {
                    for (int i = 0; i < parents.length(); i++) {
                        if (((JSONObject) parents.get(0)).getString("id").equals(parent))
                            isParent = true;
                    }
                    if (label.getString("trashed").equals("false") && isParent) {
                        GfileObject tem = new GfileObject();
                        tem.setID(temp.getString("id"));
                        tem.setTitle(temp.getString("title"));
                        tem.setMimeType(temp.getString("mimeType"));
                        tem.setOwners(temp.getJSONArray("ownerNames"));
                        if(!tem.getMimeType().equals("application/vnd.google-apps.folder"))
                            tem.setUrl(temp.getString("downloadUrl"));
                        tem.setDom(temp.getString("modifiedDate"));
                        tem.setDoc(temp.getString("createdDate"));
                        tem.setParentId(parent);
                        resultList.add(tem);
                    }
                }
                fileCount++;
            }
        }
        catch (Exception e){
            Log.i("Error",e.getMessage());
        }
        return resultList;
    }
}
package com.example.firstproject.compactdrive;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


public class Client {

    private static String clientId= "590541311833-ur79hjs509vp74l9c2vq76e8c3mk26p3.apps.googleusercontent.com";
    private static String redirect_uri = "urn:ietf:wg:oauth:2.0:oob";
    public  static String aceToken="";
    private static String refToken="";
    public static String CODE=null;
    private static StringBuffer fileList;

    public static String generatePermissionUrl() {

        String response_type = "code";
        String approval_prompt = "force";
        String access_type = "offline";
        String scope = "https://www.googleapis.com/auth/drive";
        String endPoint = "https://accounts.google.com/o/oauth2/auth";

        String finalUrl = endPoint + "?" + "client_id=" + clientId + "&response_type=" + response_type
                + "&approval_prompt=" + approval_prompt + "&access_type=" + access_type + "&scope=" + scope
                + "&redirect_uri=" + redirect_uri;
        return finalUrl;
    }

    public static void populateTokens() {
        readTokens();
        if(aceToken != ""){
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String endPoint = "https://accounts.google.com/o/oauth2/token";
                StringBuffer response = new StringBuffer();
                try {
                    String body_string ="grant_type=authorization_code"+"&code="+CODE+"&client_id="+clientId+"&redirect_uri="+redirect_uri;
                    URL temp = new URL(endPoint);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream token_stream = con.getOutputStream();
                    token_stream.write(body_string.getBytes());
                    token_stream.flush();
                    token_stream.close();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject o = new JSONObject(response.toString());
                    aceToken = o.getString("access_token");
                    refToken = o.getString("refresh_token");
                    storeTokens(o);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void refreshToken() {
        readTokens();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String endPoint = "https://accounts.google.com/o/oauth2/token";
                StringBuffer response = new StringBuffer();
                try {
                    String body_string ="grant_type=refresh_token&client_id="+clientId+"&refresh_token="+refToken+"&redirect_uri="+redirect_uri;
                    URL temp = new URL(endPoint);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    OutputStream token_stream = con.getOutputStream();
                    token_stream.write(body_string.getBytes());
                    token_stream.flush();
                    token_stream.close();
                    int c = con.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    JSONObject o = new JSONObject(response.toString());
                    aceToken = o.getString("access_token");
                    storeTokens(o);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void readTokens(){
        Properties prop = new Properties();
        InputStream in = null;
        try{
            String storagePath = Library.context.getFilesDir().getPath();
            File dir = new File(storagePath +"/compact drive");
            if(!dir.exists()){
                return;
            }
            String filePath = storagePath +"/compact drive/G_tokens.properties";
            File tokens = new File(filePath);
            boolean fileExists = tokens.exists();
            if(!fileExists){
                return;
            }

            in = new FileInputStream(tokens);
            prop.load(in);
            if(prop != null){

                aceToken = prop.getProperty("acessToken");
                refToken = prop.getProperty("refreshToken");
            }
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void storeTokens(JSONObject object){
        Properties prop = new Properties();
        OutputStream out = null;
        try {
            String storagePath = Library.context.getFilesDir().getPath();
            File dir = new File(storagePath + "/compact drive");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filePath = storagePath + "/compact drive/G_tokens.properties";
            File tokens = new File(filePath);
            tokens.createNewFile();

            prop.setProperty("acessToken", aceToken);
            prop.setProperty("refreshToken",refToken);

            out = new FileOutputStream(filePath);
            prop.store(out,null);
        } catch(Exception e){
            e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

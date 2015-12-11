package com.example.firstproject.compactdrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class fileDownload extends AppCompatActivity {

    private String fileURL=null;
    private String filename=null;
    private String Mtype = null;
    private long fileSize;
    File my_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileURL = getIntent().getStringExtra("downURL");
        filename = getIntent().getStringExtra("filename");
        Mtype = getIntent().getStringExtra("mimeType");
        fileSize = getIntent().getLongExtra("fileSize",0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new My_Task().execute();
        setContentView(R.layout.activity_file_download);

    }
    class My_Task extends AsyncTask {

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(fileDownload.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            boolean flag = false;
            if (Client.aceToken != null) {
                try {
                    URL temp = new URL(fileURL);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    String authToken = "OAuth " + Client.aceToken;
                    con.setRequestProperty("Authorization", authToken);
                    int resCode = con.getResponseCode();
                    if (resCode == 200) {
                        flag = true;
                    } else if (resCode == 401) {
                        Client.refreshToken();
                        con = (HttpURLConnection) temp.openConnection();
                        con.setRequestMethod("GET");
                        authToken = "OAuth " + Client.aceToken;
                        con.setRequestProperty("Authorization", authToken);
                        flag=true;
                    }
                    if (flag) {
                        String storagePath = Library.context.getFilesDir().getPath();
                        File dir = new File(storagePath + "/compact drive");
                        if (dir.exists()) {
                            String filePath = storagePath + "/compact drive/" + filename;
                            my_file = new File(filePath);
                            boolean fileExists = my_file.exists();
                            if (fileSize > 0) {
                                if (fileExists) {
                                    return null;
                                } else {
                                    try {
                                        InputStream inputStream = con.getInputStream();
                                        OutputStream outputStream = new FileOutputStream(my_file);
                                        byte[] buffer = new byte[1024];
                                        int bytesRead = 0;
                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, bytesRead);
                                            outputStream.flush();
                                        }
                                        inputStream.close();
                                        outputStream.close();
                                    } catch (Exception e) {
                                        Log.i("file store problem", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Log.i("fileDownload","connection problem......");
                    }
                }
                catch(Exception e){
                    Log.i("fileDownload",e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dialog.hide();
            try {
//                Intent openIntent = new Intent(Intent.ACTION_VIEW);
//                openIntent.setDataAndType(Uri.fromFile(my_file), Mtype);
//                startActivity(openIntent);
                WebView wview  = (WebView)findViewById(R.id.display_all);
                wview.getSettings().getBuiltInZoomControls();
                wview.loadUrl("file://"+my_file.getPath());
            }
            catch (Exception e) {
                Log.e("fileDownload",e.getMessage());
            }
         }
    }
}

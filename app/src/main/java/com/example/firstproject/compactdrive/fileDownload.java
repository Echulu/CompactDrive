package com.example.firstproject.compactdrive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class fileDownload extends AppCompatActivity {

    private String fileURL=null;
    private String filename=null;
    File my_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileURL = getIntent().getStringExtra(fileURL);
        filename = getIntent().getStringExtra(filename);
        setContentView(R.layout.activity_file_download);
    }
    class My_Task extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            if (Client.aceToken != null) {
                try {
                    URL temp = new URL(fileURL);
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    String authToken = "OAuth " + Client.aceToken;
                    con.setRequestProperty("Authorization", authToken);
                    int resCode = con.getResponseCode();
                    if (resCode == 200) {
                        String storagePath = Library.context.getFilesDir().getPath();
                        File dir = new File(storagePath +"/compact drive");
                        if(dir.exists()) {
                            String filePath = storagePath + "/compact drive/"+filename;
                            File temp_file = new File(filePath);
                            boolean fileExists = temp_file.exists();
                            if (fileExists) {
                                return null;
                            } else {
                                try {
                                    ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
                                    FileOutputStream fos = new FileOutputStream(temp_file);
                                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                } catch (Exception e) {
                                    Log.i("file store problem", e.getMessage());
                                }
                            }
                        }
                    } else if (resCode == 401) {

                        Client.refreshToken();
                        HttpURLConnection con2 = (HttpURLConnection) temp.openConnection();
                        con2.setRequestMethod("GET");
                        authToken = "OAuth " + Client.aceToken;
                        con2.setRequestProperty("Authorization", authToken);
                        resCode = con2.getResponseCode();
                        if (resCode == 200) {
                            String storagePath = Library.context.getFilesDir().getPath();
                            File dir = new File(storagePath +"/compact drive");
                            if(dir.exists()) {
                                String filePath = storagePath + "/compact drive/"+filename;
                                my_file = new File(filePath);
                                boolean fileExists = my_file.exists();
                                if (fileExists) {
                                    return null;
                                } else {
                                    try {
                                        ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
                                        FileOutputStream fos = new FileOutputStream(my_file);
                                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                    } catch (Exception e) {
                                        Log.i("file store problem", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ImageView imaged = (ImageView)findViewById(R.id.displayImage);
            Bitmap myBitmap = BitmapFactory.decodeFile(my_file.getAbsolutePath());
            imaged.setImageBitmap(myBitmap);
        }
    }

}

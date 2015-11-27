package com.example.firstproject.compactdrive;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class fileDownload extends AppCompatActivity {

    GfileObject gfo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gfo = (GfileObject)getIntent().getSerializableExtra("GfileObject");
        setContentView(R.layout.activity_file_download);
    }
    class My_Task extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            if (Client.aceToken != null) {
                try {
                    URL temp = gfo.getUrl();
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    String authToken = "OAuth " + Client.aceToken;
                    con.setRequestProperty("Authorization", authToken);
                    int resCode = con.getResponseCode();
                    if (resCode == 200) {
                        String storagePath = Library.context.getFilesDir().getPath();
                        File dir = new File(storagePath +"/compact drive");
                        if(dir.exists()) {
                            String filePath = storagePath + "/compact drive/"+gfo.getTitle();
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
                            ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
                            FileOutputStream fos = new FileOutputStream("information.html");
                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//                            BufferedReader in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
//                            String inputLine;
//                            while ((inputLine = in.readLine()) != null) {
//                                fileJson.append(inputLine);
//                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

}

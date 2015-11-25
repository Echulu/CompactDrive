package com.example.firstproject.compactdrive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Library extends Activity {

    private static  String PARENT = "DEMO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_library);
        Toolbar t = (Toolbar)findViewById(R.id.toolbar);
        t.setTitle("Compact Drive");
        Button gmail = (Button)findViewById(R.id.gmail);
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent temp = new Intent(v.getContext(), Auth.class);
                startActivityForResult(temp,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Intent gmail = new Intent(Library.this,Test.class);
                startActivity(gmail);
            }
        }
    }
}

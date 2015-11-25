package com.example.firstproject.compactdrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FileAdapter extends ArrayAdapter {



    public FileAdapter(Context context,ArrayList<GfileObject> temp){
        super(context,R.layout.custom_row,temp);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater tem = LayoutInflater.from(getContext());
            View conView= tem.inflate(R.layout.custom_row, parent, false);
            TextView l = (TextView) conView.findViewById(R.id.textView);
            ImageView i = (ImageView) conView.findViewById(R.id.imageView);
            GfileObject ob =(GfileObject) getItem(position);
            l.setText(ob.getTitle());
            if(ob.getMimeType().equals("application/vnd.google-apps.folder"))
                i.setImageResource(R.drawable.gmail_folder);
            else
                i.setImageResource(R.drawable.gmail_file);
        return conView;
    }

}

package com.example.firstproject.compactdrive;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class appcentral extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    public static Stack<String> parentStack = new Stack<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Welcome");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_gmail) {
            if (Client.aceToken != null){
                final ImageButton logout = (ImageButton) findViewById(R.id.inout);
            new Gmail().execute();
            toolbar.setTitle("Gmail");
            logout.setVisibility(View.VISIBLE);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(appcentral.this)
                            .setTitle("Logout")
                            .setMessage("Are You Sure?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String storagePath = Library.context.getFilesDir().getPath();
                                    File dir = new File(storagePath + "/compact drive");
                                    if (!dir.exists()) {
                                        return;
                                    }
                                    String filePath = storagePath + "/compact drive/G_tokens.properties";
                                    File tokens = new File(filePath);
                                    boolean fileExists = tokens.exists();
                                    if (fileExists) {
                                        tokens.delete();
                                        ListView list = (ListView) findViewById(R.id.listView);
                                        list.setAdapter(null);
                                        toolbar.setTitle("Welcome");
                                        logout.setVisibility(View.INVISIBLE);
                                        Client.aceToken = null;
                                        Client.CODE = null;
                                    }
                                    Toast.makeText(appcentral.this, "Signed Out", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });
        }
            else{
                startActivityForResult(new Intent(this, Auth.class), 1);
            }
        } else if (id == R.id.nav_onedrive) {
            toolbar .setTitle("One Drive");
        } else if (id == R.id.nav_dropbox) {
            toolbar .setTitle("Drop Box");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
            new Gmail().execute();
    }

    public class Gmail extends AsyncTask {

        private StringBuffer fileJson = new StringBuffer();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Client.populateTokens();
            String t = Client.aceToken;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if (Client.aceToken != null) {
                try {
                    URL temp = new URL(" https://www.googleapis.com/drive/v2/files");
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    String authToken = "OAuth " + Client.aceToken;
                    con.setRequestProperty("Authorization", authToken);
                    int resCode = con.getResponseCode();
                    if (resCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            fileJson.append(inputLine);
                        }
                    } else if (resCode == 401) {

                        Client.refreshToken();
                        HttpURLConnection con2 = (HttpURLConnection) temp.openConnection();
                        con2.setRequestMethod("GET");
                        authToken = "OAuth " + Client.aceToken;
                        con2.setRequestProperty("Authorization", authToken);
                        resCode = con2.getResponseCode();
                        if (resCode == 200) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                fileJson.append(inputLine);
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
        protected void onPostExecute(final Object o) {
            super.onPostExecute(o);
            ArrayList<GfileObject> resultList;
            try {
                GoogleChildrenTree.populateTree(new JSONObject(fileJson.toString()));
                resultList = Children_Population.getChilds("root",GoogleChildrenTree.getChildrenByParent("root"));

                FileAdapter ap = new FileAdapter(appcentral.this,resultList);
                final ListView list = (ListView)findViewById(R.id.listView);
                list.setAdapter(ap);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        GfileObject temp = (GfileObject) parent.getItemAtPosition(position);
                        if(temp.getMimeType().equals("application/vnd.google-apps.folder")) {
                            try {

                                ArrayList<GfileObject> adapter_result;
                                adapter_result = Children_Population.getChilds(temp.getID(), GoogleChildrenTree.getChildrenByParent(temp.getID()));
                                parentStack.push(temp.getParentId());
                                FileAdapter ap = new FileAdapter(appcentral.this, adapter_result);
                                ListView list = (ListView) findViewById(R.id.listView);
                                list.setAdapter(ap);
                            } catch (Exception e) {
                                Log.e("appcentral :", e.getMessage());
                            }
                        }
                        else{
                            Intent t = new Intent(appcentral.this,fileDownload.class);
                            URL k = temp.getUrl();
                            t.putExtra("downURL", k.toString());
                            t.putExtra("MType", temp.getMimeType().toString());
                            t.putExtra("filename",temp.getTitle().toString());
                            t.putExtra("fileSize",temp.getSize());
                            t.putExtra("mimeType",temp.getMimeType());
                            startActivity(t);
                        }
                    }
                });

                list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                                .getSystemService(LAYOUT_INFLATER_SERVICE);

                        View popupView = layoutInflater.inflate(R.layout.item_popup, null);
                        GfileObject temp = (GfileObject) parent.getItemAtPosition(position);
                        TextView name = (TextView) popupView.findViewById(R.id.popup_name);
                        TextView dateOfCreation = (TextView) popupView.findViewById(R.id.dateofcreation);
                        TextView dateOfModification = (TextView) popupView.findViewById(R.id.dateofModification);
                        TextView owners = (TextView) popupView.findViewById(R.id.ownerslist);
                        name.setText(":" + temp.getTitle());
                        dateOfCreation.setText(":" + temp.getDoc());
                        dateOfModification.setText(":" + temp.getDom());
                        owners.setText(":" + temp.getOwners());
                        final PopupWindow my_popup = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
                        my_popup.setOutsideTouchable(true);
                        my_popup.setFocusable(true);
                        my_popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        my_popup.setTouchInterceptor(new View.OnTouchListener() {

                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                    my_popup.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        });
                        return true;
                    }
                });
            }
            catch (Exception e) {
                Log.e(" appcentral class", e.getMessage());
            }
        }
    }
    @Override
    public void onBackPressed() {

        ArrayList<GfileObject> resultList;
        try {
            String parentId = parentStack.pop();
            resultList = Children_Population.getChilds(parentId,GoogleChildrenTree.getChildrenByParent(parentId));
            FileAdapter ap = new FileAdapter(appcentral.this,resultList);
            final ListView list = (ListView)findViewById(R.id.listView);
            list.setAdapter(ap);
        }
        catch(EmptyStackException ese){
            Intent gmail = new Intent(appcentral.this,Library.class);
            startActivity(gmail);
        }
        catch (Exception e){
            Log.i("appcentral class2", e.getMessage());
        }
    }

}

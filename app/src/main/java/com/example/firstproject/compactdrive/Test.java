package com.example.firstproject.compactdrive;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class Test extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    public static Stack<AdapterView> parentStack = new Stack<AdapterView>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Compact Drive");
        Toast.makeText(getApplication().getBaseContext(),"Google Drive Connected",
                Toast.LENGTH_SHORT).show();




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gmail) {
            toolbar .setTitle("Gmail");
            new Gmail().execute();
        } else if (id == R.id.nav_onedrive) {
            toolbar .setTitle("One Drive");
        } else if (id == R.id.nav_dropbox) {
            toolbar .setTitle("Drop Box");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class Gmail extends AsyncTask {

        private StringBuffer fileJson = new StringBuffer();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Client.populateTokens();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if (Client.aceToken != null) {
                try {
                    URL temp = new URL("https://www.googleapis.com/drive/v2/files");
                    HttpURLConnection con = (HttpURLConnection) temp.openConnection();
                    con.setRequestMethod("GET");
                    String authToken = "OAuth " + Client.aceToken;
                    con.setRequestProperty("Authorization", authToken);
                    int c = con.getResponseCode();
                    if (con.getResponseCode() == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            fileJson.append(inputLine);
                        }
                    } else if (c == 401) {

                        Client.refreshToken();
                        HttpURLConnection con2 = (HttpURLConnection) temp.openConnection();
                        con2.setRequestMethod("GET");
                        authToken = "OAuth " + Client.aceToken;
                        con2.setRequestProperty("Authorization", authToken);
                        c = con2.getResponseCode();
                        if (con2.getResponseCode() == 200) {
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
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ArrayList<GfileObject> resultList = new ArrayList<>();
            try {
                GoogleChildrenTree.populateTree((JSONObject)new JSONObject(fileJson.toString()));
                resultList = Children_Population.getChilds("root",GoogleChildrenTree.getChildrenByParent("root"));
                FileAdapter ap = new FileAdapter(Test.this,resultList);
                final ListView list = (ListView)findViewById(R.id.listView);
                list.setAdapter(ap);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            parentStack.push(parent);
                            ArrayList<GfileObject> adapter_result;
                            GfileObject temp = (GfileObject) parent.getItemAtPosition(position);
                            adapter_result = Children_Population.getChilds(temp.getID(), GoogleChildrenTree.getChildrenByParent(temp.getID()));
                            FileAdapter ap = new FileAdapter(Test.this, adapter_result);
                            ListView list = (ListView) findViewById(R.id.listView);
                            list.setAdapter(ap);
                        }
                        catch(Exception e){
                            Log.e("Test :",e.getMessage());
                        }
                    }
                });
            }
            catch (Exception e){
                Log.i("Exception in Test class", e.getMessage());
            }
        }
    }
    @Override
    public void onBackPressed() {
        ListView list = (ListView)findViewById(R.id.listView);

        list.setAdapter((ListAdapter)parentStack.pop().getAdapter());

    }
}

package com.example.sesom1.android_push_notification;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private GoogleCloudMessaging gcm;
    private String regId;
    private EditText eText;
    private Button btn;
    private final String senderId = "your_app_sender_id";
    private final String scope = "GCM";
    private final String googleAppKey = "your_app_google_key";
    private String toToken = "token_request_from_google_server_by_divice_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();
        listener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getTokenGCM();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initial(){
        eText = (EditText) findViewById(R.id.edittext);
        btn = (Button) findViewById(R.id.button);
    }

    private void listener() {
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = eText.getText().toString();
                Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
                sentPushNotification(str);
            }
        });
    }
    private void getTokenGCM() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    return InstanceID.getInstance(MainActivity.this).getToken(senderId, scope, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.i("On", "token= " + result);
            }
        }.execute();
    }

    private void sentPushNotification(final String message){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                try {
                    JSONObject jGcmData = new JSONObject();
                    JSONObject jData = new JSONObject();
                    try {
                        jData.put("message", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jGcmData.put("to", toToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jGcmData.put("data", jData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    url = new URL("https://android.googleapis.com/gcm/send");
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("Authorization", "key=" + googleAppKey);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        OutputStream outputStream = conn.getOutputStream();
                        outputStream.write(jGcmData.toString().getBytes());
                        // Read GCM response.
                        InputStream inputStream = null;
                        inputStream = conn.getInputStream();
                        Log.i("On", "respond= "+inputStream.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}

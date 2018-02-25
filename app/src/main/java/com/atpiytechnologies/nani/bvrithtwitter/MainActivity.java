package com.atpiytechnologies.nani.bvrithtwitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    ListView listview;
    String[] data;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int delay = 1000;   // delay for 5 sec.
        int interval = 10000;  // iterate every sec.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.e(TAG, "Calling Nani");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nani();
                    }
                });
            }
        }, delay, interval);
    }

    public void nani() {
        if (Config.isNetworkStatusAvailable(getApplicationContext())) {
            new getTweets().execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No Internet Connection").setTitle("Information");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public class getTweets extends AsyncTask<Void, Void, Void> {

        Dialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loader);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String url = "http://192.168.43.10:3000/retrieveTweets";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            Log.e(TAG, "URL : " + url);
            Log.e(TAG, "Got Response from url!");

            if (!jsonStr.equals("Nothing")) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray tweets = jsonObj.getJSONArray("tweets");
                    int length = tweets.length();
                    data = new String[length];

                    // looping through the tweets
                    for (int i = 0; i < length; i++) {
                        JSONObject c = tweets.getJSONObject(i);
                        data[i] = c.getString("screen_name") + "\n";
                        data[i] += c.getString("text");
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    flag = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.v(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(flag == true){
                detailview();
            }
        }

        public void detailview() {

            listview = (ListView) findViewById(R.id.lv);
            ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_main_single_lv, data);
            listview.setAdapter(adapter);
        }
    }
}

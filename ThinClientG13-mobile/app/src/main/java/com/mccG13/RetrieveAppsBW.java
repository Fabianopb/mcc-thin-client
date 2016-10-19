package com.mccG13;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.coboltforge.dontmind.multivnc.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fabiano.brito on 19/10/16.
 */

public class RetrieveAppsBW extends AsyncTask<String,Void,String> {
    public MainActivity source = null;
    Context context;
    ProgressDialog loading;

    public RetrieveAppsBW(MainActivity fl, Context ctx) {
        source = fl;
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String get_apps_url = "http://104.199.9.28/getapps/";

        try {
            String token = params[0];
            String blank = params[1];
            URL url = new URL(get_apps_url);

            // creating an http connection to communicate with url
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            String credentials = token + ":" + blank;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httpURLConnection.setRequestProperty("Authorization", "Basic " + credBase64);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            // reading answer from server
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String result = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            bufferedReader.close();
            inputStream.close();

            httpURLConnection.disconnect();
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        loading = ProgressDialog.show(context, "Getting apps list...", null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        if(result != null) {
            final String[] appsArray = new String[2];
            try {
                JSONObject jsonObj = new JSONObject(result);
                appsArray[0] = jsonObj.getString("openoffice");
                appsArray[1] = jsonObj.getString("inkscape");
            } catch (JSONException e) {
                Log.e("Parsing error", e.toString());
            }
            if(appsArray[0] != null) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        Intent intent = new Intent(context, AppSelectionActivity.class);
                        intent.putExtra("appsArray", appsArray);
                        source.startActivity(intent);
                    }
                }, 1000);
            } else {
                loading.dismiss();
                Toast.makeText(context, R.string.get_apps_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            loading.dismiss();
            Toast.makeText(context, R.string.get_apps_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

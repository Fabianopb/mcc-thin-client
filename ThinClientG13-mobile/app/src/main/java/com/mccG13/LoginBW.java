package com.mccG13;

/**
 * Created by Иван on 16.10.2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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



// sending JSON request to server and getting answer
// Login stage: check if username:password are correct
public class LoginBW extends AsyncTask<String,Void,String> {
    public MainActivity source = null;
    Context context;
    ProgressDialog loading;

    public LoginBW(MainActivity fl, Context ctx) {
        source = fl;
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String login_url = "http://104.199.9.28/token/";

        try {
            String username = params[0];
            String password = params[1];
            URL url = new URL(login_url);

            // creating an http connection to communicate with url
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
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
        loading = ProgressDialog.show(context, source.getResources().getString(R.string.login_dialog), null, true, true);
    }

    @Override
    protected void onPostExecute(String result) {
        if(result != null) {
            final String token;
            try {
                JSONObject jsonObj = new JSONObject(result);
                token = jsonObj.getString("token");
                if (!token.equals("")) {
                    SharedPreferences sharedPref = source.getSharedPreferences("sessionData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", token);
                    editor.apply();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loading.dismiss();
                            RetrieveAppsBW retrieveAppsBW = new RetrieveAppsBW(source, context);
                            retrieveAppsBW.execute(token, "");
                        }
                    }, 1000);
                } else {
                    loading.dismiss();
                    Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.e("Parsing error", e.toString());
            }
        } else {
            loading.dismiss();
            Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
        }


        //From here we call /getapps/ to bring a list of the available applications
        //Only then we start the next activity with the applications for the user to select
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

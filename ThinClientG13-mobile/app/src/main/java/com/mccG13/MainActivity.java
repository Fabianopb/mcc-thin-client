package com.mccG13;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.coboltforge.dontmind.multivnc.R;


public class MainActivity extends Activity {

    EditText etLogUsername, etLogPassword;
    String username, password;

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public String getSharedPreferences(String key) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, "");
        return value;
    }

    public void putSharedPreferences(String key, String value) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLogUsername = (EditText) findViewById(R.id.etLogUsername);
        etLogPassword = (EditText) findViewById(R.id.etLogPassword);




        // Login button
        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etLogUsername.getText().toString();
                password = etLogPassword.getText().toString();


                if (isOnline()) {

                    LoginBW loginBW = new LoginBW(MainActivity.this, MainActivity.this);
                    loginBW.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password);

                    //RetrieveAppsBW retrieveAppsBWBW = new RetrieveAppsBW(MainActivity.this, MainActivity.this);
                    //retrieveAppsBWBW.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getSharedPreferences("token"), "");

                } else {
                    Toast.makeText(getApplicationContext(), R.string.noInternet, Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}

package com.mccG13;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coboltforge.dontmind.multivnc.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startLogin(View view) {
        Intent intent = new Intent(this, AppSelectionActivity.class);
        startActivity(intent);
    }
}

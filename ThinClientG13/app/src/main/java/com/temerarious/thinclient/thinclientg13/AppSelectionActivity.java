package com.temerarious.thinclient.thinclientg13;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AppSelectionActivity extends AppCompatActivity {

    public static final String CONNECTION = "com.coboltforge.dontmind.multivnc.CONNECTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);
    }

    public void startApplication(View view) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.coboltforge.dontmind.multivnc", "com.coboltforge.dontmind.multivnc.VncCanvasActivity"));
        intent.setData(Uri.parse("vnc://104.199.2.241:5901/C24bit/tReFre4r"));
        startActivity(intent);
    }
}

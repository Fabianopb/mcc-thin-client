package com.mccG13;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.coboltforge.dontmind.multivnc.R;

public class AppSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);
        Bundle extras = getIntent().getExtras();
        String[] appsArray = extras.getStringArray("appsArray");
        Toast.makeText(this, appsArray[0] + " and " + appsArray[1], Toast.LENGTH_SHORT).show();
    }

    public void startOpenOffice(View view) {

        String instanceIP = ""; // Put the instance IP here!!
        String cloudPort = ":5901";
        String colorScheme = "/C24bit";
        String instancePwd = "/tReFre4r";

        Intent intent = new Intent(this, com.coboltforge.dontmind.multivnc.VncCanvasActivity.class);
        intent.setData(Uri.parse("vnc://" + instanceIP + cloudPort + colorScheme + instancePwd));
        startActivity(intent);
    }
}

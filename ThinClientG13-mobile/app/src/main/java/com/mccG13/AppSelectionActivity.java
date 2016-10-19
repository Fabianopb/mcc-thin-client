package com.mccG13;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coboltforge.dontmind.multivnc.R;

public class AppSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        Bundle extras = getIntent().getExtras();
        final String[] appsArray = extras.getStringArray("appsArray");

        AppListAdapter adapter = new AppListAdapter(this, appsArray);
        ListView list = (ListView) findViewById(R.id.apps_listview);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Selecteditem = appsArray[+position];
                Toast.makeText(getApplicationContext(), Selecteditem, Toast.LENGTH_SHORT).show();
                // TODO: call the start url to start the virtual machine
                // TODO: with the response from the virtual machine finally start the multiVNC using the method below
            }
        });
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

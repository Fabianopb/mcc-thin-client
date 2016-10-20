package com.mccG13;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppSelectionActivity extends Activity {

    public AppSelectionActivity source = this;

    /*public AppSelectionActivity(AppSelectionActivity fl, Context ctx) {
        source = fl;
        context = ctx;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selection);

        Log.v("WOOT", "created activity");

        Bundle extras = getIntent().getExtras();
        final String appsJSONString = extras.getString("appsJSONString");

        try {
            Log.v("WOOT", "trying to parse JSON");
            JSONObject jsonObj = new JSONObject(appsJSONString);
            JSONArray jsonArray = jsonObj.getJSONArray("apps");
            int numOfApps = jsonArray.length();
            final String[] readableNames = new String[jsonArray.length()];
            final String[] instancesNames = new String[jsonArray.length()];
            for(int i = 0; i < numOfApps; i++) {
                readableNames[i] = jsonArray.getJSONObject(i).getString("readableName");
                instancesNames[i] = jsonArray.getJSONObject(i).getString("instanceName");
            }
            ListView list = (ListView) findViewById(R.id.apps_listview);
            AppListAdapter adapter = new AppListAdapter(this, instancesNames, readableNames);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences sharedPref = source.getSharedPreferences("sessionData", Context.MODE_PRIVATE);
                    String token = sharedPref.getString("token", "");

                    StartVMBW startVMBW = new StartVMBW(AppSelectionActivity.this, AppSelectionActivity.this);
                    startVMBW.execute(token, "", instancesNames[position]);

                }
            });
        } catch (JSONException e) {
            Log.e("Parsing error", e.toString());
        }

    }

    public void startVirtualApp(String instanceIP) {

        String cloudPort = ":5901";
        String colorScheme = "/C24bit";
        String instancePwd = "/tReFre4r";

        Intent intent = new Intent(this, com.coboltforge.dontmind.multivnc.VncCanvasActivity.class);
        intent.setData(Uri.parse("vnc://" + instanceIP + cloudPort + colorScheme + instancePwd));
        startActivity(intent);
    }
}

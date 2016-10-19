package com.mccG13;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coboltforge.dontmind.multivnc.R;

/**
 * Created by fabiano.brito on 19/10/16.
 */

public class AppListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;

    public AppListAdapter(Activity context, String[] itemname) {
        super(context, R.layout.app_list_item, itemname);

        this.context = context;
        this.itemname = itemname;
    }

    public View getView(int i, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.app_list_item, null,true);

        TextView appName = (TextView) rowView.findViewById(R.id.app_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.app_icon);

        appName.setText(itemname[i]);
        int id = context.getResources().getIdentifier(itemname[i].toLowerCase(), "drawable", context.getPackageName());
        imageView.setImageResource(id);

        return rowView;

    };
}

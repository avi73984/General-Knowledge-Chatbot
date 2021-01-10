package com.android.chatBot.admin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



public class DrawerlistAdapter extends BaseAdapter {

    private Context con;
    ArrayList<String> drawitem;
    public DrawerlistAdapter(Context c, ArrayList<String> drawitem)
    {
        con=c;
        this.drawitem=drawitem;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return drawitem.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return drawitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) con.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawerlist_item, null);
        }
        String s[]=drawitem.get(position).split(",");
        ImageView img=(ImageView) convertView.findViewById(R.id.icon);
        TextView txt=(TextView) convertView.findViewById(R.id.title);

        img.setImageResource(Integer.parseInt(s[1]));
        txt.setText(s[0]);

        return convertView;
    }
}

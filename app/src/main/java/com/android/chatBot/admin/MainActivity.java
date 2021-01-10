package com.android.chatBot.admin;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    ArrayList<String> Newdraweritems;
    private DrawerlistAdapter adapter;
    Dialog mDialog;
    int Position = -1;
    SharedPreferences pref;
    String userId_Pref = "";

    String[] titlearray = new String[]{"Questions", "User", "Feedback", "Logout"};
    int[] titleicon = new int[]{R.drawable.questions, R.drawable.users, R.drawable.feedback, R.drawable.logout};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new Dialog(MainActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        pref = getSharedPreferences("ChatBot_Admin", Context.MODE_PRIVATE);
        userId_Pref = pref.getString("UserId", "");

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mDrawerList = (ListView) findViewById(R.id.listView1);


        Newdraweritems = new ArrayList<String>();
        for (int i = 0; i < titlearray.length; i++) {
            Newdraweritems.add(titlearray[i] + "," + titleicon[i]);
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        adapter = new DrawerlistAdapter(getApplicationContext(), Newdraweritems);
        mDrawerList.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                try {
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                } catch (Exception e) {
                }
            }

            public void onDrawerOpened(View drawerView) {
                try {
                    getSupportActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu();
                } catch (Exception e) {
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        displayView(0);
//        mDrawerList.setBackgroundResource(R.drawable.backlist);


    }


    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void displayView(int position) {
        android.support.v4.app.Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new QuestionFragment();
                break;
            case 1:
                fragment = new UserFragment();
                break;
            case 2:
                fragment = new FeedbackFragment();
                break;

            case 3:

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserId", "");
                editor.commit();

                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;

            default:
                break;
        }

        if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_screen_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(titlearray[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {

            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    @Override
    public void onBackPressed() {
        finish();
    }


}

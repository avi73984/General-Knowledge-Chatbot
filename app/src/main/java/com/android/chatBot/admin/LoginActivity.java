package com.android.chatBot.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    protected EditText EmailID, Password;
    protected Button SignIn;
    protected RelativeLayout relativeLayout;
    Dialog mDialog;
    SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("ChatBot_Admin", Context.MODE_PRIVATE);
        String UserIdPref = pref.getString("UserId", "");

        mDialog = new Dialog(LoginActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);


        if (UserIdPref.compareTo("") != 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            setContentView(R.layout.login_layout);
            getSupportActionBar().hide();


            EmailID = (EditText) findViewById(R.id.userid);
            Password = (EditText) findViewById(R.id.pass);
            SignIn = (Button) findViewById(R.id.login);
            relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);


            SignIn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (EmailID.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "User Name is required", Snackbar.LENGTH_SHORT).show();
                                EmailID.requestFocus();

                            } else if (Password.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, "Password is required", Snackbar.LENGTH_SHORT).show();
                                Password.requestFocus();
                            } else {

                                new LoginTask().execute(EmailID.getText().toString(), Password.getText().toString());
                            }
                        }
                    }
            );

        }


    }


    public void onBackPressed() {
        finish();
    }


    public class LoginTask extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.login(params[0], params[1]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            mDialog.dismiss();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
                ad.setTitle("Unable to Connect!");
                ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                ad.show();

            } else {

                try {
                    JSONObject json = new JSONObject(s);
                    String StatusValue = json.getString("status");

                    if (StatusValue.compareTo("false") == 0) {
                        Snackbar.make(relativeLayout, "Invalid Credential", Snackbar.LENGTH_SHORT).show();

                    } else if (StatusValue.compareTo("true") == 0) {

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("UserId", EmailID.getText().toString().trim());
                        editor.apply();
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


}

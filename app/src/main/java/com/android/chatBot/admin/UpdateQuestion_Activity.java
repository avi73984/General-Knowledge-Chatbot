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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;


public class UpdateQuestion_Activity extends AppCompatActivity {

    protected RelativeLayout relativeLayout;
    Dialog mDialog;
    SharedPreferences pref;
    EditText TopicText, MainKeyText, Key1, Key2, Key3, Key4, Key5, AnswerText;
    Button SubmitBtn;
    String QuestionId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question_layout);

        pref = getSharedPreferences("ChatBot_Admin", Context.MODE_PRIVATE);
        String UserIdPref = pref.getString("UserId", "");

        mDialog = new Dialog(UpdateQuestion_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        getSupportActionBar().setTitle("Update Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        init();
    }


    public void init() {

        TopicText = (EditText) findViewById(R.id.topic_text);
        MainKeyText = (EditText) findViewById(R.id.mainkey_text);
        Key1 = (EditText) findViewById(R.id.key1_text);
        Key2 = (EditText) findViewById(R.id.key2_text);
        Key3 = (EditText) findViewById(R.id.key3_text);
        Key4 = (EditText) findViewById(R.id.key4_text);
        Key5 = (EditText) findViewById(R.id.key5_text);
        AnswerText = (EditText) findViewById(R.id.answer_text);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_addscreen);
        SubmitBtn = (Button) findViewById(R.id.submit_btn);

        Intent intent = getIntent();
        QuestionId = intent.getStringExtra("QuestionId");
        final String topicText = intent.getStringExtra("TopicText");
        final String mainText = intent.getStringExtra("MainKey");
        final String key1 = intent.getStringExtra("Key1");
        final String key2 = intent.getStringExtra("key2");
        final String key3 = intent.getStringExtra("Key3");
        final String key4 = intent.getStringExtra("Key4");
        final String key5 = intent.getStringExtra("Key5");
        final String ansText = intent.getStringExtra("AnsText");
        final String likeCount = intent.getStringExtra("LikeCount");
        final String dislikeCount = intent.getStringExtra("DisLikeCount");


        TopicText.setText(topicText);
        MainKeyText.setText(mainText);
        Key1.setText(key1);
        Key2.setText(key2);
        Key3.setText(key3);
        Key4.setText(key4);
        Key5.setText(key5);
        AnswerText.setText(ansText);

        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkCriteria()) {

                    if (TopicText.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Topic Name is required", Snackbar.LENGTH_SHORT).show();
                        TopicText.requestFocus();

                    } else if (MainKeyText.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Main Key is required", Snackbar.LENGTH_SHORT).show();
                        MainKeyText.requestFocus();

                    } else if (Key1.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Key1 is required", Snackbar.LENGTH_SHORT).show();
                        Key1.requestFocus();

                    } else if (Key2.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Key2 is required", Snackbar.LENGTH_SHORT).show();
                        Key2.requestFocus();

                    } else if (AnswerText.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Answer is required", Snackbar.LENGTH_SHORT).show();
                        AnswerText.requestFocus();

                    } else {

//                        string qid,string topic, string mainkey, string key1, string key2,
//                                string key3, string key4, string key5, string ans

                        new UpdateQuestionTask().execute(QuestionId, TopicText.getText().toString(), MainKeyText.getText().toString(),
                                Key1.getText().toString(), Key2.getText().toString(), Key3.getText().toString(),
                                Key4.getText().toString(), Key5.getText().toString(), AnswerText.getText().toString());

                    }
                } else {
                    new AlertDialog.Builder(UpdateQuestion_Activity.this)
                            .setMessage("All fields are mandatary. Please enter all details")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete_option) {

            AlertDialog.Builder ad = new AlertDialog.Builder(UpdateQuestion_Activity.this);
            ad.setTitle("Delete!");
            ad.setMessage("Are you sure you want to delete?");

            ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    new DeleteQuestionTask().execute(QuestionId);
                }
            });

            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.show();
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class UpdateQuestionTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.UpdateQuest(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
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
            mDialog.dismiss();

//            Toast.makeText(UpdateQuestion_Activity.this, s, Toast.LENGTH_SHORT).show();


            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(UpdateQuestion_Activity.this);
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

                    if (StatusValue.compareTo("true") == 0) {
                        Snackbar.make(relativeLayout, "Question Updated Successfully", Snackbar.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }

                } catch (Exception e) {
                    Toast.makeText(UpdateQuestion_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    protected boolean checkCriteria() {
        boolean b = true;
        if ((TopicText.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public class DeleteQuestionTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.DeleteQuest(params[0]);
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
            mDialog.dismiss();

//            Toast.makeText(UpdateQuestion_Activity.this, s, Toast.LENGTH_SHORT).show();


            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(UpdateQuestion_Activity.this);
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

                    if (StatusValue.compareTo("true") == 0) {
                        Snackbar.make(relativeLayout, "Question Deleted Successfully", Snackbar.LENGTH_SHORT).show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }

                } catch (Exception e) {
                    Toast.makeText(UpdateQuestion_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


}

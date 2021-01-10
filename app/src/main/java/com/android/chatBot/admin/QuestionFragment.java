package com.android.chatBot.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nevon-Sony on 02-Feb-18.
 */

public class QuestionFragment extends Fragment {

    protected View mView;
    SharedPreferences pref;
    ListView list;
    FloatingActionButton floatingActionButton;
    Dialog mDialog;

    ArrayList<String> QidArray, TopicArray, MainKeyArray, Key1Array, Key2Array, Key3Array, Key4Array, Key5Array;
    ArrayList<String> AnsArray, LikeArray, DislikeArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.question_paper_layout, container, false);


        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        list = (ListView) mView.findViewById(R.id.question_list);
        floatingActionButton = (FloatingActionButton) mView.findViewById(R.id.addFloatButton);

        pref = getActivity().getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        String UserId = pref.getString("UserId", "");


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddQuestion_Activity.class);
                startActivity(intent);

            }
        });


        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (isAdded()) {
            new getQuestions_Task().execute();
        }
    }


    public class getQuestions_Task extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getQuest();
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

            QidArray = new ArrayList<String>();
            TopicArray = new ArrayList<String>();
            MainKeyArray = new ArrayList<String>();
            Key1Array = new ArrayList<String>();
            Key2Array = new ArrayList<String>();
            Key3Array = new ArrayList<String>();
            Key4Array = new ArrayList<String>();
            Key5Array = new ArrayList<String>();
            AnsArray = new ArrayList<String>();
            LikeArray = new ArrayList<String>();
            DislikeArray = new ArrayList<String>();

            mDialog.dismiss();

            if (s.contains("Unable to resolve host")) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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

                    if (StatusValue.compareTo("no") == 0) {
                        Toast.makeText(getActivity(), "No question added", Toast.LENGTH_SHORT).show();


                    } else if (StatusValue.compareTo("ok") == 0) {

                        JSONArray result = json.getJSONArray("Data");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject res = result.getJSONObject(i);

                            //qid,topic,mainkey,k1,k2,k3,k4,k5,ans,like,dislike
                            QidArray.add(res.getString("data0"));
                            TopicArray.add(res.getString("data1"));
                            MainKeyArray.add(res.getString("data2"));

                            Key1Array.add(res.getString("data3"));
                            Key2Array.add(res.getString("data4"));
                            Key3Array.add(res.getString("data5"));
                            Key4Array.add(res.getString("data6"));
                            Key5Array.add(res.getString("data7"));

                            AnsArray.add(res.getString("data8"));
                            LikeArray.add(res.getString("data9"));
                            DislikeArray.add(res.getString("data10"));

                        }

                        Adapter adapter = new Adapter(getActivity(), QidArray);
                        list.setAdapter(adapter);

                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.question_list_row, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.question_list_row, null, true);

            TextView TopicText = (TextView) v.findViewById(R.id.topic_text);
            TextView MainKey = (TextView) v.findViewById(R.id.mainkey_text);
            TextView SubKey = (TextView) v.findViewById(R.id.keys_text);
            TextView AnsText = (TextView) v.findViewById(R.id.ans_text);
            TextView LikeCount = (TextView) v.findViewById(R.id.like_count);
            TextView DisLikeCount = (TextView) v.findViewById(R.id.dislike_count);

            TopicText.setText(TopicArray.get(position));

            String mainkey = "<b>MainKey : <b>" + MainKeyArray.get(position);
            MainKey.setText(Html.fromHtml(mainkey));

            SubKey.setText(Key1Array.get(position) + "," + Key2Array.get(position));
            AnsText.setText(AnsArray.get(position));
            LikeCount.setText(LikeArray.get(position));
            DisLikeCount.setText(DislikeArray.get(position));


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), UpdateQuestion_Activity.class);
                    intent.putExtra("QuestionId", QidArray.get(position));
                    intent.putExtra("TopicText", TopicArray.get(position));
                    intent.putExtra("MainKey", MainKeyArray.get(position));
                    intent.putExtra("Key1", Key1Array.get(position));
                    intent.putExtra("key2", Key2Array.get(position));
                    intent.putExtra("Key3", Key3Array.get(position));
                    intent.putExtra("Key4", Key4Array.get(position));
                    intent.putExtra("Key5", Key5Array.get(position));
                    intent.putExtra("AnsText", AnsArray.get(position));
                    intent.putExtra("LikeCount", LikeArray.get(position));
                    intent.putExtra("DisLikeCount", DislikeArray.get(position));
                    startActivity(intent);
                }
            });


            return v;
        }
    }


}

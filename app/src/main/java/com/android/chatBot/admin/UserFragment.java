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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;



public class UserFragment extends Fragment {

    protected View mView;
    SharedPreferences pref;
    ListView list;
    FloatingActionButton floatingActionButton;
    Dialog mDialog;
    ArrayList<String> NameArray, EmailArray, ContactAarray;


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
            new getUser_Task().execute();
        }
    }


    public class getUser_Task extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.viewUser();
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

            NameArray = new ArrayList<String>();
            EmailArray = new ArrayList<String>();
            ContactAarray = new ArrayList<String>();


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
                        Toast.makeText(getActivity(), "No User added", Toast.LENGTH_SHORT).show();


                    } else if (StatusValue.compareTo("ok") == 0) {

                        JSONArray result = json.getJSONArray("Data");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject res = result.getJSONObject(i);

                            //name,email,cont
                            NameArray.add(res.getString("data0"));
                            EmailArray.add(res.getString("data1"));
                            ContactAarray.add(res.getString("data2"));

                        }

                        Adapter adapter = new Adapter(getActivity(), NameArray);
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
            super(context, R.layout.user_list_row, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.user_list_row, null, true);

            TextView UserName = (TextView) v.findViewById(R.id.user_name);
            TextView EmailText = (TextView) v.findViewById(R.id.user_email);
            TextView ContactText = (TextView) v.findViewById(R.id.user_contact);

            UserName.setText(NameArray.get(position));
            EmailText.setText(EmailArray.get(position));
            ContactText.setText(ContactAarray.get(position));

            return v;
        }
    }


}

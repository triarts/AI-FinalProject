package com.personalapp.ai_final_project;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    EditText etURL;
    Button btnCheck,btnPatient;
    TextView tvSavedUrL;
    clickListener clickListener = new clickListener();
    Global glob = new Global();
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        etURL = (EditText) findViewById(R.id.etURL);
        tvSavedUrL = (TextView) findViewById(R.id.tvSavedURL);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnPatient = (Button) findViewById(R.id.btnPatient);


        btnCheck.setOnClickListener(clickListener);
        btnPatient.setOnClickListener(clickListener);

        //write
        //1 tony stark
        //2 chris evan
        sharedPref = getSharedPreferences(glob.SETTING_FLAG, MODE_PRIVATE);
        editor = sharedPref.edit();
        int lastPatientId = sharedPref.getInt(glob.patientId, 0);
        if(lastPatientId == 0)
        {
            editor.putInt(glob.patientId, 1);
            //editor.putString(glob.patientName, "Tony");
            editor.apply();
        }
        else
        {
            btnPatient.setText("Patient "+lastPatientId);
        }
//        editor.putInt(glob.url_link, newHighScore);
//        editor.apply();

        //Read
//        sharedPref = getSharedPreferences(glob.SETTING_FLAG, MODE_PRIVATE);
//        sharedPref.getString(glob.url_link, " ");

    }



    public class clickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(R.id.btnCheck == v.getId())
            {
                JSONObject value = new JSONObject();
                try {
                    value.put("check","assad");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                String[] val = {};
//                val[0] =  (String[]) list.get(0);
//                PostToServer.postRequest(value.toString(),glob.defaultURL,SettingActivity.this,
//                        PostToServer.setJson("hb","aaaa"));

                // do simple volley get/post request

                //if connect
                // save url at shared preferences
                //Toast.makeText(SettingActivity.this, "btn clicked", Toast.LENGTH_LONG).show();
            }
            if(R.id.btnPatient == v.getId())
            {
                int lastPatientId = sharedPref.getInt(glob.patientId, 0);
                if(lastPatientId == 1)
                {
                    editor.putInt(glob.patientId, 2);
                    editor.apply();
                    btnPatient.setText("Patient 2");
                }
                else if(lastPatientId == 2)
                {
                    editor.putInt(glob.patientId, 1);
                    editor.apply();
                    btnPatient.setText("Patient 1");
                }

                int currID = sharedPref.getInt(glob.patientId, 0);
                Toast.makeText(SettingActivity.this, "patient Id : "+currID, Toast.LENGTH_SHORT).show();
                Toast.makeText(SettingActivity.this, "Please restart app to take effect", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

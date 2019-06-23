package com.personalapp.ai_final_project;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostToServer {

    public PostToServer()
    {

    }

    public static void postRequest(String url, final Context con, Map<String, String> postParam) {
        final String TAG = "Volley-Result";
        // url dari server
        String requestUrl = url;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                requestUrl, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(con, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        })
        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        Volley.newRequestQueue(con).add(jsonObjReq);
    }

//    public static Map<String, String> setJson(String Name, String Val, int HBCount)
//    {
//        Map<String, String> postParam= new HashMap<String, String>();
//        postParam.put("HBCount",String.valueOf(HBCount));
//        postParam.put(Name, Val);
//
//        return postParam;
//    }

    // untuk nerima
    public static Map<String, String> setJson(String Name, List<String> valList)
    {
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("HBCount",String.valueOf(valList.size()));
        for(int i=0;i<valList.size();i++)
        {
            postParam.put(Name+i, valList.get(i));
        }


        return postParam;
    }

    //combine array heartbeat jd satu string buat di kirim via post
    public static String StringArrayCombine(String[] strArr)
    {
        String combinedStr = "[";
        for(int i= 0;i<strArr.length;i++)
        {
            if(i == strArr.length-1)
            {
                combinedStr+=strArr[i]+"]";
            }
            else
            {
                combinedStr+=strArr[i]+",";
            }

        }
        return combinedStr;
    }



}
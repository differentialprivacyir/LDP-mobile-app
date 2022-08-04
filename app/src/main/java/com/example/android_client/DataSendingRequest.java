
package com.example.android_client;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DataSendingRequest {
    private Context mcontext;
    private String dataUrl;

    DataSendingRequest(Context context) {
        this.mcontext = context;
    }

    void requestData(String url1, final String dataUrl, final ArrayList<String> data) {
        this.dataUrl = dataUrl;

        final RequestQueue queue;
        queue = Volley.newRequestQueue(mcontext);

        // volley :

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            MainActivity myClass = (MainActivity) mcontext;
                            myClass.t = Integer.parseInt(obj.get("t").toString()) * 1000;
//                            myClass.epsilon = Float.valueOf(obj.get("epsilon").toString());
//                            myClass.editor.putFloat("epsilon", Float.parseFloat(obj.get("epsilon").toString()));
                            myClass.editor.putInt("t", myClass.t);
                            myClass.editor.apply();
                            if (Integer.parseInt(obj.get("status").toString()) == 201) {
                                //Sending data
                                sendData(queue, dataUrl, data);


                            }
                        } catch (JSONException e) {
                            System.out.println("error json : " + e.toString());
                        }

                        System.out.println("server response ----> " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/json; charset=UTF-8");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void sendData(RequestQueue queue, String dataUrl, final ArrayList<String> data) {
//        System.out.println("sending data");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, dataUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        System.out.println("sent data  ---> " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                for(int i =0;i< data.size();i++)
                params.put(String.valueOf(i), data.get(i));

                params.put("epsilon",String.valueOf(((MainActivity) mcontext).sharedPreferences.getFloat("epsilon",3))); // todo default epsilon
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
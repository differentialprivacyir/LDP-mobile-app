package com.example.android_client;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TokenRequest extends AsyncTask<String, String, JSONObject> {
    private Context context;

    public TokenRequest(Context context) {
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        JSONObject finalResult = null;
        try {
            System.out.println("------------------------------------- Sent token request");
            response = httpclient.execute(new HttpGet(uri[0]));
            System.out.println("---------------------- response " + response.toString());
            StatusLine statusLine = response.getStatusLine();
            System.out.println(statusLine.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONTokener tokener = new JSONTokener(json);
            finalResult = new JSONObject(tokener);
            System.out.println("json result    ---->  " + finalResult);

        } catch (ClientProtocolException e) {
            System.out.println("ex1 " + e);
        } catch (IOException e) {
            System.out.println("ex2 " + e);
        } catch (JSONException e) {
            System.out.println("ex3 " + e);
        }
        return finalResult;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (this.context != null && result != null) {
            try {
                MainActivity myClass = (MainActivity) this.context;

                myClass.t = Integer.parseInt(result.get("t").toString()) * 1000;
//                myClass.epsilon = Float.valueOf(result.get("epsilon").toString());
//                myClass.editor.putFloat("epsilon", (Float) result.get("epsilon"));

                myClass.editor.putInt("t", myClass.t);
                myClass.editor.apply();

            } catch (JSONException e) {
                System.out.println("error -> " + e.toString());
            }
        }
    }
}
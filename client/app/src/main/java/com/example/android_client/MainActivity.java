package com.example.android_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    String host = "https://project2.dataprivacy.ir";
//    String host = "http://10.0.2.2:8000";

    CMS cms = new CMS(this);
    int t; // milliseconds
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

//    Float epsilon = 2.0f;
    float default_epsilon = 3;

    SQLiteDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("DP_APP", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //Database :
        myDatabase = openOrCreateDatabase("DP",MODE_PRIVATE,null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS DP(event TEXT);");




        // load views :
        final Switch option1 = findViewById(R.id.switch1);
        final Switch option2 = findViewById(R.id.switch2);
        final Switch option3 = findViewById(R.id.switch3);
        final Switch option4 = findViewById(R.id.switch4);



        // load saved status :
        t = sharedPreferences.getInt("t", 10 * 1000);
        option1.setChecked(sharedPreferences.getBoolean("option1", false));
        option2.setChecked(sharedPreferences.getBoolean("option2", false));
        option3.setChecked(sharedPreferences.getBoolean("option3", false));
        option4.setChecked(sharedPreferences.getBoolean("option4", false));


        // set onClicks :
        option1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("option1", isChecked);
                editor.apply();
                insertEvent(""+(option1.isChecked()? 1 : 0)+(option2.isChecked()? 1 : 0)+(option3.isChecked()? 1 : 0)+(option4.isChecked()? 1 : 0));
//                fetchEvents();

            }
        });
        option2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                insertEvent(String.valueOf(option1.isChecked()? 1 : 0)+
                        String.valueOf(option2.isChecked()? 1 : 0)+
                        String.valueOf(option3.isChecked()? 1 : 0)+
                        String.valueOf(option4.isChecked()? 1 : 0));
//                fetchEvents();
                editor.putBoolean("option2", isChecked);
                editor.apply();
            }
        });
        option3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                insertEvent(String.valueOf(option1.isChecked()? 1 : 0)+(option2.isChecked()? 1 : 0)+(option3.isChecked()? 1 : 0)+(option4.isChecked()? 1 : 0));
//                fetchEvents();
                editor.putBoolean("option3", isChecked);
                editor.apply();
            }
        });
        option4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                insertEvent(String.valueOf(option1.isChecked()? 1 : 0)+(option2.isChecked()? 1 : 0)+(option3.isChecked()? 1 : 0)+(option4.isChecked()? 1 : 0));
//                fetchEvents();
                editor.putBoolean("option4", isChecked);
                editor.apply();
            }
        });


        // set request handles :
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                double n = sharedPreferences.getFloat("sample_size", 0.6f);

                // send data to server
                String[] rawData = fetchEvents();
                int mlen = rawData.length;
                System.out.println("--------------------- num of all data  : "+rawData.length+"  n :"+n);
                rawData = subArray(rawData,(int)(mlen-(n*mlen)),mlen-1);

                ArrayList<String> data = new ArrayList<String>();
                System.out.println("--------------------- data to send to server : "+Arrays.toString(rawData));

                try {
                    for(int i = 0; i<rawData.length;i++) {

                        ReturnVal r = cms.run(rawData[i], sharedPreferences.getFloat("epsilon", default_epsilon) );
                        data.add(r.v+"_"+r.j);
//                        System.out.println("---------------------- rval :  " + r.v);
                    }
                }catch (Exception e){
                    System.out.println("------------- Something went wrong with cms : ");
                    e.printStackTrace();
                }
//                System.out.println("looooooook:" + data);
                System.out.println("------------------------- Sending Data request   /t= " + t / 1000); // Do your work here
                System.out.println("epsilon :  "+ sharedPreferences.getFloat("epsilon",0));
                DataSendingRequest task = new DataSendingRequest(MainActivity.this);
                task.requestData(host + "/need_to_update/", host + "/get_data/", data);
                handler.postDelayed(this, t);

                // delete all row from table:
                myDatabase.execSQL("delete from DP");
            }
        }, t);

    }

    //settings  menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //go to settings activity
    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        //Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        Intent settingIntent = new Intent(getApplicationContext(), SettingActivity.class);
        startActivity(settingIntent);

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void insertEvent(String a){
        System.out.println("insert -> "+a);
        myDatabase.execSQL("INSERT INTO DP VALUES( '"+a+"' );");
    }

    public String[] fetchEvents(){
        SQLiteStatement byteStatement = myDatabase.compileStatement("SELECT COUNT(event) FROM DP");

        long bytes = byteStatement.simpleQueryForLong();

        @SuppressLint("Recycle") Cursor resultSet = myDatabase.rawQuery("Select rowid,event from DP",null);
        resultSet.moveToFirst();
        String[] events = new String[(int)bytes];
        int i = 0;
        while(!resultSet.isAfterLast()){
            int id = resultSet.getInt(0);
            String event = resultSet.getString(1);
//            System.out.println("fetched -> "+event);
            events[i]= event;
//            System.out.println("--------------- result i debug 2  "+events[i] );
//            System.out.println("----------------------------->  "+id+"  "+event);
            resultSet.moveToNext();
            i++;
        }
        return events;

    }
    public static<T> T[] subArray(T[] array, int beg, int end) {
        return Arrays.copyOfRange(array, beg, end + 1);
    }
}



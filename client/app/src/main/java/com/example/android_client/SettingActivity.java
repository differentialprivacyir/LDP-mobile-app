package com.example.android_client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    RadioButton radioButtonDefault1;
    RadioButton radioButtonMinimum1;
    RadioButton radioButtonMaximum1;
    RadioButton radioButtonDefault2;
    RadioButton radioButtonMinimum2;
    RadioButton radioButtonMaximum2;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences("DP_APP", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        radioButtonDefault1 = findViewById(R.id.default_value);
        radioButtonMinimum1 = findViewById(R.id.minimum_data);
        radioButtonMaximum1 = findViewById(R.id.maximum_data);

        if (sharedPreferences.getFloat("sample_size", 0.6f) == 0.6f){
            radioButtonDefault1.setChecked(true);
        }
        else if (sharedPreferences.getFloat("sample_size", 0.6f) == 0.3f){
            radioButtonMinimum1.setChecked(true);
        }
        else {
            radioButtonMaximum1.setChecked(true);
        }

        radioButtonDefault2 = findViewById(R.id.default_value2);
        radioButtonMinimum2 = findViewById(R.id.minimum);
        radioButtonMaximum2 = findViewById(R.id.maximum);

        if (sharedPreferences.getFloat("sample_size", 0.6f) == 0.6f){
            radioButtonDefault1.setChecked(true);
        }
        else if (sharedPreferences.getFloat("sample_size", 0.6f) == 0.3f){
            radioButtonMinimum1.setChecked(true);
        }
        else {
            radioButtonMaximum1.setChecked(true);
        }

    }


    //changing sample size by client
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.default_value:
                if (checked)
                    Toast.makeText(this, "default value of records will be collected", Toast.LENGTH_SHORT).show();
                editor.putFloat("sample_size", 0.6f);
                editor.apply();
                break;
            case R.id.minimum_data:
                if (checked)
                    Toast.makeText(this, "minimum number of records will be collected", Toast.LENGTH_SHORT).show();
                editor.putFloat("sample_size", 0.3f);
                editor.apply();
                break;

            case R.id.maximum_data:
                if (checked)
                    Toast.makeText(this, "maximum number of records will be collected", Toast.LENGTH_SHORT).show();
                editor.putFloat("sample_size", 0.8f);
                editor.apply();
                break;
        }
    }
    public void onRadioButtonClicked2(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.default_value2:
                if (checked)
                    Toast.makeText(this, "level of privacy is set to default", Toast.LENGTH_SHORT).show();
                editor.putFloat("epsilon", 10);
                editor.apply();
                break;
            case R.id.minimum:
                if (checked)
                    Toast.makeText(this, "level of privacy is set to minimum", Toast.LENGTH_SHORT).show();
                editor.putFloat("epsilon", 3);
                editor.apply();
                break;

            case R.id.maximum:
                if (checked)
                    Toast.makeText(this, "level of privacy is set to maximum", Toast.LENGTH_SHORT).show();
                editor.putFloat("epsilon", 20); //todo ?
                editor.apply();
                break;
        }
    }

}

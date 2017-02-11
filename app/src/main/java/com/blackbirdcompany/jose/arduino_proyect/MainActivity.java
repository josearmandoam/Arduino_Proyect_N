package com.blackbirdcompany.jose.arduino_proyect;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    SensorManager sm;
    GameView gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gv=new GameView(this);
        setContentView(gv);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
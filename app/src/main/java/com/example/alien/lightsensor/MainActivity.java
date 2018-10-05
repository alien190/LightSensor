package com.example.alien.lightsensor;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.alien.lightsensor.lightSensor.ILightSensorCallback;
import com.example.alien.lightsensor.lightSensor.LightSensor;

public class MainActivity extends AppCompatActivity implements ILightSensorCallback {

    private TextView mTextView;
    private TextView mTextViewSate;
    private LightSensor mLightSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        mTextViewSate = findViewById(R.id.textViewState);
        mLightSensor = new LightSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        mLightSensor.setOwner(this);
    }

    @Override
    public void onSensorChanged(float value) {
        if (mTextView != null) {
            mTextView.setText(String.valueOf(value));
        }
    }

    @Override
    public void onChangeState(boolean isDark) {
        if (mTextViewSate != null)
            if (isDark) {
                mTextViewSate.setText("темно");
            } else {
                mTextViewSate.setText("светло");
            }

    }
}

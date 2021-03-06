package com.example.alien.lightsensor.lightSensor;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Calendar;
import java.util.Date;

public class LightSensor implements SensorEventListener {
    private static final int LIGHTING_CHANGE_DELAY_SECS = 5;
    private static final float LIGHTING_CHANGE_LEVEL = 10;

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private ILightSensorCallback mCallback;
    private long mLastChangeTime;
    private boolean mIsDark;
    private boolean mTriggeredState;
    private LightSensorObserver mLightSensorObserver;

    public LightSensor(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        try {
            mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            mLightSensor = null;
        }
        mIsDark = false;
        mTriggeredState = false;
        mLastChangeTime = getCurrentDate().getTime();
        mLightSensorObserver = new LightSensorObserver();
    }

    private void onResumeLightObserving() {
        if (isInitSuccessfull()) {
            mSensorManager.registerListener(this,
                    mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void onPauseLightObserving() {
        if (isInitSuccessfull()) {
            mSensorManager.unregisterListener(this);
        }
    }

    private boolean isInitSuccessfull() {
        return mSensorManager != null && mLightSensor != null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mCallback != null) {
            float value = sensorEvent.values[0];
            mCallback.onSensorChanged(value);
            checkLightLevel(value);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void setOwner(ILightSensorCallback callback) {
        mCallback = callback;
        mCallback.getLifecycle().addObserver(mLightSensorObserver);
    }

    private void checkLightLevel(float value) {
        boolean isDark = value < LIGHTING_CHANGE_LEVEL;
        if (isDark != mIsDark) {
            mLastChangeTime = getCurrentDate().getTime();
            mIsDark = isDark;
        } else {
            if (isDark != mTriggeredState && mCallback != null) {
                long curTime = getCurrentDate().getTime();
                if (curTime >= mLastChangeTime + LIGHTING_CHANGE_DELAY_SECS * 1000) {
                    mTriggeredState = isDark;
                    mCallback.onChangeState(isDark);
                }
            }
        }
    }

    private Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public class LightSensorObserver implements LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        void onPause() {
            onPauseLightObserving();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        void onResume() {
            onResumeLightObserving();
        }
    }

    public boolean isDark() {
        return mIsDark;
    }
}


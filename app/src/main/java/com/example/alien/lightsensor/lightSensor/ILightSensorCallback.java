package com.example.alien.lightsensor.lightSensor;

import android.arch.lifecycle.LifecycleOwner;

public interface ILightSensorCallback extends LifecycleOwner {
    void onSensorChanged(float value);
    void onChangeState(boolean isDark);
}

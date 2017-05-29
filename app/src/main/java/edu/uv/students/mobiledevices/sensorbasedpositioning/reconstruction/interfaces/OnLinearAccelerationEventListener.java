package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces;

import android.hardware.SensorEvent;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnLinearAccelerationEventListener {
    void onLinearAccelerationEvent(SensorEvent events, long pTimeStamp_ns);
}

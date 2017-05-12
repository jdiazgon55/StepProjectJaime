package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnAccelerometerEventListener {
    void onAccelerometerEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy);
}

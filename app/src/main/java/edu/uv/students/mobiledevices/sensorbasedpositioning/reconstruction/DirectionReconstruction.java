package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.util.Log;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionReconstruction implements OnGyroscopeEventListener, OnMagneticFieldEventListener {
    private final OnDirectionChangedListener directionChangedListener;

    public DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
    }

    @Override
    public void onGyroscopeEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        // DirectionData directionData = new DirectionData();
        // reconstruct Direction
        // put into direction data
        // directionChangedListener.onDirectionChanged(directionData);
    }

    @Override
    public void onMagneticFieldEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        Log.i(Positioning.LOG_TAG, "MAGNETIC FIELD EVENT! time(ns): " + pTimeStamp_ns + " x: " + pX + " y: " + pY + " z: " + pZ);
    }
}

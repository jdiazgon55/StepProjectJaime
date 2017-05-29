package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnLinearAccelerationEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 *
 * Distributes all the events (sensors, steps, ...) that happen during the program run.
 */

public class EventDistributor implements
        OnAccelerometerEventListener,
        OnLinearAccelerationEventListener,
        OnMagneticFieldEventListener {

    private final LinkedList<OnAccelerometerEventListener> onAccelerometerEventListeners;
    private final LinkedList<OnLinearAccelerationEventListener> onLinearAccelerationEventListeners;
    private final LinkedList<OnMagneticFieldEventListener> onMagneticSensorEventListeners;


    public EventDistributor(SensorManager pSensorManager) {
        onAccelerometerEventListeners = new LinkedList<>();
        onLinearAccelerationEventListeners = new LinkedList<>();
        onMagneticSensorEventListeners = new LinkedList<>();
    }

    public void registerAccelerometerEventListener(OnAccelerometerEventListener pListener) {
        onAccelerometerEventListeners.add(pListener);
    }

    public void registerLinearAccelerationEventListener(OnLinearAccelerationEventListener pListener) {
        onLinearAccelerationEventListeners.add(pListener);
    }

    public void registerMagneticFieldEventListener(OnMagneticFieldEventListener pListener) {
        onMagneticSensorEventListeners.add(pListener);
    }

    @Override
    public void onAccelerometerEvent(SensorEvent events, long pTimeStamp_ns, int pAccuracy) {
        for(OnAccelerometerEventListener listener : onAccelerometerEventListeners)
            listener.onAccelerometerEvent(events, pTimeStamp_ns, pAccuracy);
    }

    @Override
    public void onMagneticFieldEvent(SensorEvent events, long pTimeStamp_ns, int pAccuracy) {
        for(OnMagneticFieldEventListener listener : onMagneticSensorEventListeners)
            listener.onMagneticFieldEvent(events, pTimeStamp_ns, pAccuracy);
    }

    @Override
    public void onLinearAccelerationEvent(SensorEvent events, long pTimeStamp_ns) {
        for(OnLinearAccelerationEventListener listener : onLinearAccelerationEventListeners)
            listener.onLinearAccelerationEvent(events, pTimeStamp_ns);
    }
}

package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnLinearAccelerationEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepRecognition;

/**
 * Created by Fabi on 02.05.2017.
 *
 * Distributes all the events (sensors, steps, ...) that happen during the program run.
 */

public class EventDistributor implements
        OnPathChangedListener,
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        OnAccelerometerEventListener,
        OnLinearAccelerationEventListener,
        OnMagneticFieldEventListener {

    private final LinkedList<OnPathChangedListener> onPathChangedListeners;
    private final LinkedList<OnStepRecognition> onStepListeners;
    private final LinkedList<OnDirectionChangedListener> onDirectionChangedListeners;
    private final LinkedList<OnStepLengthChangedListener> onStepLengthChangedListeners;

    private final LinkedList<OnAccelerometerEventListener> onAccelerometerEventListeners;
    private final LinkedList<OnLinearAccelerationEventListener> onLinearAccelerationEventListeners;
    private final LinkedList<OnMagneticFieldEventListener> onMagneticSensorEventListeners;


    public EventDistributor(SensorManager pSensorManager) {
        onPathChangedListeners = new LinkedList<>();
        onStepListeners = new LinkedList<>();
        onDirectionChangedListeners = new LinkedList<>();
        onStepLengthChangedListeners = new LinkedList<>();

        onAccelerometerEventListeners = new LinkedList<>();
        onLinearAccelerationEventListeners = new LinkedList<>();
        onMagneticSensorEventListeners = new LinkedList<>();
    }

    public void registerOnPathChangedListener(OnPathChangedListener pListener) {
        onPathChangedListeners.add(pListener);
    }

    public void registerOnStepListener(OnStepRecognition pListener) {
        onStepListeners.add(pListener);
    }

    public void registerOnDirectionChangedListener(OnDirectionChangedListener pListener) {
        onDirectionChangedListeners.add(pListener);
    }

    public void registerOnStepLengthChangedListener(OnStepLengthChangedListener pListener) {
        onStepLengthChangedListeners.add(pListener);
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
    public void onPathChanged(PathData pPathData) {
        for(OnPathChangedListener listener : onPathChangedListeners)
            listener.onPathChanged(pPathData);
    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {
        for(OnDirectionChangedListener listener : onDirectionChangedListeners)
            listener.onDirectionChanged(pDirectionData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {
        for(OnStepLengthChangedListener listener : onStepLengthChangedListeners)
            listener.onStepLengthChanged(pStepLengthData);
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

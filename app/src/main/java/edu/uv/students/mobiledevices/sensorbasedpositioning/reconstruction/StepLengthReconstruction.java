package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction {

    private final OnStepLengthChangedListener stepLengthChangedListener;

    public StepLengthReconstruction(OnStepLengthChangedListener pListener) {
        stepLengthChangedListener = pListener;
    }
}

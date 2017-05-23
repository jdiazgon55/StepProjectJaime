package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction {

    private final OnStepLengthChangedListener stepLengthChangedListener;

    public StepLengthReconstruction(OnStepLengthChangedListener pListener) {
        stepLengthChangedListener = pListener;
    }
}

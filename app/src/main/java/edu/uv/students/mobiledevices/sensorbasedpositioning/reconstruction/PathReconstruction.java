package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        OnStepListener {

    private final OnPathChangedListener pathChangedListener;

    public PathReconstruction(OnPathChangedListener pListener) {
        pathChangedListener = pListener;
    }

    @Override
    public void onStep(StepData pStepData) {

    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {

    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {

        PathData pathData = new PathData();
        // reconstruct path
        // put reconstruction int pathData
        // pathChangedListener.onPathChanged(pathData);
    }
}

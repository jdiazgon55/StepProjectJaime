package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces;

import java.util.ArrayList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 */

public interface OnStepRecognition {
    void hayPaso(ArrayList<StepData> pasoActual);
}

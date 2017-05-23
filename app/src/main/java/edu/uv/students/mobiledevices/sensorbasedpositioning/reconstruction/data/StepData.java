package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data;

import static processing.core.PApplet.sqrt;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 */

public class StepData {
    float[] array;

    public StepData(float ax, float ay, float az) {
        array = new float[4];
        array[0] = ax;
        array[1] = ay;
        array[2] = az;
        array[3] = sqrt((ax*ax) + (ay*ay) + (az*az)); //Módulo
    }
}

package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.SensorEvent;

import java.util.ArrayList;


import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.StepRecognition;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;


import static processing.core.PApplet.sqrt;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 *
 *  Recoge eventos del acelerómetro y los guarda en una arrayList, cuando se consiguen 16
 *  se comprueba si hay paso.
 */

public class StepReconstruction implements OnAccelerometerEventListener {

    private final OnAccelerometerEventListener onAccelerometerEventListener;
    private StepRecognition stepRecognition;
    private ArrayList<float[]> pasoReconstruction;
    private ProcessingVisualization processingVisualization;
    private float milisegundos = 0.0f;
    private float tiempoStart = System.currentTimeMillis();

    public StepReconstruction(OnAccelerometerEventListener pListener) {
        onAccelerometerEventListener = pListener;
        pasoReconstruction = new ArrayList<float[]>();
        stepRecognition = new StepRecognition();
        this.processingVisualization = Positioning.processingVisualization;
    }

    @Override
    public void onAccelerometerEvent(SensorEvent events, long pTimeStamp_ns, int pAccuracy) {

    }
}

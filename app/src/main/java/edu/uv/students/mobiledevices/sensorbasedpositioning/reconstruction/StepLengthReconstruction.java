package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.SensorEvent;

import java.util.ArrayList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnLinearAccelerationEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;

import static java.lang.StrictMath.abs;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 */

public class StepLengthReconstruction implements OnLinearAccelerationEventListener{

    private ArrayList<Float> aceleracionY;
    private ProcessingVisualization processingVisualization;
    private float maxAceleracionY = 0.0f;

    public StepLengthReconstruction() {
        aceleracionY = new ArrayList<Float>();
        this.processingVisualization = Positioning.processingVisualization;
    }

    @Override
    public void onLinearAccelerationEvent(SensorEvent events, long pTimeStamp_ns) {
        aceleracionY.add(abs(events.values[1]));
        //Tomamos el valor máximo de las aceleraciones
        if(aceleracionY.size() >= 16){
            float max = 0.0f;
            for (float valorTmp : aceleracionY){
                if(valorTmp > max)
                    max = valorTmp;
            }
            if(processingVisualization.getReiniciarMaxAceleracion()){
                maxAceleracionY = max;
                processingVisualization.setMaxAceleracionY(maxAceleracionY);
                processingVisualization.setReiniciarMaxAceleracion(false);
            }
            aceleracionY = new ArrayList<Float>();
        }
    }
}

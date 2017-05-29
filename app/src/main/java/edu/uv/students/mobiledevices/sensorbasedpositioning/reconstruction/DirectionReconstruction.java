package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;

import static java.lang.Math.PI;

/**
 * Created by Fabi and Jaime on 02.05.2017.
 */

public class DirectionReconstruction implements OnMagneticFieldEventListener, OnAccelerometerEventListener {
    private final OnDirectionChangedListener directionChangedListener;
    private float[] arrayAceleracion;
    private float[] arrayMagnetico;
    private float azimutAnterior = 0.0f;
    private ProcessingVisualization processingVisualization;

    public DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
        this.processingVisualization = Positioning.processingVisualization;
    }


    @Override
    public void onMagneticFieldEvent(SensorEvent pEvent, long pTimeStamp_ns, int pAccuracy) {
        arrayMagnetico = pEvent.values;
        setAzimut();
    }

    @Override
    public void onAccelerometerEvent(SensorEvent pEvent, long pTimeStamp_ns, int pAccuracy) {
        arrayAceleracion = pEvent.values;
        setAzimut();
    }

    private void setAzimut(){
        if (arrayAceleracion != null && arrayMagnetico != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            if (SensorManager.getRotationMatrix(R, I, arrayAceleracion, arrayMagnetico)) {
                // orientation contains azimut, pitch and roll
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimut = orientation[0];

                // Solo si hay un gran cambio en la rotación, cambiamos la orientación
                if(azimut - azimutAnterior > 0.12 || azimut - azimutAnterior < -0.12) {
                    setRotation(azimut);
                    azimutAnterior = azimut;
                }
            }
        }
    }

    private void setRotation(float azimut){
        float rotacion = azimut;

        if(azimut < 0){
            rotacion = azimut + (float)(2*PI);
        }

        if (processingVisualization.getNorte() == -10000.0f){
            processingVisualization.setNorte(rotacion);
        }
        processingVisualization.setRotacionActual(rotacion);
    }
}

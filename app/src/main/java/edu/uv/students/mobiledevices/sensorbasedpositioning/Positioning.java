package edu.uv.students.mobiledevices.sensorbasedpositioning;

import android.app.FragmentManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.DirectionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.EventDistributor;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.EventEmulator;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.PathReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.StepLengthReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.StepReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;
public class Positioning extends AppCompatActivity implements SensorEventListener {

    private EventDistributor eventDistributor;

    private StepReconstruction stepReconstruction;
    private DirectionReconstruction directionReconstruction;
    private StepLengthReconstruction stepLengthReconstruction;
    private PathReconstruction pathReconstruction;

    public static final String LOG_TAG = "DETECTA_POSICIONES";

    public static ProcessingVisualization processingVisualization;

    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magneticSensor;

    private float[] arrayAceleracion;
    private float[] arrayMagnetico;
    private float primeraRotacion = -10000.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        initProcessing();
        initReconstruction();

        // Choose either to initialize the real sensors
        // or, for testing, use the event emulation
        initSensors();
        //initEventEmulation();

        /*
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(!areAllRequiredSensorsPresent()) {
            ((TextView) findViewById(R.id.positioning_errorTV)).setText(R.string.error_missing_sensors);
            return;
        }*/

    }

    private void initEventEmulation() {
        EventEmulator eventEmulator = new EventEmulator(eventDistributor);
        // the EventEmulator provides different emulations for testing purposes
        // eventEmulator.startEmulation01();
        eventEmulator.startEmulationLoadedFromFile(getResources().openRawResource(R.raw.walking_in_flat),(long)(3*1e9));
    }

    private void initSensors() {
        context = this.getApplicationContext();
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void desactivarSensores(){
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(this);
    }

    private void initProcessing() {
        FragmentManager fragmentManager = getFragmentManager();
        processingVisualization = new ProcessingVisualization();
        fragmentManager.beginTransaction()
                .replace(R.id.ProcessingContainer, processingVisualization)
                .commit();
    }

    private boolean areAllRequiredSensorsPresent() {
        return
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
            && sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
            && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
    }

    private void initReconstruction() {
        eventDistributor = new EventDistributor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        //stepReconstruction = new StepReconstruction(eventDistributor);
        directionReconstruction = new DirectionReconstruction(eventDistributor);
        stepLengthReconstruction = new StepLengthReconstruction(eventDistributor);
        //pathReconstruction = new PathReconstruction(eventDistributor);
        initEventDistribution();
    }

    private void initEventDistribution() {
        // Step reconstruction
        //eventDistributor.registerAccelerometerEventListener(stepReconstruction);

        // direction reconstruction
        eventDistributor.registerMagneticFieldEventListener(directionReconstruction);
        eventDistributor.registerAccelerometerEventListener(directionReconstruction);

        //step length reconstruction

        // path reconstruction
        //eventDistributor.registerOnDirectionChangedListener(pathReconstruction);
        //eventDistributor.registerOnStepLengthChangedListener(pathReconstruction);

        // processing drawing
        //eventDistributor.registerOnPathChangedListener(processingVisualization);
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent) {
        if(pEvent.sensor==magneticSensor) {
            eventDistributor.onMagneticFieldEvent(pEvent, pEvent.timestamp, pEvent.accuracy);
        }
        else if(pEvent.sensor==accelerometer){
            eventDistributor.onAccelerometerEvent(pEvent, pEvent.timestamp, pEvent.accuracy);
        }
    }

    public void onResume() {
        super.onResume();
        initSensors();
    }

    public void onPause() {
        super.onPause();
        desactivarSensores();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

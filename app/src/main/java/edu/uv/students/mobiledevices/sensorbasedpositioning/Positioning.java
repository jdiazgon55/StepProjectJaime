package edu.uv.students.mobiledevices.sensorbasedpositioning;

import android.app.FragmentManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.DirectionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.EventDistributor;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.StepLengthReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;
public class Positioning extends AppCompatActivity implements SensorEventListener {

    private EventDistributor eventDistributor;

    private DirectionReconstruction directionReconstruction;
    private StepLengthReconstruction stepLengthReconstruction;

    public static final String LOG_TAG = "DETECTA_POSICIONES";

    public static ProcessingVisualization processingVisualization;

    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linearAcceleration;
    private Sensor magneticSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        initProcessing();
        initReconstruction();

        // Initialize the real sensors
        initSensors();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Comprobamos si los sensores están presentes, en caso contrario se muestra un mensaje
        if(!areAllRequiredSensorsPresent()) {
            ((TextView) findViewById(R.id.positioning_errorTV)).setText(R.string.error_missing_sensors);
            return;
        }
        else{
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layout_root);
            final LinearLayout child = (LinearLayout) linearLayout.findViewById(R.id.layout_error);
            linearLayout.removeView(child);
        }

    }

    private void initSensors() {
        context = this.getApplicationContext();
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_GAME);
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
            && sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null
            && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
    }

    private void initReconstruction() {
        eventDistributor = new EventDistributor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        directionReconstruction = new DirectionReconstruction();
        stepLengthReconstruction = new StepLengthReconstruction();
        initEventDistribution();
    }

    private void initEventDistribution() {
        // Step reconstruction
        //eventDistributor.registerAccelerometerEventListener(stepReconstruction);

        // direction reconstruction
        eventDistributor.registerMagneticFieldEventListener(directionReconstruction);
        eventDistributor.registerAccelerometerEventListener(directionReconstruction);

        //step length reconstruction
        eventDistributor.registerLinearAccelerationEventListener(stepLengthReconstruction);

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
        else if(pEvent.sensor==linearAcceleration){
            eventDistributor.onLinearAccelerationEvent(pEvent, pEvent.timestamp);
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

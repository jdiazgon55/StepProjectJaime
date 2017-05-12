package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;

/**
 * Created by Fabi on 11.05.2017.
 */

public class EventEmulator {
    private EventDistributor eventDistributor;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    enum SensorEventType {
        ACCELEROMETER,
        GYROSCOPE,
        MAGNETIC_FIELD
    }
    static class SensorEvent {
        public SensorEventType eventType;
        public long timeNs;
        public float[] values;

        public SensorEvent(SensorEventType pEventType, long timeNs, float pX, float pY, float pZ) {
            eventType = pEventType;
            this.timeNs = timeNs;
            values = new float[3];
            values[0] = pX;
            values[1] = pY;
            values[2] = pZ;
        }
    }

    private static final String ACCELEROMETER_DATA_TAG = "ACCELEROMETER";
    private static final String GYROSCOPE_DATA_TAG = "GYROSCOPE";
    private static final String MAGNETIC_FIELD_DATA_TAG = "MAGNETIC_FIELD";
    private static final String COLUMN_SEPARATOR = ",";


    public EventEmulator(EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

    private SensorEventType mapStringToEventType(String pString) {
        switch (pString) {
            case ACCELEROMETER_DATA_TAG:
                return SensorEventType.ACCELEROMETER;
            case GYROSCOPE_DATA_TAG:
                return SensorEventType.GYROSCOPE;
            case MAGNETIC_FIELD_DATA_TAG:
                return SensorEventType.MAGNETIC_FIELD;
            default:
                return SensorEventType.ACCELEROMETER;
        }
    }

    /**
     * Loads captured sensor events from a file and emulates them on this device
     * The file format ist
     * Column 0: On of {ACCELEROMETER, GYROSCOPE, MAGNETIC_FIELD} (STRING)
     * Column 1: The time in nanoseconds (LONG)
     * Column 2-4: the sensor data (FLOAT)
     * Column separator: "," without spaces
     * @param pInputStream the file from which to load the events
     * @param pTimeDelayNs an initial time delay before the emulation starts in nanoseconds
     */
    public void startEmulationLoadedFromFile(InputStream pInputStream, long pTimeDelayNs){
        String line;
        ArrayList<SensorEvent> sensorEvents = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(pInputStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String[] dataFields = line.split(COLUMN_SEPARATOR);
                sensorEvents.add(new SensorEvent(
                        mapStringToEventType(dataFields[0]),
                        Long.valueOf(dataFields[1])+pTimeDelayNs,
                        Float.valueOf(dataFields[2]),
                        Float.valueOf(dataFields[3]),
                        Float.valueOf(dataFields[4])));
            }
        } catch (IOException e) {
            Log.e(Positioning.LOG_TAG, "Error while reading file to emulate sensor events.", e);
        }
        for (final SensorEvent sensorEvent : sensorEvents) {
            switch(sensorEvent.eventType) {
                case ACCELEROMETER:
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            eventDistributor.onAccelerometerEvent(
                                    sensorEvent.values[0],
                                    sensorEvent.values[1],
                                    sensorEvent.values[2],
                                    sensorEvent.timeNs,
                                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
                case GYROSCOPE:
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            eventDistributor.onGyroscopeEvent(
                                    sensorEvent.values[0],
                                    sensorEvent.values[1],
                                    sensorEvent.values[2],
                                    sensorEvent.timeNs,
                                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
                case MAGNETIC_FIELD:
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            eventDistributor.onMagneticFieldEvent(
                                    sensorEvent.values[0],
                                    sensorEvent.values[1],
                                    sensorEvent.values[2],
                                    sensorEvent.timeNs,
                                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
            }
        }
    }

    /**
     * Emulates 5 steps in west direction and then a rotation of 360 degrees
     */
    public void startEmulation01() {
        final PathData pathData = new PathData();

        long startTimePaddingMs = 3000;

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(1,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+1000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(1,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+2000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(2,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+3000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(3,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4250, TimeUnit.NANOSECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 2*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4500, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 3*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4750, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 4*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+5000, TimeUnit.MILLISECONDS);
    }
}

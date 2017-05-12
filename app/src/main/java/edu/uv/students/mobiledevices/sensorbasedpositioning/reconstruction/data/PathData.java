package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealVector;

import java.util.Stack;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathData {
    /**
     * Angle counter clockwise in a right handed coordinate system
     * x is East direction
     * y is South direction
     */
    public double angle;

    /**
     * Positions as 2D vectors
     * Coordinate system
     * x is East direction
     * y is South direction
     * Units are meters
     */
    public Stack<Vector2D> positions;

    public PathData() {
        angle = 0.0;
        positions = new Stack<>();
        positions.add(new Vector2D(0,0));
    }
}

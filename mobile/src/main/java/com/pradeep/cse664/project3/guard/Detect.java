package com.pradeep.cse664.project3.guard;

/**
 * Created by Pradeep on 5/7/15.
 */
public class Detect {
    private float[] current = new float[3];
    private static final float plane = 7.0f;

    public Detect() {
        current[0] = 0.0f;
        current[1] = 0.0f;
        current[2] = 0.0f;
    }

    public String sense(float[] values) {

        current[0] = current[0] * 0.5f + values[0] * 0.5f;
        current[1] = current[1] * 0.5f + values[1] * 0.5f;
        current[2] = current[2] * 0.5f + values[2] * 0.5f;

        String newPosition = "Unknown";
        if (current[0] > plane) {
            newPosition = "Right";
        } else if (current[0] < -plane) {
            newPosition = "Left";
        } else if (current[2] > plane) {
            newPosition = "Top";
        } else if (current[2] < -plane) {
            newPosition = "Back";
        }

        return newPosition;
    }
}

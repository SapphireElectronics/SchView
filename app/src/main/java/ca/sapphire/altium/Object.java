package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Interface for an Altium Object
 */
public interface Object {
    int recordNumber = -1;
    /**
     * Make the object ready for drawing.
     *
     * Does all pre-calculations and stores the generated data for later fast drawing.
     */
    void render();

    /**
     * Draws the object to the Canvas using the Paint object
     *
     * @param canvas : Canvas to draw on
     * @param paint : paint object to use
     */
    void draw( Canvas canvas, Paint paint );

    /**
     * Read the object from the specified file.
     *
     * After reading, the object must call render to make it drawable.
     *
     * @param dis : Data Input Stream
     * @throws IOException
     */
    void read( DataInputStream dis ) throws IOException;

    /**
     * Writes the object to the specified file.
     *
     * Writes only the data necessary to recreate the object later, does not store the
     * data created by render.
     *
     * @param dos
     * @throws IOException
     */
    void write( DataOutputStream dos ) throws IOException;
}

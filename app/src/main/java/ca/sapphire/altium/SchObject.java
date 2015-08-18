package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ca.sapphire.graphics.GrEngine;

/**
 * New interface for Altium Schematic Objects
 */
public interface SchObject {
    void render( GrEngine engine);

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

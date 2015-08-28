package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium Wire multi-segment object
 */
public class Bus implements SchObject {
    int color;
    float[] x, y;

    public Bus( Map<String, String> record ) {
        int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
        x = new float[size];
        y = new float[size];

        Utility.addMultiLine(record, x, y);
        color = Utility.getColor(record);
    }

    @Override
    public void render( GrEngine engine ) {
        for (int i = 0; i < x.length-1; i++)
            engine.addLine( x[i], -y[i], x[i+1], -y[i+1], color, 3 );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}

}

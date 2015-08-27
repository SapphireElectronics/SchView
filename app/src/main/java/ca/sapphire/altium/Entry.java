package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium reference designator
 */
public class Entry implements SchObject {
//    int x1, y1, x2, y2;
    float x1, y1, x2, y2;
    int color;

    public Entry( Map<String, String> record ) {
        x1 = Integer.parseInt(record.get("LOCATION.X"));
        y1 = -Integer.parseInt(record.get("LOCATION.Y"));
        x2 = Integer.parseInt(record.get("CORNER.X"));
        y2 = -Integer.parseInt(record.get("CORNER.Y"));
        color = Utility.getColor(record);
    }

    public Entry() {
        x1 = Field.getFloat(Field.LOCATION_X.ordinal() );
        y1 = -Field.getFloat(Field.LOCATION_Y.ordinal() );
        x2 = Field.getFloat(Field.CORNER_X.ordinal() );
        y2 = -Field.getFloat(Field.CORNER_Y.ordinal() );
        color = Field.getInt( Field.COLOR.ordinal() );
        color = Utility.altiumToRGB(color);
    }
//    public void draw( Canvas canvas, Paint paint ) {
//        paint.setColor( color );
//        canvas.drawLine( x1, y1, x2, y2, paint );
//    }

    @Override
    public void render( GrEngine engine ) {
        engine.addLine( x1, y1, x2, y2, color );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

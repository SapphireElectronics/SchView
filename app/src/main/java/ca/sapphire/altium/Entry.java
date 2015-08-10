package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Contains an Altium reference designator
 */
public class Entry implements Object {
    int x1, y1, x2, y2, color;

    public Entry( Map<String, String> record ) {
        x1 = Integer.parseInt(record.get("LOCATION.X"));
        y1 = -Integer.parseInt(record.get("LOCATION.Y"));
        x2 = Integer.parseInt(record.get("CORNER.X"));
        y2 = -Integer.parseInt(record.get("CORNER.Y"));
        color = Utility.getColor(record);
    }
    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );
        canvas.drawLine( x1, y1, x2, y2, paint );
    }

    @Override
    public void render() {}

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

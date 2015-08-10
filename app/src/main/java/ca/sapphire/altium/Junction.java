package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Contains an Altium Junction
 */
public class Junction implements Object {
    int x, y, color;

    public Junction( Map<String, String> record) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = -Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getColor(record);
    }

    @Override
    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );
        canvas.drawCircle( x, y, 2, paint );
    }

    @Override
    public void render() {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}

    @Override
    public void read(DataInputStream dis) throws IOException {}
}

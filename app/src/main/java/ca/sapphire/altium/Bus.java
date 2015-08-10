package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Contains an Altium Wire multi-segment object
 */
public class Bus implements Object {
    int color;
    Point point[];

    public Bus( Map<String, String> record ) {
        point = Utility.addMultiLine(record);
        Utility.xyToJava( point );
        color = Utility.getColor(record);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor( color );
        paint.setStrokeWidth(3);
        for (int i = 0; i < point.length-1; i++)
            canvas.drawLine( point[i].x, point[i].y, point[i+1].x, point[i+1].y, paint );
        paint.setStrokeWidth( 0 );
    }

    @Override
    public void render() {}

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}

}

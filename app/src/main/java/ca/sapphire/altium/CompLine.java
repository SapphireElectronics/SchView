package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Contains an Altium Component Line multisegment object
 */
public class CompLine implements Object, Serializable {
    int x1, y1, x2, y2, color;

    public CompLine( Map<String, String> record ) {
        x1 = Integer.parseInt(record.get("LOCATION.X"));
        y1 = Integer.parseInt(record.get("LOCATION.Y"));
        x2 = Integer.parseInt(record.get("CORNER.X"));
        y2 = Integer.parseInt(record.get("CORNER.Y"));
        color = Utility.getColor(record);
    }



    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void render() {}

    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );
        canvas.drawLine( x1, -y1, x2, -y2, paint );
    }

}


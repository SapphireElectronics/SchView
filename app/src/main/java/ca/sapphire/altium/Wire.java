package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import ca.sapphire.graphics.Line;

/**
 * Contains an Altium Wire multisegment object
 */
public class Wire implements Object, Serializable {
    int color;
    Point point[];

    public Wire( Map<String, String> record ) {
        point = Utility.addMultiLine(record);
        color = Utility.getIntValue(record, "COLOR");
    }

    public void render( Render renderer ) {
        for (int i = 0; i < point.length-1; i++)
            renderer.objects.add( new Line( point[i].x, point[i].y, point[i+1].x, point[i+1].y, color ) );

//            renderer.addLine( point[i], point[i+1], color );
    }

    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );
        for (int i = 0; i < point.length-1; i++)
            canvas.drawLine( point[i].x, -point[i].y, point[i+1].x, -point[i+1].y, paint );

    }

    @Override
    public void render() {

    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }
}


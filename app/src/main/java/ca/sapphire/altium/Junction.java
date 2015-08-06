package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Contains an Altium Junction
 */
public class Junction implements Object, Serializable{
    int x, y, color;

    public Junction( Map<String, String> record) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getIntValue(record, "COLOR");
    }

    @Override
    public void render(Render renderer) {
 //       renderer.objects.add(new Render.Circle(x, y, 2, color));
    }

    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );
        canvas.drawCircle( x, y, 2, paint );
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void render() {

    }
}

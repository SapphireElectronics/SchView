package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.Line;

/**
 * Created by Admin on 06/08/15.
 */
public class Pin implements Object {
    int x, y, length, designator, option, color;
    String name;

    PointF pnt1, pnt2;

    public Pin( Map<String, String> record ) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = Utility.getIntValue(record, "LOCATION.Y");
        length = Integer.parseInt(record.get("PINLENGTH"));
        designator = Integer.parseInt(record.get("DESIGNATOR"));
        option = Integer.parseInt(record.get("PINCONGLOMERATE"));
//        color = Utility.getColor(record);
        name = record.get("NAME");
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void render() {
        pnt1 = new PointF( x, y );
        pnt2 = new PointF(x+10, y );
        Utility.rotate( pnt2, pnt1, option & 0x03 );
        color = 0xff0000ff;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawLine( pnt1.x, -pnt1.y, pnt2.x, -pnt2.y, paint );
    }
}

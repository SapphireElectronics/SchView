package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 06/08/15.
 */
public class CompPoly implements Object {
    int size, lineColor, areaColor;
    Point[] point;

    Path path;


    public CompPoly(  Map<String, String> record ) {
        size = Integer.parseInt(record.get("LOCATIONCOUNT"));
        lineColor = Utility.altiumToRGB(Integer.parseInt(record.get("COLOR")));
        areaColor = Utility.altiumToRGB( Integer.parseInt(record.get("AREACOLOR")) );
        point = Utility.addMultiLine(record);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void render() {
        path = Utility.polygon( point );

    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor( areaColor );
        canvas.drawPath( path, paint );

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor( lineColor );
        canvas.drawPath( path, paint );
    }
}

package ca.sapphire.schview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.altium.*;
import ca.sapphire.altium.Object;

/**
 * Contains an Altium component filled body
 */
public class CompBox implements Object {
    Point[] point;
    int lineColor, fillColor;

    boolean draw = false;
    Path path;

    public CompBox( Map<String, String> record ) {
        if(record.get("ISSOLID") == null)
            return;

        draw = true;
        lineColor = Utility.altiumToRGB(Integer.parseInt(record.get("COLOR")));
        fillColor = Utility.altiumToRGB(Integer.parseInt(record.get("AREACOLOR")));
        point = new Point[4];
        point[0] = new Point(Integer.parseInt(record.get("LOCATION.X")), -Integer.parseInt(record.get("LOCATION.Y")));
        point[2] = new Point(Integer.parseInt(record.get("CORNER.X")), -Integer.parseInt(record.get("CORNER.Y")));
        point[1] = new Point( point[0].x, point[2].y );
        point[3] = new Point( point[2].x, point[0].y );
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if( !draw )
            return;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor( fillColor );
        canvas.drawPath( path, paint );

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor( lineColor );
        canvas.drawPath( path, paint );
    }

    @Override
    public void render() {
        if( !draw )
            return;
        path = Utility.polygon( point );
    }
    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

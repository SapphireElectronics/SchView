package ca.sapphire.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import ca.sapphire.altium.Utility;
import ca.sapphire.graphics.GraphicsObject;

/**
 * Created by Admin on 04/08/15.
 */
public class Line implements GraphicsObject {
    public int x1,y1,x2,y2,color;

    public Line() {}

    public Line( Point p1, Point p2, int color ) {
        x1 = p1.x;
        y1 = -p1.y;
        x2 = p2.x;
        y2 = -p2.y;
        this.color = color;
    }

    public Line( int x1, int y1, int x2, int y2, int color ) {
        this.x1 = x1;
        this.y1 = -y1;
        this.x2 = x2;
        this.y2 = -y2;
        this.color = color;
    }

    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( Utility.altiumToRGB(color) );
        canvas.drawLine(x1, y1, x2, y2, paint);
    }
}


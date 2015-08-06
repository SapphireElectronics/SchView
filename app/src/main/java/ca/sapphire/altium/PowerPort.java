package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.Serializable;
import java.util.Map;

import ca.sapphire.graphics.Line;

/**
 * Contains an Altium Power Port object
 */
public class PowerPort implements Object, Serializable {
    int x, y, color, xs[], xe[], ys[], ye[];
    byte style, rotation, fontId;
    boolean hidden, locked;
    String name;

    public PowerPort( Map<String, String> record ) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getIntValue(record, "COLOR");
        style = Utility.getByteValue(record, "STYLE", (byte) 0 );
        rotation = Utility.getByteValue(record, "ORIENTATION", (byte) 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        hidden = !Utility.getBooleanValue(record, "SHOWNETNAME");
        locked = Utility.getBooleanValue(record, "GRAPHICALLYLOCKED", false);
        name = record.get( "TEXT");


    }

    /**
     * Styles:
     * 0 = Circle
     * 1 = Arrow
     * 2 = Bar
     * 3 = Wave
     * 4 = Power Ground
     * 5 = Signal Ground
     * 6 = Earth
     * 7 = GOST Arrow
     * 8 = GOST Power
     * 9 = GOST Earth
     * 10 = GOST Bar
     *
     * Orientations:
     * 0 = 0 degrees
     * 1 = 90 degrees
     * 2 = 180 degrees
     * 3 = 270 degrees
     */

    public void render( Render renderer ) {
        // TODO: Add all rendering types
        // TODO: Fix text rendering location
        // Currently only renders bar, arrow without text

        renderer.addText( name, x, y, fontId, color );

        switch( style ) {
            case 1:
                Point[] points = new Point[5];

                points[0] = new Point( x, y );
                points[1] = new Point( x+4, y );
                points[2] = new Point( x+4, y-3 );
                points[3] = new Point( x+4, y+3 );
                points[4] = new Point( x+10, y );

                for( Point point : points )
                    Utility.rotate( point, points[0], rotation );

                renderer.objects.add( new Line( points[0], points[1], color));
                renderer.objects.add( new Line( points[2], points[3], color));
                renderer.objects.add( new Line( points[3], points[4], color));
                renderer.objects.add( new Line( points[4], points[2], color));

//                renderer.objects.add( new Line( points[0], points[1], color));
//                renderer.objects.add( new Line( points[2], points[3], color));
//                renderer.objects.add( new Line( points[3], points[4], color));
//                renderer.objects.add( new Line( points[4], points[2], color));

                break;

            default:
                points = new Point[4];

                points[0] = new Point( x, y );
                points[1] = new Point( x+10, y );
                points[2] = new Point( x+10, y-5 );
                points[3] = new Point( x+10, y+5 );

                for( Point point : points )
                    Utility.rotate( point, points[0], rotation );

                renderer.objects.add( new Line( points[0], points[1], color));
                renderer.objects.add( new Line( points[2], points[3], color));

                break;
        }
    }

    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor( color );

    }
}

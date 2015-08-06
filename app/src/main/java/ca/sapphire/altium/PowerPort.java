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
import ca.sapphire.altium.Options;

/**
 * Contains an Altium Power Port object
 */
public class PowerPort implements Object, Serializable {
    // data to read and write
    int x, y, color;
    byte style, rotation, fontId;
    boolean hidden, locked;
    String name;

    // data for rendering
    float[] linepts;
    float[] circlePts;
    float textSize;

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

    public void read( DataInputStream dis ) throws IOException {
        x = dis.readInt();
        y = dis.readInt();
        color = dis.readInt();
        style = dis.readByte();
        rotation = dis.readByte();
        fontId = dis.readByte();
        hidden = dis.readBoolean();
        locked = dis.readBoolean();
        name = dis.readUTF();
    }

    public void write( DataOutputStream dos ) throws IOException {
        dos.writeInt( x );
        dos.writeInt(y);
        dos.writeInt(color);
        dos.writeByte(style);
        dos.writeByte(rotation);
        dos.writeByte(fontId);
        dos.writeBoolean(hidden);
        dos.writeBoolean(locked);
        dos.writeUTF( name );
//        render( null );
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

    public void render() {
//        fontSize = fonts.get( fontId ).size

    }

    public void render( Render renderer ) {
        // TODO: Add all rendering types
        // TODO: Fix text rendering location
        // Currently only renders bar, arrow without text

        //renderer.addText(name, x, y, fontId, color);

//        textSize = renderer.fonts.get( fontId ).size;
        textSize = Options.INSTANCE.fontSize[fontId];

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

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        paint.setTextSize(textSize);
        canvas.drawText(name, x, y, paint);


    }
}

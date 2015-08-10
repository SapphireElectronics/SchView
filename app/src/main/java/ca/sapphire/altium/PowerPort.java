package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import ca.sapphire.graphics.Line;
import ca.sapphire.graphics.Text;

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
    PointF[] linepts;
    PointF[] circlePts;
    PointF textpt;
    float textSize;
    Text tag;

    public PowerPort( Map<String, String> record ) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getColor(record);
        style = Utility.getByteValue(record, "STYLE", (byte) 0);
        rotation = Utility.getByteValue(record, "ORIENTATION", (byte) 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        hidden = !Utility.getBooleanValue(record, "SHOWNETNAME");
        locked = Utility.getBooleanValue(record, "GRAPHICALLYLOCKED", false);
        name = record.get("TEXT");
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
        // TODO: Add all rendering types
        // TODO: Fix text rendering location
        // Currently only renders bar, arrow without text

        textSize = Options.INSTANCE.fontSize[fontId-1];

        switch( style ) {
            case 1: // Arrow
                linepts = new PointF[8];

                linepts[0] = new PointF( x, y );
                linepts[1] = new PointF( x+4, y );
                linepts[2] = new PointF( x+4, y-3 );
                linepts[3] = new PointF( x+4, y+3 );
                linepts[4] = new PointF( x+4, y+3 );
                linepts[5] = new PointF( x+10, y );
                linepts[6] = new PointF( x+10, y );
                linepts[7] = new PointF( x+4, y-3 );

                textpt = new PointF( x+10+5, y );
                break;

            default:    // bar
                linepts = new PointF[4];

                linepts[0] = new PointF( x, y );
                linepts[1] = new PointF( x+10, y );
                linepts[2] = new PointF( x+10, y-5 );
                linepts[3] = new PointF( x+10, y+5 );

                textpt = new PointF( x+10+5, y );
                break;
        }
        for( PointF point : linepts )
            Utility.rotate( point, linepts[0], rotation );

        Utility.rotate( textpt, linepts[0], rotation );
        textpt.y = -textpt.y;


        switch( rotation ) {
            case 0:
                tag = new Text( name, textpt, color, Text.Halign.LEFT, Text.Valign.CENTER );
                break;

            case 1:
                tag = new Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.BOTTOM );
                break;

            case 2:
                tag = new Text( name, textpt, color, Text.Halign.RIGHT, Text.Valign.CENTER );
                break;

            case 3:
                tag = new Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.TOP );
                break;

        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setTextSize(textSize);
        paint.setColor( color );
        tag.draw( canvas, paint );

        for (int i = 0; i < linepts.length; i+=2) {
            canvas.drawLine( linepts[i].x, -linepts[i].y, linepts[i+1].x, -linepts[i+1].y, paint );
        }
    }
}

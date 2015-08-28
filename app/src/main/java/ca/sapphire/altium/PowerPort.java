package ca.sapphire.altium;

import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;
import ca.sapphire.graphics.Text;

/**
 * Contains an Altium Power Port object
 */
public class PowerPort implements SchObject {
    // data to read and write
    int x, y, color;
    byte style, rotation, fontId;
    boolean showNetName, locked;
    String name;

    // data for rendering
    PointF[] linepts;
//    PointF[] circlePts;
    PointF textpt;
    float textSize;
    Text tag;

    public PowerPort( Map<String, String> record ) {
        x = Utility.getIntValue(record, "LOCATION.X");
        y = -Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getColor(record);
        style = Utility.getByteValue(record, "STYLE", (byte) 0);
        rotation = Utility.getByteValue(record, "ORIENTATION", (byte) 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        showNetName = Utility.getBooleanValue(record, "SHOWNETNAME", true );
        locked = Utility.getBooleanValue(record, "GRAPHICALLYLOCKED", false);
        name = record.get("TEXT");
    }

//    @Override
//    public void draw(Canvas canvas, Paint paint) {
//        paint.setTextSize(textSize);
//        paint.setColor(color);
//        tag.draw(canvas, paint);
//
//        for (int i = 0; i < linepts.length; i+=2) {
//            canvas.drawLine( linepts[i].x, linepts[i].y, linepts[i+1].x, linepts[i+1].y, paint );
//        }
//    }


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

    // define the default alignment options given an orientation (rotation) as an index
    public static final GrEngine.Halign nameHalign[] = { GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER };
    public static final GrEngine.Valign nameValign[] = { GrEngine.Valign.CENTER, GrEngine.Valign.BOTTOM, GrEngine.Valign.CENTER, GrEngine.Valign.TOP };


    @Override
    public void render( GrEngine engine ) {
        // TODO: Add all rendering types
        // Currently only renders bar, arrow

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

                textpt = new PointF( x+10+2, y );
                break;

            case 6: // Earth
                linepts = new PointF[10];

                linepts[0] = new PointF( x, y );
                linepts[1] = new PointF( x+10, y );
                linepts[2] = new PointF( x+10, y-10 );
                linepts[3] = new PointF( x+10, y+10 );
                linepts[4] = new PointF( x+10, y-10 );
                linepts[5] = new PointF( x+20, y-5 );
                linepts[6] = new PointF( x+10, y );
                linepts[7] = new PointF( x+20, y+5 );
                linepts[8] = new PointF( x+10, y+10 );
                linepts[9] = new PointF( x+20, y+15 );

                textpt = new PointF( x+20+2, y );
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

            for (int i = 0; i < linepts.length; i+=2)
                engine.addLine(linepts[i].x, linepts[i].y, linepts[i + 1].x, linepts[i + 1].y, color);

        if( showNetName ) {
            Utility.rotate(textpt, linepts[0], rotation);
            engine.addText(name, textpt.x, textpt.y, color, textSize, nameHalign[rotation], nameValign[rotation]);
        }
    }


    @Override
    public void read( DataInputStream dis ) throws IOException {
        x = dis.readInt();
        y = dis.readInt();
        color = dis.readInt();
        style = dis.readByte();
        rotation = dis.readByte();
        fontId = dis.readByte();
        showNetName = dis.readBoolean();
        locked = dis.readBoolean();
        name = dis.readUTF();
    }

    @Override
    public void write( DataOutputStream dos ) throws IOException {
        dos.writeInt( x );
        dos.writeInt(y);
        dos.writeInt(color);
        dos.writeByte(style);
        dos.writeByte(rotation);
        dos.writeByte(fontId);
        dos.writeBoolean(showNetName);
        dos.writeBoolean(locked);
        dos.writeUTF( name );
//        render( null );
    }
}

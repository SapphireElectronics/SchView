package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;
import ca.sapphire.graphics.Text;

/**
 * Contains an Altium pin
 */
public class Pin extends SchBase implements SchObject {
    public static final int PIN_INVISIBLE = 0b00100;
    public static final int NAME_VISIBLE = 0b01000;
    public static final int DESIGNATOR_VISIBLE = 0b10000;


    int x, y, length, option, color, fontId, textSize, orientation;
    String name, designator;

    PointF pnt1, pnt2;
    boolean drawable = true;
    ca.sapphire.graphics.Text nameTag, desTag;
    PointF nameTextpt, desTextpt;

    GrEngine engine;

    public Pin( Map<String, String> record, boolean multiPartComponent ) {

//    public Pin( Map<String, String> record, boolean multiPartComponent  ) {
        super(record);
        this.engine = engine;
        x = Utility.getIntValue(record, "LOCATION.X");
        y = -Utility.getIntValue(record, "LOCATION.Y");
        length = Integer.parseInt(record.get("PINLENGTH"));
//        designator = Integer.parseInt(record.get("DESIGNATOR"));
        option = Integer.parseInt(record.get("PINCONGLOMERATE"));
//        color = Utility.getColor(record);
        name = record.get("NAME");
        if( name == null )
            name = "";
        designator = record.get("DESIGNATOR");
        if( designator == null )
            designator = "";

        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        orientation = option & 0x03;

        if( multiPartComponent ) {
            if( record.get("OWNERPARTDISPLAYMODE") != null)
                drawable = false;
        }
    }

    @Override
    public void render( GrEngine engine ) {
        if( !drawable )
            return;

        pnt1 = new PointF( x, y );
        pnt2 = new PointF(x+length, y );
        Utility.rotate( pnt2, pnt1, orientation );
        color = 0xff000000;

        textSize = Options.INSTANCE.fontSize[fontId-1];

        nameTextpt = new PointF( x-5, y );
        Utility.rotate(nameTextpt, pnt1, orientation );

        desTextpt = new PointF( x+8, y );
        Utility.rotate(desTextpt, pnt1, orientation );

//        // TODO check designator (pin number) orientation in Altium
        if( !drawable )
            return;

        if( (option & PIN_INVISIBLE) > 0 )
            return;

        engine.addLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, color, 0);

        if( (option & NAME_VISIBLE) > 0 ) {
            switch( orientation ) {
                case 0:
                    engine.addText(name, nameTextpt.x, nameTextpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.CENTER);
                    break;
                case 1:
                    engine.addText( name, nameTextpt.x, nameTextpt.y, color, textSize, GrEngine.Halign.CENTER, GrEngine.Valign.TOP );
                    break;
                case 2:
                    engine.addText( name, nameTextpt.x, nameTextpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.CENTER );
                    break;
                case 3:
                    engine.addText( name, nameTextpt.x, nameTextpt.y, color, textSize, GrEngine.Halign.CENTER, GrEngine.Valign.BOTTOM );
                    break;
            }
        }

        if( (option & DESIGNATOR_VISIBLE) > 0 ) {
            switch( orientation ) {
                case 0:
                    engine.addText( designator, desTextpt.x, desTextpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.BOTTOM );
                    break;
                case 1:
                    engine.addText( designator, desTextpt.x, desTextpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.BOTTOM );
                    break;
                case 2:
                    engine.addText( designator, desTextpt.x, desTextpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.BOTTOM );
                    break;
                case 3:
                    engine.addText( designator, desTextpt.x, desTextpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.BOTTOM );
                    break;
            }
        }
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

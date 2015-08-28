package ca.sapphire.altium;

import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium pin
 */
public class Pin extends SchBase implements SchObject {
    public static final int PIN_INVISIBLE = 0b00100;
    public static final int NAME_VISIBLE = 0b01000;
    public static final int DESIGNATOR_VISIBLE = 0b10000;

    float x,y;

    int length, option, color, fontId, textSize, orientation;
    int nameOption, nameLocation;
    int desOption, desLocation;
    String name, designator;

    PointF pnt1, pnt2;
    boolean drawable = true;
    PointF nameTextpt, desTextpt;

    GrEngine engine;

    public Pin( Map<String, String> record, boolean multiPartComponent ) {
        super(record);
        x = Utility.addLocationX( record );
        y = -Utility.addLocationY( record );
        length = Integer.parseInt(record.get("PINLENGTH"));
//        designator = Integer.parseInt(record.get("DESIGNATOR"));
        option = Integer.parseInt(record.get("PINCONGLOMERATE"));
        color = Utility.getColor(record);
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

        nameOption = Utility.getIntValue(record, "PINNAME_POSITIONCONGLOMERATE", 0);
        nameLocation = Utility.getIntValue(record, "NAME_CUSTOMPOSITION_MARGIN", 0);

        desOption = Utility.getIntValue(record, "PINDESIGNATOR_POSITIONCONGLOMERATE", 0);
        desLocation = Utility.getIntValue(record, "DESIGNATOR_CUSTOMPOSITION_MARGIN", 0);


    }

    // define the default alignment options given an orientation (rotation) as an index
    public static final GrEngine.Halign nameHalign[] = { GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT, GrEngine.Halign.CENTER };
    public static final GrEngine.Valign nameValign[] = { GrEngine.Valign.CENTER, GrEngine.Valign.TOP, GrEngine.Valign.CENTER, GrEngine.Valign.BOTTOM };
    public static final GrEngine.Halign desHalign[] = { GrEngine.Halign.LEFT, GrEngine.Halign.LEFT, GrEngine.Halign.RIGHT, GrEngine.Halign.RIGHT };
    public static final GrEngine.Valign desValign[] = { GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM };


    @Override
    public void render( GrEngine engine ) {
        if( !drawable )
            return;

        pnt1 = new PointF( x, y );
        pnt2 = new PointF(x+length, y );
        Utility.rotate( pnt2, pnt1, orientation );

        textSize = Options.INSTANCE.fontSize[fontId-1];

        if( nameOption == 0)
            nameTextpt = new PointF( x-5, y );
        else
            nameTextpt = new PointF( x-nameLocation, y );

        Utility.rotate(nameTextpt, pnt1, orientation );

        if( desOption == 0 )
            desTextpt = new PointF( x+8, y-2 );
        else
            desTextpt = new PointF( x+desLocation, y-2 );

        Utility.rotate(desTextpt, pnt1, orientation );

//        // TODO check designator (pin number) orientation in Altium
        if( !drawable )
            return;

        if( (option & PIN_INVISIBLE) > 0 )
            return;

        engine.addLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, color, 0);

        if( (option & NAME_VISIBLE) > 0 )
            engine.addText(name, nameTextpt.x, nameTextpt.y, color, textSize, nameHalign[orientation], nameValign[orientation] );

        if( (option & DESIGNATOR_VISIBLE) > 0 )
            engine.addText( designator, desTextpt.x, desTextpt.y, color, textSize, desHalign[ orientation], desValign[ orientation ] );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

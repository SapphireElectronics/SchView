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
public class Pin extends SchBase implements Object {
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

    public Pin( Map<String, String> record, boolean multiPartComponent, GrEngine engine ) {

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
    public void draw(Canvas canvas, Paint paint) {
//        if( !drawable )
//            return;
//
//        if( (option & PIN_INVISIBLE) > 0 )
//            return;
//
//        paint.setColor(color);
//        canvas.drawLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, paint);
//
//        paint.setTextSize(textSize);
//
//        if( (option & NAME_VISIBLE) > 0 )
//            nameTag.draw(canvas, paint);
//
//        if( (option & DESIGNATOR_VISIBLE) > 0 )
//            desTag.draw(canvas, paint);
    }

    @Override
    public void render() {
        render( engine );
    }

//    @Override
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

        desTextpt = new PointF( x+length, y );
        Utility.rotate(desTextpt, pnt1, orientation );

        // TODO check designator (pin number) orientation in Altium
        switch( orientation ) {
            case 0:
                nameTag = new Text( name, nameTextpt, color, textSize, Text.Halign.RIGHT, Text.Valign.CENTER );
                desTag = new Text( designator, desTextpt, color, textSize, Text.Halign.LEFT, Text.Valign.BOTTOM );
                break;
            case 1:
                nameTag = new Text( name, nameTextpt, color, textSize, Text.Halign.CENTER, Text.Valign.TOP );
                desTag = new Text( designator, desTextpt, color, textSize, Text.Halign.LEFT, Text.Valign.BOTTOM );
                break;
            case 2:
                nameTag = new Text( name, nameTextpt, color, textSize, Text.Halign.LEFT, Text.Valign.CENTER );
                desTag = new Text( designator, desTextpt, color, textSize, Text.Halign.RIGHT, Text.Valign.BOTTOM );
                break;
            case 3:
                nameTag = new Text( name, nameTextpt, color, textSize, Text.Halign.CENTER, Text.Valign.BOTTOM );
                desTag = new Text( designator, desTextpt, color, textSize, Text.Halign.RIGHT, Text.Valign.BOTTOM );
                break;
        }

        if( !drawable )
            return;

        if( (option & PIN_INVISIBLE) > 0 )
            return;

//        paint.setColor(color);
//        canvas.drawLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, paint);
//
//        paint.setTextSize(textSize);
//
//        if( (option & NAME_VISIBLE) > 0 )
//            nameTag.draw(canvas, paint);
//
//        if( (option & DESIGNATOR_VISIBLE) > 0 )
//            desTag.draw(canvas, paint);

        engine.addLine(pnt1.x, pnt1.y, pnt2.x, pnt2.y, color, 0);

        if( (option & NAME_VISIBLE) > 0 )
            engine.addText( name, nameTag.draw.x, nameTag.draw.y, color, textSize );

        if( (option & DESIGNATOR_VISIBLE) > 0 )
            engine.addText( designator, desTag.draw.x, desTag.draw.y, color, textSize );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

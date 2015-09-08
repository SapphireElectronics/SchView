package ca.sapphire.altium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.altium.SchObject;
import ca.sapphire.altium.Utility;
import ca.sapphire.graphics.GrEngine;
import ca.sapphire.graphics.Text;


/**
 * Created by Admin on 29/08/15.
 */
public class Port  implements SchObject {
    float x, y;
    int width, height, textColor, areaColor, style, ioType, fontId, textSize;
    String name;

    public Port( Map<String, String> record ) {
        x = Utility.addLocationX( record );
        y = -Utility.addLocationY(record);
        width = Utility.getIntValue(record, "WIDTH");
        height = Utility.getIntValue(record, "HEIGHT");
        style = Utility.getIntValue( record, "STYLE" );
        ioType = Utility.getIntValue( record, "IOTYPE", 0 );
        name = record.get("NAME");
        textColor = Utility.getColor(record, "TEXTCOLOR");
        areaColor = Utility.getColor(record, "AREACOLOR");
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);

    }

    public void render( GrEngine engine ) {
        textSize = Options.INSTANCE.fontSize[fontId-1];
        if( style <= 3 ) {
            engine.addRect( x, y-height/2, x+width, y+height/2, areaColor, true );
            engine.addRect( x, y-height/2, x+width, y+height/2, textColor, false );
            engine.addText(name, x+textSize, y, textColor, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.CENTER, 0 );
        }
        else {
            engine.addRect( x-height/2, y, x+height/2, y-width, areaColor, true );
            engine.addRect( x-height/2, y, x+height/2, y-width, textColor, false );
            engine.addText(name, x-textSize/2, y-width, textColor, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.CENTER, 1);
        }

    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}

}

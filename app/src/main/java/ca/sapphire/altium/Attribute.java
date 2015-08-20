package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;
import ca.sapphire.graphics.Text;

/**
 * Contains an Altium text attribute
 */
public class Attribute extends SchBase implements SchObject {
    int x, y, color, justification, orientation, fontId;
    String name;
    boolean hidden;

    PointF textpt;
    float textSize;
    ca.sapphire.graphics.Text tag;

    public Attribute( Map<String, String> record) {
        super( record );
        x = Utility.getIntValue(record, "LOCATION.X", 0);
        y = -Utility.getIntValue(record, "LOCATION.Y", 0);
        color = Utility.getColor(record);
        name = record.get("TEXT");
        justification = Utility.getIntValue(record, "JUSTIFICATION", 0);
        orientation = Utility.getIntValue( record, "ORIENTATION", 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        hidden = Utility.getBooleanValue(record, "ISHIDDEN", false);
    }

//    @Override
//    public void draw(Canvas canvas, Paint paint) {
//        if( hidden )
//            return;
//        paint.setTextSize(textSize);
//        paint.setColor( color );
//        tag.draw( canvas, paint );
//    }

    @Override
    public void render( GrEngine engine ) {
        if( hidden || name == null )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];

        textpt = new PointF( x, y );
        if( orientation == 2)
            textpt.y += textSize;
//        Utility.rotate( textpt, orientation );

        switch( justification ) {
            case 0: // Bottom Left
                engine.addText(name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.BOTTOM);
                break;
            case 1: // Bottom Centre
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.CENTER, GrEngine.Valign.BOTTOM );
                break;
            case 2: // Bottom Right
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.BOTTOM );
                break;
            case 3: // Centre Left
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.CENTER );
                break;
            case 4: // Centre Centre
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.CENTER, GrEngine.Valign.CENTER );
                break;
            case 5: // Centre Right
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.CENTER );
                break;
            case 6: // Top Left
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.LEFT, GrEngine.Valign.TOP );
                break;
            case 7: // Top Centre
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.CENTER, GrEngine.Valign.TOP );
                break;
            case 8: // Top Right
                engine.addText( name, textpt.x, textpt.y, color, textSize, GrEngine.Halign.RIGHT, GrEngine.Valign.TOP );
                break;
        }

    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

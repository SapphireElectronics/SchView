package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;
import ca.sapphire.graphics.Text;

/**
 * Contains an Altium reference designator
 */
public class Designator extends SchBase implements SchObject {
    int x, y, color, justification, orientation, fontId;
    String name;
    boolean isHidden;

    PointF textpt;
    float textSize;
    ca.sapphire.graphics.Text tag;

    public Designator( Map<String, String> record) {
        super( record );
        x = Utility.getIntValue(record, "LOCATION.X");
        y = -Utility.getIntValue(record, "LOCATION.Y");
        color = Utility.getColor(record);
        name = record.get("TEXT");
        justification = Utility.getIntValue(record, "JUSTIFICATION", 0);
        orientation = Utility.getIntValue( record, "ORIENTATION", 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        isHidden = Utility.getBooleanValue(record, "ISHIDDEN", false );
    }

//    @Override
//    public void draw(Canvas canvas, Paint paint) {
//        paint.setTextSize(textSize);
//        paint.setColor( color );
//        tag.draw( canvas, paint );
//    }

    @Override
    public void render( GrEngine engine ) {
        if( isHidden )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];

        textpt = new PointF( x, y );
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

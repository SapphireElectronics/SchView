package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.Text;

/**
 * Contains an Altium reference designator
 */
public class Designator extends SchBase implements Object {
    int x, y, color, justification, orientation, fontId;
    String name;

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
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setTextSize(textSize);
        paint.setColor( color );
        tag.draw( canvas, paint );
    }

    @Override
    public void render() {
        textSize = Options.INSTANCE.fontSize[fontId-1];

        textpt = new PointF( x, y );
//        Utility.rotate( textpt, orientation );

        switch( justification ) {
            case 0: // Bottom Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.LEFT, Text.Valign.BOTTOM );
                break;
            case 1: // Bottom Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.BOTTOM );
                break;
            case 2: // Bottom Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.RIGHT, Text.Valign.BOTTOM );
                break;
            case 3: // Centre Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.LEFT, Text.Valign.CENTER );
                break;
            case 4: // Centre Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.CENTER );
                break;
            case 5: // Centre Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.RIGHT, Text.Valign.CENTER );
                break;
            case 6: // Top Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.LEFT, Text.Valign.TOP );
                break;
            case 7: // Top Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.TOP );
                break;
            case 8: // Top Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, Text.Halign.RIGHT, Text.Valign.TOP );
                break;
        }
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

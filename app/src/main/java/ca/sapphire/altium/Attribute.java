package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.Text;

/**
 * Contains an Altium text attribute
 */
public class Attribute extends SchBase implements Object {
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
        if( name != null)
            if( name.equals( "1K")) {
                Log.i("", "Name=1k");
            }
        justification = Utility.getIntValue(record, "JUSTIFICATION", 0);
        orientation = Utility.getIntValue( record, "ORIENTATION", 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        hidden = Utility.getBooleanValue(record, "ISHIDDEN", false);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if( name != null)
            if( name.equals( "1K")) {
                Log.i("", "Name=1k");
            }
        if( hidden )
            return;
        paint.setTextSize(textSize);
        paint.setColor( color );
        tag.draw( canvas, paint );
    }

    @Override
    public void render() {
        if( name != null)
            if( name.equals( "1K")) {
                Log.i("", "Name=1k");
            }
        textSize = Options.INSTANCE.fontSize[fontId-1];

        textpt = new PointF( x, y );
        if( orientation == 2)
            textpt.y += textSize;
//        Utility.rotate( textpt, orientation );

        switch( justification ) {
            case 0: // Bottom Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.LEFT, ca.sapphire.graphics.Text.Valign.BOTTOM );
                break;
            case 1: // Bottom Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.CENTER, ca.sapphire.graphics.Text.Valign.BOTTOM );
                break;
            case 2: // Bottom Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.RIGHT, ca.sapphire.graphics.Text.Valign.BOTTOM );
                break;
            case 3: // Centre Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.LEFT, ca.sapphire.graphics.Text.Valign.CENTER );
                break;
            case 4: // Centre Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.CENTER, ca.sapphire.graphics.Text.Valign.CENTER );
                break;
            case 5: // Centre Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.RIGHT, ca.sapphire.graphics.Text.Valign.CENTER );
                break;
            case 6: // Top Left
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.LEFT, ca.sapphire.graphics.Text.Valign.TOP );
                break;
            case 7: // Top Centre
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.CENTER, ca.sapphire.graphics.Text.Valign.TOP );
                break;
            case 8: // Top Right
                tag = new ca.sapphire.graphics.Text( name, textpt, color, ca.sapphire.graphics.Text.Halign.RIGHT, ca.sapphire.graphics.Text.Valign.TOP );
                break;
        }

    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

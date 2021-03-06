package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium text object
 */
public class Text implements SchObject {
    int x, y, fontId, color;
    String text;
    boolean valueLookup = false;

    float textSize;

    public Text( Map<String, String> record, String[] pairs ) {
        x = Integer.parseInt(record.get("LOCATION.X"));
        y = -Integer.parseInt(record.get("LOCATION.Y"));
        fontId = Integer.parseInt(record.get("FONTID"));
        color = Utility.getColor(record);
        text = record.get("TEXT");

        for( String pair : pairs ) {
            if( pair.contains( "==" )) {
                text = pair.substring(pair.lastIndexOf("=") + 1);
                valueLookup = true;
            }
        }
    }

//    @Override
//    public void draw(Canvas canvas, Paint paint) {
//        paint.setColor(color);
//        paint.setTextSize(textSize);
//        canvas.drawText(text, x, y, paint);
//    }

    @Override
    public void render( GrEngine engine ) {
        if( valueLookup )
            text = Options.projectOptions.get( text.toLowerCase() );

        if( text == null )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];
        engine.addText( text, x, y, color, textSize );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

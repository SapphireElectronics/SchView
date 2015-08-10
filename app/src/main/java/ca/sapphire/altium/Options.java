package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

/**
 * Contains an Altium Options record
 */

public enum Options implements Object {
    INSTANCE;

    byte fontCount, fontSize[], fontRotation[];
    String fontName[];

    public void put( Map<String, String> record, Render renderer ) {
        fontCount = Utility.getByteValue( record, "FONTIDCOUNT", (byte) 0 );

        fontSize = new byte[fontCount];
        fontRotation = new byte[fontCount];
        fontName = new String[fontCount];

        for (int i = 0; i < fontCount; i++) {
            fontSize[i] = Utility.getByteValue(record, "SIZE" + String.valueOf(i + 1));
            fontRotation[i] = Utility.getByteValue(record, "ROTATION" + String.valueOf(i + 1), (byte) 0);
            fontName[i] = record.get("FONTNAME" + String.valueOf(i + 1));
        }
//        render( renderer );
    }

    public void put( Map<String, String> record ) {
        fontCount = Utility.getByteValue( record, "FONTIDCOUNT", (byte) 0 );

        fontSize = new byte[fontCount];
        fontRotation = new byte[fontCount];
        fontName = new String[fontCount];

        for (int i = 0; i < fontCount; i++) {
            fontSize[i] = Utility.getByteValue(record, "SIZE" + String.valueOf(i + 1));
            fontRotation[i] = Utility.getByteValue(record, "ROTATION" + String.valueOf(i + 1), (byte) 0);
            fontName[i] = record.get("FONTNAME" + String.valueOf(i + 1));
        }
    }


    @Override
    public void draw( Canvas canvas, Paint paint ) {}

    @Override
    public void read( DataInputStream dis ) {}

    @Override
    public void write( DataOutputStream dos ) {}

    @Override
    public void render() {}

//    public void render( Render renderer ) {
//        for (int i = 0; i < fontCount; i++) {
//            renderer.addFont(fontSize[i], fontName[i]);
//        }
//    }
}

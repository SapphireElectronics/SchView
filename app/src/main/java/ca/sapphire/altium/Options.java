package ca.sapphire.altium;

import java.io.Serializable;
import java.util.Map;

/**
 * Contains an Altium Options record
 */

public class Options implements Object, Serializable {
    byte fontCount, fontSize[], fontRotation[];
    String fontName[];

    public Options( Map<String, String> record, Render renderer ) {
        fontCount = Utility.getByteValue( record, "FONTIDCOUNT", (byte) 0 );

        fontSize = new byte[fontCount];
        fontRotation = new byte[fontCount];
        fontName = new String[fontCount];

        for (int i = 0; i < fontCount; i++) {
            fontSize[i] = Utility.getByteValue(record, "SIZE" + String.valueOf(i + 1));
            fontRotation[i] = Utility.getByteValue(record, "ROTATION" + String.valueOf(i + 1), (byte) 0);
            fontName[i] = record.get("FONTNAME" + String.valueOf(i + 1));
        }
        render( renderer );
    }

    public void render( Render renderer ) {
        for (int i = 0; i < fontCount; i++)
            renderer.addFont(fontSize[i], fontName[i]);
    }
}

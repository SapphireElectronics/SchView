package ca.sapphire.altium;

import android.graphics.PointF;

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
    int x, y, color, justification, orientation, fontId, owner;
    String name, text;
    boolean hidden;

    PointF textpt;
    float textSize;
    ca.sapphire.graphics.Text tag;

//    static public Map<String, String> projectOptions = new HashMap<>();

    public Attribute( Map<String, String> record ) {
        super( record );
        x = Utility.getIntValue(record, "LOCATION.X", 0);
        y = -Utility.getIntValue(record, "LOCATION.Y", 0);
        color = Utility.getColor(record);
        name = record.get("NAME");
        text = record.get("TEXT");
        justification = Utility.getIntValue(record, "JUSTIFICATION", 0);
        orientation = Utility.getIntValue( record, "ORIENTATION", 0);
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        hidden = Utility.getBooleanValue(record, "ISHIDDEN", false);
        owner = Utility.getIntValue(record, "OWNERINDEX", 0);

        // if owner is 0, this is a Project Attribute
        if( owner == 0 ) {
            Options.projectOptions.put(name.toLowerCase(), text );
        }
    }

    @Override
    public void render( GrEngine engine ) {
        if( hidden || text == null )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];
        engine.addText(text, x, y, color, textSize, Text.textHalign[orientation][justification], Text.textValign[orientation][justification] );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

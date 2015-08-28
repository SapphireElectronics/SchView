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

    @Override
    public void render( GrEngine engine ) {
        if( hidden || name == null )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];
        engine.addText(name, x, y, color, textSize, Text.teztHalign[orientation][justification], Text.textValign[orientation][justification] );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

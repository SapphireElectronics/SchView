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

    @Override
    public void render( GrEngine engine ) {
        if( isHidden )
            return;

        textSize = Options.INSTANCE.fontSize[fontId-1];
        engine.addText(name, x, y, color, textSize, Text.textHalign[orientation][justification], Text.textValign[orientation][justification] );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

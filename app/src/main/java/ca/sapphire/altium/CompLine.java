package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium Component Line single segment line
 */
public class CompLine extends SchBase implements SchObject {
    int x1, y1, x2, y2, color;
    boolean drawable = true;

    public CompLine( Map<String, String> record, boolean multiPartComponent ) {
        super( record );
        x1 = Integer.parseInt(record.get("LOCATION.X"));
        y1 = -Integer.parseInt(record.get("LOCATION.Y"));
        x2 = Integer.parseInt(record.get("CORNER.X"));
        y2 = -Integer.parseInt(record.get("CORNER.Y"));
        color = Utility.getColor(record);

        if( multiPartComponent ) {
            if( record.get("OWNERPARTDISPLAYMODE") != null)
                drawable = false;
        }
    }

    @Override
    public void render( GrEngine engine ) {
        if( !drawable )
            return;
        engine.addLine( x1, y1, x2, y2, color, 0 );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}



}


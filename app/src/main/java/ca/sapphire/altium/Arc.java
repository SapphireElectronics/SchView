package ca.sapphire.altium;

import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.altium.SchObject;
import ca.sapphire.altium.Utility;
import ca.sapphire.graphics.GrEngine;

/**
 * Created by apreston on 8/24/2015.
 */
public class Arc implements SchObject {
    int color, radius, width;
    float startAngle, endAngle;
    PointF centre;
    boolean drawable = true;

    public Arc(  Map<String, String> record, boolean multiPartComponent ) {
        centre = Utility.addLocation( record );
        color = Utility.altiumToRGB(Integer.parseInt(record.get("COLOR")));
        startAngle = Utility.getFloatValue(record, "STARTANGLE", 0f);
        endAngle = Utility.getFloatValue(record, "ENDANGLE", 360f);
        radius = Utility.getIntValue( record, "RADIUS", 1 );
        if( multiPartComponent ) {
            if (record.get("OWNERPARTDISPLAYMODE") != null)
                drawable = false;
        }
    }

    @Override
    public void render( GrEngine engine ) {
        if( !drawable )
            return;
        engine.addArc(centre.x, -centre.y, radius, startAngle, endAngle, color);
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}


package ca.sapphire.schview;

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
    int color, radius, width, startAngle, endAngle;
    PointF centre;

    public Arc(  Map<String, String> record ) {
        color = Utility.altiumToRGB(Integer.parseInt(record.get("COLOR")));
        startAngle = Utility.getIntValue(record, "STARTANGLE", 0);
        endAngle = Utility.getIntValue(record, "STARTANGLE", 360);
    }

    @Override
    public void render( GrEngine engine ) {
        engine.addCircle( centre.x, -centre.y,  );
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}


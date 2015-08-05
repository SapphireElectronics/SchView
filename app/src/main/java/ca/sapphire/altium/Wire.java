package ca.sapphire.altium;

import android.graphics.Point;

import java.io.Serializable;
import java.util.Map;

/**
 * Contains an Altium Wire multisegment object
 */
public class Wire implements Object, Serializable {
    int color;
    Point point[];

    public Wire(Map<String, String> record, Render renderer) {
        point = Utility.addMultiLine(record);
        color = Utility.getIntValue(record, "COLOR");
        render( renderer );
    }

    public void render( Render renderer ) {
        for (int i = 0; i < point.length-1; i++)
            renderer.addLine( point[i], point[i+1], color );
    }
}


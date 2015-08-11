package ca.sapphire.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Graphics rendering engine
 */
public class GrEngine {
    // Graphics Lines:  List of lines, sorted by color.
    public Map<Integer, List<Float>> lines = new android.support.v4.util.ArrayMap<>();

    float[][] floatArray;

    public void addLine(float x1, float y1, float x2, float y2, int color) {
        List<Float> coords = new ArrayList<>();
        coords.add(x1);
        coords.add(y1);
        coords.add(x2);
        coords.add(y2);
        if( !lines.containsKey( color )) {
            lines.put(color, coords );
        }
        else {
            lines.get( color ).addAll( coords );
        }
    }

    public void addRectangle( float x1, float y1, float x2, float y2, int color ) {
        addLine( x1, y1, x2, y1, color );
        addLine( x2, y1, x2, y2, color );
        addLine( x2, y2, x1, y2, color );
        addLine( x1, y2, x1, y1, color );
    }

    public void render() {
        floatArray = new float[lines.size()][];

        int arrayPt=0;
        for (Map.Entry<Integer, List<Float>> entry : lines.entrySet()) {
            floatArray[arrayPt] = new float[ entry.getValue().size() ];

            int i=0;
            for (Float f : entry.getValue() ) {
                floatArray[arrayPt][i++] = f;
            }

            arrayPt++;
        }
    }

    public void draw( Canvas canvas, Paint paint) {
        int arrayPt=0;
        for (Map.Entry<Integer, List<Float>> entry : lines.entrySet()) {
            paint.setColor( entry.getKey() );
            canvas.drawLines( floatArray[arrayPt++], paint );
        }
    }
}

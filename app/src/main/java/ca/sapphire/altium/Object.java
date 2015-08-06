package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Map;

/**
 * Created by apreston on 8/4/2015.
 */
public interface Object {
    void render( Render renderer );
    void draw( Canvas canvas, Paint paint );
}

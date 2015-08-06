package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by apreston on 8/6/2015.
 */
public class Font implements Object {
    public Font() {

    };

    public void read( DataInputStream dis ) throws IOException {
    }

    public void write( DataOutputStream dos ) throws IOException {
    }

    public void render() {}

    public void render( Render renderer ) {}

    public void draw( Canvas canvas, Paint paint ) {}
}

package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 07/08/15.
 */
public class Component extends SchBase implements Object {
    public int displayModeCount;

    public Component( Map<String, String> record ) {
        super( record );
        displayModeCount = Utility.getIntValue(record, "DISPLAYMODECOUNT");
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void render() {

    }

    @Override
    public void draw(Canvas canvas, Paint paint) {

    }
}

package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Contains an Altium component
 */
public class Component extends SchBase implements Object {
    public int displayModeCount;

    public Component( Map<String, String> record ) {
        super( record );
        displayModeCount = Utility.getIntValue(record, "DISPLAYMODECOUNT");
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {}

    @Override
    public void render() {}

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

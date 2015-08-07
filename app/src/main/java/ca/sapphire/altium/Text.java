package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Admin on 06/08/15.
 */
public class Text implements Object {
    int x, y, fontId, color;
    String text;

    float textSize;


    public Text( Map<String, String> record ) {
        x = Integer.parseInt(record.get("LOCATION.X"));
        y = Integer.parseInt(record.get("LOCATION.Y"));
        fontId = Integer.parseInt(record.get("FONTID"));
        color = Utility.getColor(record);
        text = record.get("TEXT");
    }

    @Override
    public void read(DataInputStream dis) throws IOException {

    }

    @Override
    public void write(DataOutputStream dos) throws IOException {

    }

    @Override
    public void render() {
        textSize = Options.INSTANCE.fontSize[fontId];
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        paint.setTextSize(textSize);
        canvas.drawText(text, x, -y, paint);
    }
}

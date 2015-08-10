package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import ca.sapphire.graphics.Text;

/**
 * Contains an Altium pin
 */
public class Pin extends SchBase implements Object {
    int x, y, length, designator, option, color, fontId, textSize, orientation;
    String name;

    PointF pnt1, pnt2;
    boolean drawable = true;
    ca.sapphire.graphics.Text tag;
    PointF textpt;

    public Pin( Map<String, String> record, boolean multiPartComponent  ) {
        super(record);
        x = Utility.getIntValue(record, "LOCATION.X");
        y = -Utility.getIntValue(record, "LOCATION.Y");
        length = Integer.parseInt(record.get("PINLENGTH"));
        designator = Integer.parseInt(record.get("DESIGNATOR"));
        option = Integer.parseInt(record.get("PINCONGLOMERATE"));
//        color = Utility.getColor(record);
        name = record.get("NAME");
        if( name == null )
            name = "";
        fontId = Utility.getByteValue(record, "FONTID", (byte) 1);
        orientation = option & 0x03;

        if( multiPartComponent ) {
            if( record.get("OWNERPARTDISPLAYMODE") != null)
                drawable = false;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if( !drawable )
            return;

        paint.setColor( color );
        canvas.drawLine( pnt1.x, pnt1.y, pnt2.x, pnt2.y, paint );

        paint.setTextSize(textSize);
        tag.draw( canvas, paint );
    }

    @Override
    public void render() {
        if( !drawable )
            return;

        pnt1 = new PointF( x, y );
        pnt2 = new PointF(x+length, y );
        Utility.rotate( pnt2, pnt1, orientation );
        color = 0xff0000ff;

        textSize = Options.INSTANCE.fontSize[fontId-1];

        textpt = new PointF( x-5, y );
        Utility.rotate( textpt, pnt1, orientation );

        switch( orientation ) {
            case 0:
                tag = new Text( name, textpt, color, Text.Halign.RIGHT, Text.Valign.CENTER );
                break;
            case 1:
                tag = new Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.TOP );
                break;
            case 2:
                tag = new Text( name, textpt, color, Text.Halign.LEFT, Text.Valign.CENTER );
                break;
            case 3:
                tag = new Text( name, textpt, color, Text.Halign.CENTER, Text.Valign.BOTTOM );
                break;
        }
    }

    @Override
    public void read(DataInputStream dis) throws IOException {}

    @Override
    public void write(DataOutputStream dos) throws IOException {}
}

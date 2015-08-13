package ca.sapphire.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by Admin on 07/08/15.
 */
public class Text implements GraphicsObject {
    public static enum Halign { LEFT, CENTER, RIGHT };
    public static enum Valign { TOP, CENTER, BOTTOM };
    PointF location;
    int color;
    String text;

    public Halign hAlign;
    public Valign vAlign;

    public Text( String text, PointF location, int color, Halign hAlign, Valign vAlign )
    {
        this.text = text;
        this.location = location;
        this.color = color;
        this.hAlign = hAlign;
        this.vAlign = vAlign;


    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds( text, 0, text.length(), bounds );

        PointF draw = new PointF(location.x, location.y);

        switch( hAlign ) {
            case CENTER:
                draw.x -= bounds.exactCenterX();
                break;
            case RIGHT:
                draw.x -= ( bounds.width() + 1 );
        }

        switch( vAlign ) {
            case TOP:
                draw.y += bounds.height();
                break;
            case CENTER:
                draw.y -= bounds.exactCenterY();
        }

        paint.setColor( color );
        canvas.drawText( text, draw.x, draw.y, paint );
    }
}

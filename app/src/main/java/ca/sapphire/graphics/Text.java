package ca.sapphire.graphics;

// Todo: figure out when null text objects are coming from, they may be slowing things down

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import ca.sapphire.altium.Options;

/**
 * Created by Admin on 07/08/15.
 */

public class Text implements GraphicsObject {
    public static enum Halign { LEFT, CENTER, RIGHT };
    public static enum Valign { TOP, CENTER, BOTTOM };

    public static final GrEngine.Halign textHalign[][] = {
            {GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT,
                    GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT,
                    GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT},
            {GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT,
                    GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT,
                    GrEngine.Halign.LEFT, GrEngine.Halign.CENTER, GrEngine.Halign.RIGHT},
            {GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT,
                    GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT,
                    GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT},
            {GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT,
                    GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT,
                    GrEngine.Halign.RIGHT, GrEngine.Halign.CENTER, GrEngine.Halign.LEFT}
    };
    public static final GrEngine.Valign textValign[][] = {
            {GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM,
                    GrEngine.Valign.CENTER, GrEngine.Valign.CENTER, GrEngine.Valign.CENTER,
                    GrEngine.Valign.TOP, GrEngine.Valign.TOP, GrEngine.Valign.TOP},
            {GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM,
                    GrEngine.Valign.CENTER, GrEngine.Valign.CENTER, GrEngine.Valign.CENTER,
                    GrEngine.Valign.TOP, GrEngine.Valign.TOP, GrEngine.Valign.TOP},
            {GrEngine.Valign.TOP, GrEngine.Valign.TOP, GrEngine.Valign.TOP,
                    GrEngine.Valign.CENTER, GrEngine.Valign.CENTER, GrEngine.Valign.CENTER,
                    GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM},
            {GrEngine.Valign.TOP, GrEngine.Valign.TOP, GrEngine.Valign.TOP,
                    GrEngine.Valign.CENTER, GrEngine.Valign.CENTER, GrEngine.Valign.CENTER,
                    GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM, GrEngine.Valign.BOTTOM}
    };


    public PointF location, draw;
    int color;
    float textSize;
    String text;
    Paint paint;

    public Halign hAlign;
    public Valign vAlign;

    public Text( String text, PointF location, int color, float textSize, Halign hAlign, Valign vAlign )
    {
        this.text = text;
        this.location = location;
        this.color = color;
        this.textSize = textSize;
        this.hAlign = hAlign;
        this.vAlign = vAlign;

        Paint paint = new Paint();
        paint.setTextSize( textSize );

        Rect bounds = new Rect();
        if( text != null )
            paint.getTextBounds(text, 0, text.length(), bounds);
        else
            bounds.set(0,0,0,0);

        draw = new PointF(location.x, location.y);

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


    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
//        Rect bounds = new Rect();
//        paint.getTextBounds( text, 0, text.length(), bounds );
//
//        PointF draw = new PointF(location.x, location.y);
//
//        switch( hAlign ) {
//            case CENTER:
//                draw.x -= bounds.exactCenterX();
//                break;
//            case RIGHT:
//                draw.x -= ( bounds.width() + 1 );
//        }
//
//        switch( vAlign ) {
//            case TOP:
//                draw.y += bounds.height();
//                break;
//            case CENTER:
//                draw.y -= bounds.exactCenterY();
//        }

        paint.setColor( color );
        canvas.drawText( text, draw.x, draw.y, paint );
    }
}

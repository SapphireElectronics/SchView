package ca.sapphire.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Graphics rendering engine
 */
public class GrEngine {
    public List<Paint> paintList = new ArrayList<>();

    // Graphics Lines:  List of lines, sorted by color.
    public Map<Integer, List<Float>> lines = new android.support.v4.util.ArrayMap<>();
    public Map<Integer, List<TextString>> texts = new android.support.v4.util.ArrayMap<>();
    public List<Shape> shapes = new ArrayList<>();
    public List<Circle> circles = new ArrayList<>();
    public List<Arc> arcs = new ArrayList<>();

    float[][] floatArray;

    public void addLine(float x1, float y1, float x2, float y2, int color ) {
        addLine( x1, y1, x2, y2, color, 0 );
    }


    public void addLine(float x1, float y1, float x2, float y2, int color, int width ) {
        List<Float> coords = new ArrayList<>();
        coords.add(x1);
        coords.add(y1);
        coords.add(x2);
        coords.add(y2);

        int paintIndex = getPaintIndex( color, width );

        if( !lines.containsKey( paintIndex ))
            lines.put(paintIndex, coords );
        else
            lines.get( paintIndex ).addAll( coords );
    }

    public void addRectangle( float x1, float y1, float x2, float y2, int color ) {
        addLine(x1, y1, x2, y1, color);
        addLine(x2, y1, x2, y2, color);
        addLine(x2, y2, x1, y2, color);
        addLine(x1, y2, x1, y1, color);
    }

    public void addText( String text, float x, float y, int color, float size ) {
            addText(text, x, y, color, size, Halign.LEFT, Valign.BOTTOM);
    }

    @Deprecated
    // Reason: orientation should be specified
    public void addText( String text, float x, float y, int color, float size, Halign hAlign, Valign vAlign ) {
        addText(text, x, y, color, size, hAlign, vAlign, 0);
    }

    public void addText( String text, float x, float y, int color, float size, Halign hAlign, Valign vAlign, int orientation ) {
//        if( text == null ) {
//            text = ".";
//            return;
//        }

        int paintIndex = getPaintIndex( color, size );

        if( !texts.containsKey( paintIndex ))
            texts.put( paintIndex, new ArrayList<TextString>() );

        TextString ts = new TextString(text, x, y, color, size, orientation );
        ts.hAlign(hAlign);
        ts.vAlign(vAlign);

        texts.get( paintIndex ).add(ts);
    }

    public void addPath( Path path, int color, boolean filled ) {
        Shape shape = new Shape( path, color, filled );
        shapes.add(shape);
    }

    public void addCircle( float x, float y, float radius, int color ) {
        circles.add( new Circle( x, y, radius, color ) );
    }

    public void addArc( float x, float y, float radius, float startAngle, float endAngle, int color ) {
        arcs.add( new Arc( x, y, radius, startAngle, endAngle, color ));
    }

    public void addRect( float left, float top, float right, float bottom, int color, boolean filled ) {
        Path path = new Path();
        path.moveTo( left, top );
        path.lineTo(right, top);
        path.lineTo(right, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, top);
        shapes.add( new Shape( path, color, filled ));
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
        for( Shape shape : shapes ) {
            canvas.drawPath( shape.path, shape.paint );
        }

        for( Circle circle : circles )
            canvas.drawCircle(circle.x, circle.y, circle.radius, circle.paint );

        for( Arc arc : arcs )
            canvas.drawArc( arc.oval, arc.angle, arc.sweep, false, arc.paint );

        for (Map.Entry<Integer, List<Float>> entry : lines.entrySet()) {
//            paint.setColor( entry.getKey() );
            canvas.drawLines( floatArray[arrayPt++], paintList.get( entry.getKey() ) );
        }

        for( Map.Entry<Integer, List<TextString>> entry : texts.entrySet() ) {
            Paint newpaint = paintList.get( entry.getKey() );
            for( TextString ts : entry.getValue() ) {
//                try {
                    if( ts.orientation == 1 ) {
                        canvas.save();
                        canvas.rotate( 90f, ts.x, ts.y );
                        canvas.drawText( ts.text, ts.x, ts.y, newpaint );
                        canvas.restore();
                    }
                    else
                        canvas.drawText( ts.text, ts.x, ts.y, newpaint );

//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }

    }

    public int getPaintIndex( int color, int width ) {
        for (int i = 0; i < paintList.size(); i++)
            if( paintList.get(i).getColor() == color && paintList.get(i).getStrokeWidth() == width )
                return i;

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(width);

        paintList.add( paint );
        return paintList.size()-1;
    }

    public int getPaintIndex( int color, float size ) {
        for (int i = 0; i < paintList.size(); i++)
            if( paintList.get(i).getColor() == color && paintList.get(i).getTextSize() == size )
                return i;

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(size);

        paintList.add( paint );
        return paintList.size()-1;
    }

    public static enum Halign { LEFT, CENTER, RIGHT };
    public static enum Valign { TOP, CENTER, BOTTOM };

    class TextString {
        float x, y, size;
        int color;
        int orientation;
        String text;
        Rect bounds = new Rect();
        Paint paint = new Paint();

        public TextString( String text, float x, float y, int color, float size, int orientation ) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
            this.orientation = orientation;
        }

        public void hAlign( Halign hAlign ) {
            paint.setTextSize( size );
            paint.getTextBounds( text, 0, text.length(), bounds );

            switch( hAlign ) {
                case CENTER:
                    x -= bounds.exactCenterX();
                    break;
                case RIGHT:
                    x -= ( bounds.width() + 1 );
            }
        }

        public void vAlign( Valign vAlign ) {
            paint.setTextSize( size );
            paint.getTextBounds( text, 0, text.length(), bounds );

            switch( vAlign ) {
                case TOP:
                    y += bounds.height();
                    break;
                case CENTER:
                    y -= bounds.exactCenterY();
            }
        }
    }

    class Shape {
        Path path;
        Paint paint;

        public Shape( Path path, int color, boolean filled ) {
            this.path = path;
            paint = new Paint();
            paint.setColor( color );
            paint.setStrokeWidth(0);
            paint.setStyle(filled ? Paint.Style.FILL : Paint.Style.STROKE);
        }
    }

    class Circle {
        float x, y, radius;
        Paint paint;

        public Circle( float x, float y, float radius, int color ) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            paint = new Paint();
            paint.setColor( color );
            paint.setStrokeWidth( 0 );
        }
    }

    class Arc {
        RectF oval;
        float angle, sweep;
        Paint paint;

        public Arc( float x, float y, float radius, float start, float end, int color ) {
            oval = new RectF( x-radius, y-radius, x+radius, y+radius );
            angle = end;
            sweep = Math.abs( end-start );
            paint = new Paint();
            paint.setColor( color );
            paint.setStyle(Paint.Style.STROKE );
            paint.setStrokeWidth( 0 );
        }
    }
}

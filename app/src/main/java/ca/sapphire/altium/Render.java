package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

import ca.sapphire.graphics.GraphicsObject;
import ca.sapphire.schview.StreamFile;

/**
 * Created by apreston on 8/4/2015.
 */
public class Render {
    public ArrayList<GraphicsObject> objects = new ArrayList<>();
    public ArrayList<Object> altiumObjects = new ArrayList<>();
    public ArrayList<Font> fonts = new ArrayList<>();

    public void draw(Canvas canvas, Paint paint) {
        for( GraphicsObject object : objects )
            object.draw( canvas, paint );
    }

//    public void addLine(Point start, Point end, int color) {
//        objects.add( new Line(start.x, start.y, end.x, end.y, color));
//    }
//
//    public void addLine( int x1, int y1, int x2, int y2, int color ) {
//        objects.add( new Line( x1, y1, x2, y2, color));
//    }

    public void addFont(int size, String name) {
        fonts.add( new Font(size, name) );
    }

//    public void addText( String text, int x, int y, int fontId, int color) {
//        objects.add( new Text( x, y, fontId, text ) );
//    }

    public void addCircle( int x, int y, int radius, int color ) {
        objects.add( new Circle( x, y, radius, color ) );
    }

    public void addPolygon( int[] x, int[] y, int color, boolean filled ) {
        objects.add( new Polygon( x, y, color, filled ));
    }

//    public void addText( int x, int y, int fontId, String name ) {
//        objects.add( new Text( x, y, fontId, name ));
//    }

//    public class Line implements GraphicsObject {
//        public int x1,y1,x2,y2,color;
//
//        public Line() {}
//
//        public Line( int x1, int y1, int x2, int y2, int color ) {
//            this.x1 = x1;
//            this.y1 = -y1;
//            this.x2 = x2;
//            this.y2 = -y2;
//            this.color = color;
//        }
//
//        public void draw( Canvas canvas, Paint paint ) {
//            paint.setColor( altiumToRGB(color) );
//            canvas.drawLine(x1, y1, x2, y2, paint);
//        }
//    }

    public class Circle implements GraphicsObject {
        public int x,y,radius,color;

        public Circle( int x, int y, int radius, int color ) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setColor( altiumToRGB(color) );
            canvas.drawCircle( x, y, radius, paint );
        }
    }

    public class Polygon implements GraphicsObject{
        public Path path = new Path();
        public int color;
        public boolean filled;

        public Polygon( int[] x, int[] y, int color, boolean filled )
        {
            this.color = color;
            this.filled = filled;
            path.moveTo(x[0], -y[0]);
            for (int i = 1; i < x.length; i++) {
                path.lineTo( x[i], -y[i] );
            }
            path.lineTo(x[0], -y[0]);
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setColor( altiumToRGB(color));
            if( filled ) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, paint);
            }
            else {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, paint);
            }
        }
    }

    public class Font implements GraphicsObject {
        public int size;

        public Font( int size, String name ) {
//            Typeface.create("sans-serif-light", Typeface.NORMAL);
            this.size = (int) (size * 1.25);
        }

        public void draw( Canvas canvas, Paint paint ) {}
    }

    public class Text implements GraphicsObject {
        int x, y, fontId;
        String name;

        public Text( int x, int y, int fontID, String name ) {
            this.x = x;
            this.y = -y;
            this.fontId = fontID;
            this.name = name;
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setTextSize( fonts.get( fontId ).size );
            canvas.drawText( name, x, y, paint );
        }
    }

    public int altiumToRGB( int altColor )
    {
        int red = ( altColor & 0xff ) << 16;
        int grn = altColor & 0xff00;
        int blu = ( altColor & 0xff0000 ) >> 16;
        return 0xff000000 | red | grn | blu;
    }
}
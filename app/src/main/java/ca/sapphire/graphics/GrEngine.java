package ca.sapphire.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

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
//    public Map<Integer, List<>>

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
            addText( text, x, y, color, size, Halign.LEFT, Valign.BOTTOM );
    }

    public void addText( String text, float x, float y, int color, float size, Halign hAlign, Valign vAlign ) {
        int paintIndex = getPaintIndex( color, size );

        if( !texts.containsKey( paintIndex ))
            texts.put( paintIndex, new ArrayList<TextString>() );

        TextString ts = new TextString(text, x, y, color, size );
        ts.hAlign( hAlign );
        ts.vAlign( vAlign );

        texts.get( paintIndex ).add(ts);
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
        for (Map.Entry<Integer, List<Float>> entry : lines.entrySet()) {
//            paint.setColor( entry.getKey() );
            canvas.drawLines( floatArray[arrayPt++], paintList.get( entry.getKey() ) );
        }

        for( Map.Entry<Integer, List<TextString>> entry : texts.entrySet() ) {
            Paint newpaint = paintList.get( entry.getKey() );
            for( TextString ts : entry.getValue() ) {
                canvas.drawText( ts.text, ts.x, ts.y, newpaint );
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
        String text;

        public TextString( String text, float x, float y, int color, float size ) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
        }

        public void hAlign( Halign hAlign ) {

        }

        public void vAlign( Valign vAlign ) {

        }
    }
}

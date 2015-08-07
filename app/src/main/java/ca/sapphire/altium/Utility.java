package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.Map;

import ca.sapphire.graphics.GraphicsObject;

/**
 * Utilities for extracting data from Altium files
 */
public abstract class Utility {
    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @return : Boolean value of parameter
     */
    static public boolean getBooleanValue( Map<String, String> record, String name  ) {
        return ( record.get( name ).equals( "T" ));
    }


    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @param defValue ; Default value to return if parameter does not exist in the record
     * @return : Boolean value of parameter
     */
    static public boolean getBooleanValue( Map<String, String> record, String name, boolean defValue  ) {
        if( record.get( name ) == null )
            return defValue;
        return ( record.get( name ).equals( "T" ));
    }


    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @return : Byte value of parameter
     */
    static public byte getByteValue( Map<String, String> record, String name  ) {
        return Byte.parseByte(record.get(name));
    }

    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @param defValue ; Default value to return if parameter does not exist in the record
     * @return : Byte value of parameter
     */
    static public byte getByteValue( Map<String, String> record, String name, byte defValue  ) {
        if( record.get( name ) == null )
            return defValue;
        return Byte.parseByte(record.get(name));
    }

    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @return : Short value of parameter
     */
    static public short getShortValue( Map<String, String> record, String name  ) {
        return Short.parseShort(record.get(name));
    }

    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @param defValue ; Default value to return if parameter does not exist in the record
     * @return : Short value of parameter
     */
    static public short getShortValue( Map<String, String> record, String name, short defValue  ) {
        if( record.get( name ) == null )
            return defValue;
        return Short.parseShort(record.get(name));
    }

    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @return : Integer value of parameter
     */
    static public int getIntValue( Map<String, String> record, String name  ) {
        return Integer.parseInt(record.get(name));
    }

    /**
     *
     * @param record : Data record
     * @param name : Name of parameter to extract
     * @param defValue ; Default value to return if parameter does not exist in the record
     * @return : Integer value of parameter
     */
    static public int getIntValue( Map<String, String> record, String name, int defValue  ) {
        if( record.get( name ) == null )
            return defValue;
        return Integer.parseInt(record.get(name));
    }

    static public int getColor( Map<String, String> record ) {
        return altiumToRGB(Integer.parseInt(record.get("COLOR")));
    }



    /**
     * Rotate a point around the origin
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( Point src, int rotation ) {
        switch( rotation ) {
            case 1:
                int tmp = src.x;
                src.x = -src.y;
                src.y = tmp;
                return;

            case 2:
                src.x = -src.x;
                src.y = -src.y;
                return;

            case 3:
                tmp = src.x;
                src.x = src.y;
                src.y = -tmp;
                return;
        }
    }

    /**
     * Rotate a point around the origin
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( PointF src, int rotation ) {
        switch( rotation ) {
            case 1:
                float tmp = src.x;
                src.x = -src.y;
                src.y = tmp;
                return;

            case 2:
                src.x = -src.x;
                src.y = -src.y;
                return;

            case 3:
                tmp = src.x;
                src.x = src.y;
                src.y = -tmp;
                return;
        }
    }

    /**
     * Rotate a point around a specified point
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( Point src, Point origin, int rotation ) {
        int dx = src.x - origin.x;
        int dy = src.y - origin.y;

        switch( rotation ) {
            case 1:
                src.x = origin.x - dy;
                src.y = origin.y + dx;
                return;

            case 2:
                src.x = origin.x - dx;
                src.y = origin.y - dy;
                return;

            case 3:
                src.x = origin.x + dy;
                src.y = origin.y - dx;
                return;
        }
    }

    /**
     * Rotate a point around a specified point
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( PointF src, PointF origin, int rotation ) {
        float dx = src.x - origin.x;
        float dy = src.y - origin.y;

        switch( rotation ) {
            case 1:
                src.x = origin.x - dy;
                src.y = origin.y + dx;
                return;

            case 2:
                src.x = origin.x - dx;
                src.y = origin.y - dy;
                return;

            case 3:
                src.x = origin.x + dy;
                src.y = origin.y - dx;
                return;
        }
    }
    static public Point[] addMultiLine( Map<String, String> record) {
        int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
        Point[] point = new Point[size];

        for (int i = 0; i < size; i++) {
            point[i] = new Point();
            point[i].x = Integer.parseInt((String) record.get("X" + String.valueOf(i + 1)));
            point[i].y = Integer.parseInt((String) record.get("Y" + String.valueOf(i + 1)));
        }

        return point;
    }

    static public int altiumToRGB( int altColor )
    {
        int red = ( altColor & 0xff ) << 16;
        int grn = altColor & 0xff00;
        int blu = ( altColor & 0xff0000 ) >> 16;
        return 0xff000000 | red | grn | blu;
    }

    static public Path polygon( Point[] point ) {
        Path path = new Path();
        path.moveTo( point[0].x, -point[0].y );
        for (int i = 1; i < point.length; i++) {
            path.lineTo( point[i].x, -point[i].y );
        }
        path.lineTo( point[0].x, -point[0].y );
        return path;
    }



    public class Polygon implements GraphicsObject {
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



}


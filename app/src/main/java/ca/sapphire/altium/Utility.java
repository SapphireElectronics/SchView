package ca.sapphire.altium;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import java.util.Map;

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

    static public float getFloatValue( Map<String, String> record, String name, float defValue  ) {
        if( record.get( name ) == null )
            return defValue;
        return Float.parseFloat(record.get(name));
    }


    static public int getColor( Map<String, String> record ) {
        String color = record.get( "COLOR" );
        if( color == null )
            return Color.BLACK;

        return altiumToRGB(Integer.parseInt(color));
    }



    /**
     * Rotate a point around the origin in Java space
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( Point src, int rotation ) {
        switch( rotation ) {
            case 1:
                int tmp = src.x;
                src.x = src.y;
                src.y = -tmp;
                return;

            case 2:
                src.x = -src.x;
                src.y = -src.y;
                return;

            case 3:
                tmp = src.x;
                src.x = -src.y;
                src.y = tmp;
                return;
        }
    }

    /**
     * Rotate a point around the origin in Java space
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( PointF src, int rotation ) {
        switch( rotation ) {
            case 1:
                float tmp = src.x;
                src.x = src.y;
                src.y = -tmp;
                return;

            case 2:
                src.x = -src.x;
                src.y = -src.y;
                return;

            case 3:
                tmp = src.x;
                src.x = -src.y;
                src.y = tmp;
                return;
        }
    }

    /**
     * Rotate a point around a specified point in Java space
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( Point src, Point origin, int rotation ) {
        int dx = src.x - origin.x;
        int dy = src.y - origin.y;

        switch( rotation ) {
            case 1:
                src.x = origin.x + dy;
                src.y = origin.y - dx;
                return;

            case 2:
                src.x = origin.x - dx;
                src.y = origin.y - dy;
                return;

            case 3:
                src.x = origin.x - dy;
                src.y = origin.y + dx;
                return;
        }
    }

    /**
     * Rotate a point around a specified point in Java space
     *
     * @param src ; Source point
     * @param rotation ; Rotation amount, 0=0', 1=90', 2=180', 3=270'
     */
    static public void rotate( PointF src, PointF origin, int rotation ) {
        float dx = src.x - origin.x;
        float dy = src.y - origin.y;

        switch( rotation ) {
            case 1:
                src.x = origin.x + dy;
                src.y = origin.y - dx;
                return;

            case 2:
                src.x = origin.x - dx;
                src.y = origin.y - dy;
                return;

            case 3:
                src.x = origin.x - dy;
                src.y = origin.y + dx;
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

    static public PointF addLocation( Map<String, String> record) {
        PointF point = new PointF();

        point.x = Float.parseFloat( ( record.containsKey("LOCATION.X") ? record.get( "LOCATION.X") : "0" ) + "." +  ( record.containsKey("LOCATION.X_FRAC") ? record.get( "LOCATION.X_FRAC") : "0" ) );
        point.y = Float.parseFloat( ( record.containsKey("LOCATION.Y") ? record.get( "LOCATION.Y") : "0" ) + "." +  ( record.containsKey("LOCATION.Y_FRAC") ? record.get( "LOCATION.Y_FRAC") : "0" ) );

        return point;
    }

    static public void xyToJava( Point[] points ) {
        for (Point point : points ) {
            point.y = -point.y;
        }
    }

    static public void xyToJava( PointF[] points ) {
        for (PointF point : points ) {
            point.y = -point.y;
        }
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
        path.moveTo( point[0].x, point[0].y );
        for (int i = 1; i < point.length; i++) {
            path.lineTo( point[i].x, point[i].y );
        }
        path.lineTo( point[0].x, point[0].y );
        return path;
    }
}


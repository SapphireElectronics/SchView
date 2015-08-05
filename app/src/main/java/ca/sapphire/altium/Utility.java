package ca.sapphire.altium;

import android.graphics.Point;

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
}

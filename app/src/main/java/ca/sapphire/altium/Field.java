package ca.sapphire.altium;

/**
 * Created by apreston on 8/20/2015.
 */

public enum Field {
    HEADER( Field.STRING ),
    RECORD( Field.INTEGER ),
    OWNERINDEX( Field.INTEGER),
    COLOR( Field.INTEGER),
    LOCATION_X( Field.FLOAT ),
    LOCATION_X_FRAC( Field.FRACTION ),
    LOCATION_Y( Field.FLOAT ),
    LOCATION_Y_FRAC( Field.FRACTION ),
    CORNER_X( Field.FLOAT ),
    CORNER_X_FRAC( Field.FRACTION ),
    CORNER_Y( Field.FLOAT ),
    CORNER_Y_FRAC( Field.FRACTION ),
    LINEWIDTH( Field.INTEGER ),
    OWNERPARTID( Field.INTEGER ),
    FORMALTYPE( Field.UNKNOWN ),
    ELECTRICAL( Field.UNKNOWN ),
    PINCONGLOMERATE( Field.INTEGER ),
    PINLENGTH( Field.INTEGER ),
    NAME( Field.STRING ),
    DESIGNATOR( Field.STRING ),
    SWAPIDPIN( Field.UNKNOWN ),
    SWAPIDPART( Field.UNKNOWN ),
    PINNAME_POSITIONCONGLOMERATE( Field.INTEGER ),
    NAME_CUSTOMPOSITION_MARGIN( Field.INTEGER ),
    PINDESIGNATOR_POSITIONCONGLOMERATE( Field.INTEGER ),
    DESIGNATOR_CUSTOMPOSITION_MARGIN( Field.INTEGER ),
    WEIGHT( Field.UNKNOWN ),
    FONTIDCOUNT(Field.INTEGER),
    USEMBCS(Field.UNKNOWN),
    ISBOC(Field.UNKNOWN),
    HOTSPOTGRIDON(Field.UNKNOWN),
    HOTSPOTGRIDSIZE(Field.UNKNOWN),
    SHEETSTYLE(Field.INTEGER),
    SYSTEMFONT(Field.UNKNOWN),
    BORDERON(Field.UNKNOWN),
    TITLEBLOCKON(Field.UNKNOWN),
    SHEETNUMBERSPACESIZE(Field.UNKNOWN),
    AREACOLOR(Field.INTEGER),
    SNAPGRIDON(Field.UNKNOWN),
    SNAPGRIDSIZE(Field.UNKNOWN),
    VISIBLEGRIDON(Field.UNKNOWN),
    VISIBLEGRIDSIZE(Field.UNKNOWN),
    CUSTOMX(Field.UNKNOWN),
    CUSTOMY(Field.UNKNOWN),
    CUSTOMXZONES(Field.INTEGER),
    CUSTOMYZONES(Field.INTEGER),
    CUSTOMMARGINWIDTH(Field.UNKNOWN),
    DISPLAY_UNIT(Field.UNKNOWN),
    FONTID(Field.INTEGER),
    ISHIDDEN(Field.BOOLEAN),
    TEXT(Field.STRING),
    READONLYSTATE(Field.UNKNOWN),
    UNIQUEID(Field.STRING),
    INDEXINSHEET(Field.INTEGER),
    LIBREFERENCE(Field.UNKNOWN),
    COMPONENTDESCRIPTION(Field.UNKNOWN),
    PARTCOUNT(Field.INTEGER),
    DISPLAYMODECOUNT(Field.UNKNOWN),
    CURRENTPARTID(Field.UNKNOWN),
    LIBRARYPATH(Field.UNKNOWN),
    SOURCELIBRARYNAME(Field.UNKNOWN),
    TARGETFILENAME(Field.UNKNOWN),
    PARTIDLOCKED(Field.UNKNOWN),
    NOTUSEDBTABLENAME(Field.UNKNOWN),
    DESIGNITEMID(Field.UNKNOWN),
    ISNOTACCESIBLE(Field.BOOLEAN),
    JUSTIFICATION(Field.UNKNOWN),
    ISMIRRORED(Field.BOOLEAN),
    OVERRIDENOTAUTOPOSITION(Field.UNKNOWN),
    NOTAUTOPOSITION(Field.UNKNOWN),
    DESCRIPTION(Field.STRING),
    USECOMPONENTLIBRARY(Field.UNKNOWN),
    MODELNAME(Field.STRING),
    MODELTYPE(Field.UNKNOWN),
    DATAFILECOUNT(Field.UNKNOWN),
    MODELDATAFILEENTITY0(Field.UNKNOWN),
    MODELDATAFILEKIND0(Field.UNKNOWN),
    ISCURRENT(Field.BOOLEAN),
    DATALINKSLOCKED(Field.UNKNOWN),
    DATABASEDATALINKSLOCKED(Field.UNKNOWN),
    INTEGRATEDMODEL(Field.UNKNOWN),
    DATABASEMODEL(Field.UNKNOWN),
    STYLE(Field.UNKNOWN),
    SHOWNETNAME(Field.BOOLEAN),
    ORIENTATION(Field.INTEGER),
    LOCATIONCOUNT(Field.COUNT),
    X(Field.XVAL),
    X_FRAC(Field.XVAL),
    Y(Field.YVAL),
    Y_FRAC(Field.YVAL),
    ISSOLID(Field.BOOLEAN),
    RADIUS(Field.UNKNOWN),
    ENDANGLE(Field.UNKNOWN),
    VAULTGUID(Field.STRING),
    ITEMGUID(Field.STRING),
    REVISIONGUID(Field.STRING),
    SYMBOLVAULTGUID(Field.STRING),
    SYMBOLITEMGUID(Field.STRING),
    SYMBOLREVISIONGUID(Field.STRING),
    OWNERPARTDISPLAYMODE(Field.UNKNOWN),
    STARTANGLE(Field.UNKNOWN),
    MODELVAULTGUID(Field.STRING),
    MODELITEMGUID(Field.STRING),
    MODELREVISIONGUID(Field.STRING),
    TRANSPARENT(Field.BOOLEAN),


    ZZZ( Field.INTEGER );

    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int BOOLEAN = 2;
    public static final int STRING = 3;
    public static final int FRACTION = 4;
    public static final int COUNT = 5;
    public static final int XVAL = 6;
    public static final int YVAL = 7;
    public static final int UNKNOWN = 8;



    private static final int fieldSize = ZZZ.ordinal();

    public static int[] integers = new int[fieldSize];
    public static float[] floats = new float[fieldSize];
    public static boolean[] booleans = new boolean[fieldSize];
    public static String[] strings = new String[fieldSize];

    public static float[] x, y;
    private static boolean counting = false;

    private static boolean[] used = new boolean[fieldSize];

    public final int type;


    Field( int type ) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static void clear() {
        for( boolean use : used  )
            use = false;
        counting = false;
    }

    public static void put( String[] data ) {
        if( counting ) {
            if (data[0].matches("X\\d+$")) {
                int num = Integer.parseInt(data[0].substring(1)) - 1;
                x[num] = Float.parseFloat(data[1]);
                return;
            }
            if (data[0].matches("Y\\d+$")) {
                int num = Integer.parseInt(data[0].substring(1)) - 1;
                y[num] = Float.parseFloat(data[1]);
                return;
            }
            if (data[0].matches("X\\d_FRAC")) {
                int num = Integer.parseInt(data[0].substring(1)) - 1;
                x[num] += Float.parseFloat("0." + data[1]);
                return;
            }
            if (data[0].matches("Y\\d_FRAC")) {
                int num = Integer.parseInt(data[0].substring(1)) - 1;
                y[num] += Float.parseFloat("0." + data[1]);
                return;
            }
        }

        valueOf(data[0]).put( data[1] );
    }

    public void put( String data ) {
        used[ this.ordinal() ] = true;
        switch ( this.getType() ) {
            case INTEGER:
                integers[ this.ordinal() ] = Integer.parseInt( data ) ;
                break;

            case FLOAT:
                floats[ this.ordinal() ] = Float.parseFloat( data ) ;
                break;

            case BOOLEAN:
                booleans[ this.ordinal() ] = data.equals("T");
                break;

            case STRING:
                strings[ this.ordinal() ] = data;
                break;

            case FRACTION:
                floats[ this.ordinal() ] = Float.parseFloat( "0." + data );
                break;

            case COUNT:
                int size = Integer.parseInt( data );
                x = new float[size];
                y = new float[size];
                counting = true;
                break;
        }
    }

    public static int getInt( int index ) {
        return used[index] ? integers[index] : 0;
    }

    public static int getInt( int index, int defaultValue ) {
        return used[index] ? integers[index] : defaultValue;
    }

    public static float getFloat( int index ) {
        return ( used[index] ? floats[index] : 0f ) + ( used[index+1] ? floats[index+1] : 0f );
    }

    public static float getFloat( int index, float defaultValue ) {
        return used[index] ? ( floats[index] + ( used[index+1] ? floats[index+1] : 0f ) ) : defaultValue;
    }

}

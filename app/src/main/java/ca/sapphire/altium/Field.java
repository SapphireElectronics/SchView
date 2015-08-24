package ca.sapphire.altium;

/**
 * Created by apreston on 8/20/2015.
 */


enum Type {
    SHORT, INT, STRING;
}

public enum Field {
    RECORD( "RECORD", Type.SHORT, 2 ),
    OWNERINDEX( "OWNERINDEX", Type.SHORT, 2 ),
    COLOR( "COLOR", Type.INT, 1 ),
    LOCATION_X( "LOCATION.X", Type.INT, 1 ),
    LOCATION_Y( "LOCATION.Y", Type.INT, 1 ),
    CORNER_X( "CORNER.X", Type.INT, 1 ),
    CORNER_Y( "CORNER.Y", Type.INT, 1 ),
    LINEWIDTH( "LINEWIDTH", Type.INT, 1 );

//    OWNERPARTID( "OWNERPARTID" ),
//    FORMALTYPE( "FORMALTYPE" ),
//    ELECTRICAL( "ELECTRICAL" ),
//    PINCONGLOMERATE( "PINCONGLOMERATE" ),
//    PINLENGTH( "PINLENGTH" ),
//    LOCATION_X( "LOCATION.X" ),
//    LOCATION_Y( "LOCATION.Y" ),
//    NAME( "NAME"),
//    DESIGNATOR( "DESIGNATOR"),
//    SWAPIDPIN( "SWAPIDPIN"),
//    SWAPIDPART( "SWAPIDPART"),
//    PINNAME_POSITIONCONGLOMERATE( "PINNAME_POSITIONCONGLOMERATE" ),
//    NAME_CUSTOMPOSITION_MARGIN( "NAME_CUSTOMPOSITION_MARGIN" ),
//    PINDESIGNATOR_POSITIONCONGLOMERATE( "PINDESIGNATOR_POSITIONCONGLOMERATE" ),
//    DESIGNATOR_CUSTOMPOSITION_MARGIN( "DESIGNATOR_CUSTOMPOSITION_MARGIN" );



//    public final int number;
    public final String name;
    public final Type type;
    public final int primitive;


    Field( String name, Type type, int primitive ) {
        this.name = name;
        this.type = type;
        this.primitive = primitive;
    }
//    Field( int number, String name ) {
//        this.number = number;
//        this.name = name;
//    }

//    public int getNumber() {
//        return number;
//    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getPrimitive() {
        return primitive;
    }

}

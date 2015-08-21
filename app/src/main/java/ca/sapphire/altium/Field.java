package ca.sapphire.altium;

/**
 * Created by apreston on 8/20/2015.
 */

enum Type {
    SHORT, INT, STRING;
}

public enum Field {
    OWNERINDEX( "OWNERINDEX", Type.SHORT ),
    COLOR( "COLOR", Type.INT ),
    LOCATION_X( "LOCATION.X", Type.INT ),
    LOCATION_Y( "LOCATION.Y", Type.INT ),
    CORNER_X( "CORNER.X", Type.INT ),
    CORNER_Y( "CORNER.Y", Type.INT );

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


    Field( String name, Type type ) {
        this.name = name;
        this.type = type;
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
}

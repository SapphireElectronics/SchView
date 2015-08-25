package ca.sapphire.altium;

import android.util.SparseArray;

/**
 * Created by apreston on 8/20/2015.
 */



public enum Field {
    RECORD( Type.SHORT ),
    OWNERINDEX( Type.SHORT ),
    COLOR( Type.INT ),
    LOCATION_X( Type.INT ),
    LOCATION_Y( Type.INT ),
    CORNER_X( Type.INT ),
    CORNER_Y( Type.INT ),
    LINEWIDTH( Type.INT );



//    RECORD( "RECORD", Type.SHORT ),
//    OWNERINDEX( "OWNERINDEX", Type.SHORT ),
//    COLOR( "COLOR", Type.INT ),
//    LOCATION_X( "LOCATION.X", Type.INT ),
//    LOCATION_Y( "LOCATION.Y", Type.INT ),
//    CORNER_X( "CORNER.X", Type.INT ),
//    CORNER_Y( "CORNER.Y", Type.INT ),
//    LINEWIDTH( "LINEWIDTH", Type.INT );

    public enum Type {
        SHORT, INT, STRING;
    }


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
//    public final String name;
    public final Type type;
//    public final int primitive;


    Field( Type type ) {
        this.type = type;
    }

//    Field( String name, Type type ) {
//        this.name = name;
//        this.type = type;
//    }

//    public String getName() {
//        return name;
//    }

    public Type getType() {
        return type;
    }
}

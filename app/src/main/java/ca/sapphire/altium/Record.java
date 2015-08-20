package ca.sapphire.altium;

/**
 * Created by apreston on 8/20/2015.
 */
public enum Record {
    COMPONENT( 1 ),
    PIN( 2 ),
    TEXT( 4 ),
    COMPONENT_MULTILINE( 6 ),
    COMPONENT_POLYGON( 7 ),
    COMPONENT_ARC( 12 ),
    COMPONENT_LINES( 13 ),
    COMPONENT_AREA( 14 ),
    POWER_PORT( 17 ),
    NET_NAME( 25 ),
    BUS( 26 ),
    WIRE( 27 ),
    JUNCTION( 29 ),
    DESIGNATOR( 34 ),
    BUS_ENTRY( 37 ),
    ATTRIBUTE( 41 );

    private final int number;

    Record( int number ) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}


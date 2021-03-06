package ca.sapphire.schview;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sapphire.altium.Arc;
import ca.sapphire.altium.Attribute;
import ca.sapphire.altium.Bus;
import ca.sapphire.altium.CompBox;
import ca.sapphire.altium.CompLine;
import ca.sapphire.altium.CompMultiLine;
import ca.sapphire.altium.CompPoly;
import ca.sapphire.altium.Component;
import ca.sapphire.altium.Designator;
import ca.sapphire.altium.Entry;
import ca.sapphire.altium.Field;
import ca.sapphire.altium.Junction;
import ca.sapphire.altium.Options;
import ca.sapphire.altium.Pin;
import ca.sapphire.altium.Port;
import ca.sapphire.altium.PowerPort;
import ca.sapphire.altium.Text;
import ca.sapphire.altium.Wire;
import ca.sapphire.graphics.GrEngine;

/**
 * Reads the schematic stream from an Altium file, stores it into Altium objects.
 */

/**
 * Record Types
 *
 * Num	Imp	Name
 * 1  	N	Component ?? (loc x,y, color, areacolor
 * 2  	Y	Component Pin
 * 3    N   ???
 * 4    N   Text (loc x/y, text, fontid, color
 * 5    N   ???
 * 6  	Y	Component lines
 * 7    Y   Component graphic - fill ( loccount, color, areacolor
 * 8    N   ???
 * 9    N   ???
 * 10   N   ???
 * 11   N   ???
 * 12   N   Component graphic - circle (loc.x, loc.y, radius, color, endangle)
 * 13   Y   Component graphic - line (loc.x, loc.y, corn.x, corn.y, color)
 * 14 	Y   Component box (loc x/y, cornerx/y, color, areacolor, transpartent t/f, issolid t/f (only draw if last two parts exist
 * 17   Y   Power Port: Implemented as an object
 * 25	N   Net label
 * 26	Y	Bus - multi segment line: LOCATIONCOUNT segments, X1,Y1 , X2,Y2 etc.
 * 27 	Y	Wire
 * 29 	Y	Junction
 * 31   Y   Options: Implemented as an object
 * 34	N	Component Designator - loc x/y, name, text, justification, fontid,
 * 37	Y	Bus entry ("LOCATION.X/Y" is the Bus end of the entry, CORNER.X/Y is the Wire end
 * 41 	N	Component Attribute (text) - loc.x/y, name, text, ishidden:t/f, color, fontid
 * 44 	N	Component ?? (single field - may be to provide an ownerindex for a component so they can be enumerated - not sure)
 * 45 	N	Component description?
 * 46	N	?? (single field)
 * 48	N	?? (single field)
 */

public class StreamFile {
    public final static String TAG = "StreamFile";
    BufferedInputStream bis;
    StreamedFile sf;
    public int recordNumber = 0;

//    public ArrayList<ca.sapphire.altium.Object> objects = new ArrayList<>();
    public ArrayList<ca.sapphire.altium.SchObject> newObjects = new ArrayList<>();
    boolean multiPartComponent = false;
    public GrEngine grEngine = new GrEngine();

    private boolean eof = false;

    public List<String> flds = new ArrayList<>();
    public List<String> subFlds = new ArrayList<>();

    public List<String> missingFields = new ArrayList<>();

    byte[] buffer = new byte[1024];
    Map<String, String> result = new HashMap<>();


    public StreamFile(String fileName) {
        try {
            bis = new BufferedInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            while (sf.available() >= 4 && !eof) {
                readRecord();
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();

        }

        Log.i(TAG, "Records read: " + recordNumber);
    }

    public StreamFile( StreamedFile sf, String fileName ) {
        long startTime = System.currentTimeMillis();

        this.sf = sf;
        try {
            while (sf.available() >= 4 && !eof) {
                readRecord();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        Options.putProjectOption( "DocumentFullPathAndName", fileName );
        Options.putProjectOption( "CurrentDate", dateFormat.format(c.getTime()) );
        Options.putProjectOption( "CurrentTime", timeFormat.format(c.getTime()) );

        Log.i( TAG, "Process time: " + (System.currentTimeMillis() - startTime ) );

        Log.i( TAG, "Missing fields:" + missingFields );
        Log.i(TAG, "Records read: " + recordNumber);
    }

    public void readRecord() throws IOException {
        String line = readLine();

        if (line == null) return;

//        parseRecord(line);

        result.clear();
        String pairs[] = line.split("\\|");

        for (String pair : pairs) {
            String[] data = pair.split("=");
            if (data.length == 2) {
                result.put(data[0], data[1]);
            }
        }

        String record = result.get("RECORD");

// todo: Check to see when multipartcomponent is set to false
// todo: Handle multipartcomponent a little nicer.


        if (record != null) {
            switch (Integer.parseInt(record)) {
                case 1:
                    Component component = new Component( result );
                    newObjects.add( component );
                    if( component.displayModeCount > 1)
                        multiPartComponent = true;
                    break;
                case 2:
                    newObjects.add( new Pin( result, multiPartComponent ));
                    break;
                case 4:
                    newObjects.add( new Text( result, pairs ));
                    break;
                case 6:
                    newObjects.add( new CompMultiLine( result ));
                    break;
                case 7:
                    newObjects.add( new CompPoly( result ));
                    break;
                case 12:
                    newObjects.add( new Arc( result, multiPartComponent ));
                    break;
                case 13:
                    newObjects.add( new CompLine( result, multiPartComponent ));
                    break;
                case 14:
                    newObjects.add( new CompBox( result ));
                    break;
                case 17:
                    newObjects.add( new PowerPort( result ) );
                    break;
                case 18:
                    newObjects.add( new Port( result ) );
                    break;
                case 25:
                    newObjects.add( new Designator( result ));
                    break;
                case 26:
                    newObjects.add( new Bus( result ));
                    break;
                case 27:
//                    newObjects.add( new Wire() );
                    newObjects.add( new Wire( result ));
                    break;
                case 29:
                    newObjects.add( new Junction( result ));
                    break;
                case 31:
                    Options.INSTANCE.put(result);
                    break;
                case 34:
                    newObjects.add(new Designator(result));
                    break;
                case 37:
//                    newObjects.add( new Entry());
                    newObjects.add( new Entry( result ));
                    break;
                case 41:
                    newObjects.add( new Attribute( result ));
                    break;
            }
        }
        recordNumber++;
    }

    public String readLine() throws IOException {
        int length;
        try {
            length = sf.readInt();
        } catch (BufferUnderflowException e) {
            eof = true;
            return null;
        }
        if (length < 1) {
            eof = true;
            return null;
        }

        sf.readBytes( buffer, length );
        if (buffer[0] == 0) return null;

        return new String(buffer, 0, length).trim();
    }

    public void parseRecord( String line ) {

/**
 * Pros and Cons for "pre parse"
 *
 * Cons:
 * - large enum for all field names (about 170-180 entries)
 * - two steps:
 *      - convert data to sparse arrays
 *      - convert sparse array data to specific values
 *
 * Pros:
 * - can process data on a field by field basis, no need to search for an optional
 *      parameter that may not exist
 * - all data "quirks" are accounted for in one place
 */
        Field.clear();

        String pairs[] = line.split("\\|");

        for (String pair : pairs) {
            if (pair.trim().isEmpty()) continue;

            String[] data = pair.split("=");
            if (data.length == 2) {
                data[0] = data[0].trim().replaceAll("%UTF8%", "" ).replace(".", "_");

                // look for field ended in a number
//                if( data[0].matches(".*\\d+$")) {
//                    String fld[] = data[0].split("\\d+$");
//                    String num = data[0].substring( fld[0].length() );
//                    int fn = Integer.parseInt( num );
//                }

//                if( !flds.contains( data[0]))
//                    flds.add( data[0] );

//                if( !subFlds.contains( sub[0]))
//                    subFlds.add( sub[0] );

//                if( sub[0].contains( "\\d+$")) {
//
//                }

//                Field field;
                try {
                    Field.put( data );

                    if( Field.integers[Field.RECORD.ordinal()] == 31 ) {
                        Options.INSTANCE.put( pairs );
                        return;
                    }

                } catch (IllegalArgumentException ex) {
                    if( ! missingFields.contains(data[0] ) ) {
                        missingFields.add( data[0] );
                    }
                }


//                result.put(data[0], data[1]);
            }
        }
    }
}




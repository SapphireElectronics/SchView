package ca.sapphire.schview;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sapphire.altium.Bus;
import ca.sapphire.altium.CompBox;
import ca.sapphire.altium.CompLine;
import ca.sapphire.altium.CompMultiLine;
import ca.sapphire.altium.CompPoly;
import ca.sapphire.altium.Component;
import ca.sapphire.altium.Designator;
import ca.sapphire.altium.Entry;
import ca.sapphire.altium.Junction;
import ca.sapphire.altium.Options;
import ca.sapphire.altium.Pin;
import ca.sapphire.altium.PowerPort;
import ca.sapphire.altium.Render;
import ca.sapphire.altium.Text;
import ca.sapphire.altium.Wire;

/**
 * Created by apreston on 7/30/2015.
 */

/**
 * Record Types
 *
 * Num	Imp	Name
 * 1  	N	Component ?? (loc x,y, color, areacolor
 * 2  	Y	Component Pin, TODO add pin types
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
 * 26	Y	Bus - multi segment line: LOCATIONCOUNT segments, X1,Y1 , X2,Y2 etc.  TODO bus width
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
    public List<Map<String, String>> records = new ArrayList<Map<String, String>>();
    public int recordNumber = 0;
//    public int fpr = 0;

    public Render renderer = new Render();
    public Options options;

    public ArrayList<ca.sapphire.altium.Object> objects = new ArrayList<>();
    boolean multiPartComponent = false;


    public StreamFile(String fileName) {
        try {
            bis = new BufferedInputStream(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            load();
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();

        }

        Log.i(TAG, "Records read: " + recordNumber);
//        Log.i(TAG, "File pointer: " + fpr );

//        // render all from Altium Objects to Graphics Objects
//        for( ca.sapphire.altium.Object object : objects ) {
//            object.render( renderer );
//        }
    }

    public boolean load() throws IOException {
        records.clear();
        while (bis.available() >= 4) {
            Map<String, String> record = readRecord();
            if (record != null && !record.isEmpty()) {
                records.add(record);
            }
        }

//        signature = (String) records.remove(0).get("HEADER");

        return !records.isEmpty();
    }

    public Map<String, String> readRecord() throws IOException {
        String line = readLine();

        if (line == null) return null;

        Map<String, String> result = new HashMap<String, String>();

        String pairs[] = line.split("\\|");

        for (String pair : pairs) {
            if (pair.trim().isEmpty()) continue;

            String[] data = pair.split("=");
            if (data.length == 2) {
                result.put(data[0], data[1]);
//                Log.i( TAG, "Data: " + data[0]);
            }
        }

        String record = result.get("RECORD");

        if (record != null) {

//            if( result.get("OWNERINDEX") != null )
//                if( result.get("OWNERINDEX").equals("81"))

            if (record.equals("4")||record.equals("9")||record.equals("10")||record.equals("11"))
                    Log.i("Record", result.toString());

            switch (Integer.parseInt(record)) {
                case 1:
                    Component component = new Component( result );
                    objects.add( component );
                    if( component.displayModeCount > 1)
                        multiPartComponent = true;
                    break;
                case 2:
                    objects.add( new Pin( result, multiPartComponent ));

                    break;
                case 4:
                    objects.add( new Text( result ));
                    break;
                case 6:
                    objects.add( new CompMultiLine( result ));
                    break;
                case 7:
                    objects.add( new CompPoly( result ));
                    break;
                case 13:
                    objects.add( new CompLine( result, multiPartComponent ));
                    break;
                case 14:
                    objects.add( new CompBox( result ));
                    break;
                case 17:
                    objects.add( new PowerPort( result ) );
                    break;
                case 25:    // TODO: make new class
                    objects.add( new Designator( result ));
                    break;
                case 26:
                    objects.add( new Bus( result ));
                    break;
                case 27:
                    objects.add( new Wire( result ));
                    break;
                case 29:
                    objects.add( new Junction( result ));
                    break;
                case 31:
                    options.INSTANCE.put( result );
                    break;
                case 34:
                    objects.add( new Designator( result ));
                    break;
                case 37:
                    objects.add( new Entry( result ));
                    break;
            }
        }

        line = null;
        recordNumber++;
        return result;
    }


    public String readLine() throws IOException {
        int length = readInt();
//        fpr += 4;
        if (length < 1) return null;

        byte[] buffer = new byte[length];

        if (bis.read(buffer, 0, length) != length) {
            Log.i(TAG, "Didn't read enough bytes");
        }
//        fpr += length;

        if (buffer[0] == 0) return null;

        return new String(buffer).split("\u0000")[0];
    }

    public int readInt() throws IOException {
        return (((int) (bis.read() & 0xff)) |
                ((int) (bis.read() & 0xff) << 8) |
                ((int) (bis.read() & 0xff) << 16) |
                ((int) (bis.read() & 0xff) << 24));
    }
}




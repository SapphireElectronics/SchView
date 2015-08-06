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

import ca.sapphire.altium.Options;
import ca.sapphire.altium.PowerPort;
import ca.sapphire.altium.Render;
import ca.sapphire.altium.Wire;
import ca.sapphire.graphics.Line;

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
    public int recordsRead = 0;
//    public int fpr = 0;

    public Render renderer = new Render();
    public Options options;

    public ArrayList<PowerPort> powerPorts = new ArrayList<>();
    public ArrayList<Wire> wires = new ArrayList<>();

    public ArrayList<ca.sapphire.altium.Object> objects = new ArrayList<>();


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

        Log.i(TAG, "Records read: " + recordsRead);
//        Log.i(TAG, "File pointer: " + fpr );

        // render all from Altium Objects to Graphics Objects
        for( ca.sapphire.altium.Object object : objects ) {
            object.render( renderer );
        }
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
                case 2:
                    addCompPin(result);
                    break;
                case 4:
                    addText(result);
                    break;
                case 6: // todo: Correct this ... these are actually component lines, not wires!!
                    objects.add( new Wire( result ));
                    break;
                case 7:
                    addPolygon(result);
                    break;
                case 13:
                    addCompGraphicLine(result);
                    break;
                case 14:
                    addCompGraphicBox(result);
                    break;

                case 17:
                    objects.add(new PowerPort( result ) );
                    break;

                case 26:
                    addBus(result);
                    break;
                case 27:
                    addWire(result);
                    break;
                case 29:
                    addJunction(result);
                    break;
                case 31:
                    options = new Options( result, renderer );
                    break;
                case 37:
                    addEntry(result);
                    break;
            }
        }

        line = null;
        recordsRead++;
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

    public void addCompPin(Map<String, String> record) {
        int x = Integer.parseInt(record.get("LOCATION.X"));
        int y = Integer.parseInt(record.get("LOCATION.Y"));
        int length = Integer.parseInt(record.get("PINLENGTH"));
//            designator = Integer.parseInt(record.get("DESIGNATOR"));
        int option = Integer.parseInt(record.get("PINCONGLOMERATE"));
//            if (record.get("NAME ") != null)
//                name = record.get("NAME");
//            int color = Integer.parseInt(record.get("COLOR"));
        // TODO: See if Altium pins have a colour attribute


        switch (option & 0x03) {
            case 0:
                renderer.objects.add((new Line(x, y, x + 10, y, 0xff0000)));
//                renderer.addLine(x, y, x + 10, y, 0xff0000);
                break;
            case 1:
                renderer.objects.add((new Line(x, y, x, y + 10, 0xff0000)));
//                renderer.addLine(x, y, x, y + 10, 0xff0000);
                break;
            case 2:
                renderer.objects.add((new Line(x, y, x - 10, y, 0xff0000)));
//                renderer.addLine(x, y, x - 10, y, 0xff0000);
                break;
            case 3:
                renderer.objects.add((new Line(x, y, x, y - 10, 0xff0000)));
//                renderer.addLine(x, y, x, y - 10, 0xff0000);
                break;


        }
    }

    public void addText( Map<String, String> record) {
        int x = Integer.parseInt(record.get("LOCATION.X"));
        int y = Integer.parseInt(record.get("LOCATION.Y"));
        int fontId = Integer.parseInt(record.get("FONTID"));
        int color = Integer.parseInt(record.get("COLOR"));

        renderer.addText( x, y, fontId, record.get("TEXT") );
    }

    public void addMultiLine( Map<String, String> record) {
        int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
        int x[] = new int[size];
        int y[] = new int[size];
        for (int i = 0; i < size; i++) {
            x[i] = Integer.parseInt((String) record.get("X" + String.valueOf(i + 1)));
            y[i] = Integer.parseInt((String) record.get("Y" + String.valueOf(i + 1)));
        }

        int color = Integer.parseInt(record.get("COLOR"));

        for (int i = 0; i < size-1; i++) {
            renderer.objects.add(new Line(x[i], y[i], x[i + 1], y[i + 1], color) );
//            renderer.addLine(x[i], y[i], x[i + 1], y[i + 1], color);
        }

    }

    public void addCornerLine( Map<String, String> record) {
        int x1 = Integer.parseInt(record.get("LOCATION.X"));
        int y1 = Integer.parseInt(record.get("LOCATION.Y"));
        int x2 = Integer.parseInt(record.get("CORNER.X"));
        int y2 = Integer.parseInt(record.get("CORNER.Y"));
        int color = Integer.parseInt(record.get("COLOR"));
        renderer.objects.add(new Line(x1, y1, x2, y2, color) );
    }

    public void addPolygon( Map<String, String> record) {

        int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
        int x[] = new int[size];
        int y[] = new int[size];
        for (int i = 0; i < size; i++) {
            x[i] = Integer.parseInt((String) record.get("X" + String.valueOf(i + 1)));
            y[i] = Integer.parseInt((String) record.get("Y" + String.valueOf(i + 1)));
        }

        int outline = Integer.parseInt(record.get("COLOR"));
        int area = Integer.parseInt(record.get("AREACOLOR"));

        renderer.addPolygon(x, y, area, true);
        renderer.addPolygon(x, y, outline, false );
    }

    public void addWire(Map<String, String> record) {
        addMultiLine(record);
    }

    public void addCompLine(Map<String, String> record) {
        addMultiLine(record);
    }

    public void addCompGraphicLine(Map<String, String> record) {
        addCornerLine(record);
    }

    public void addCompGraphicBox( Map<String, String> record) {
        if(record.get("ISSOLID") == null)
            return;

        int[] x = new int[4];
        int[] y = new int[4];

        x[0] = Integer.parseInt(record.get("LOCATION.X"));
        y[0] = Integer.parseInt(record.get("LOCATION.Y"));
        x[2] = Integer.parseInt(record.get("CORNER.X"));
        y[2] = Integer.parseInt(record.get("CORNER.X"));
        x[1] = x[0];
        y[1] = y[2];
        x[3] = x[2];
        y[3] = y[0];

        int outline = Integer.parseInt(record.get("COLOR"));
        int area = Integer.parseInt(record.get("AREACOLOR"));

        renderer.addPolygon(x, y, area, true);
        renderer.addPolygon( x, y, outline, false );
    }

    public void addBus( Map<String, String> record) {
        addMultiLine( record );
    }

    public void addJunction(Map<String, String> record) {
        int x = Integer.parseInt(record.get("LOCATION.X"));
        int y = Integer.parseInt(record.get("LOCATION.Y"));
        int color = Integer.parseInt(record.get("COLOR"));

        renderer.addCircle( x, y, 2, color );
    }

    public void addEntry(Map<String, String> record) {
        addCornerLine( record );
    }

    public int altiumToRGB( int altColor )
    {
        int red = ( altColor & 0xff ) << 16;
        int grn = altColor & 0xff00;
        int blu = ( altColor & 0xff0000 ) >> 16;
        return 0xff000000 | red | grn | blu;
    }

}




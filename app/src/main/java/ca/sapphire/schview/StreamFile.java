package ca.sapphire.schview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apreston on 7/30/2015.
 */

/**
 * Record Types
 *
 * Num	Imp	Name
 * 1  	N	Component ??
 * 2  	Y	Component Pin, TODO add pin types
 * 6  	Y	Component lines
 * 7    N   Component graphic - fill ( loccount, color, areacolor
 * 12   N   Component graphic - circle (loc.x, loc.y, radius, color, endangle)
 * 13   N   Component graphic - line (loc.x, loc.y, corn.x, corn.y, color)
 * 14 	N   ? Bounding box for component??
 * 25	N   Net label
 * 26	N	Bus - multi segment line: LOCATIONCOUNT segments, X1,Y1 , X2,Y2 etc.  TODO bus width
 * 27 	Y	Wire
 * 29 	Y	Junction
 * 34	N	Component Text drawn with Font
 * 37	Y	Bus entry ("LOCATION.X|Y" is the Bus end of the entry, CORNER.X|Y is the Wire end
 * 41 	N	Component Text - pin name, or more generally an attribute?
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

//    public ArrayList<CompPin> compPins = new ArrayList<>();
//    public ArrayList<CompLine> compLines = new ArrayList<>();
//    public ArrayList<Wire> wires = new ArrayList<>();
//    public ArrayList<Junction> junctions = new ArrayList<>();

    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Circle> circles = new ArrayList<>();


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

            if( result.get("OWNERINDEX") != null )
                if( result.get("OWNERINDEX").equals("81"))
//            if (record.equals("6"))
                    Log.i("6:", result.toString());


            switch (Integer.parseInt(record)) {
                case 2:
                    addCompPin(result);
                    break;
                case 6:
                    addCompLine(result);
                    break;
                case 27:
                    addWire(result);
                    break;
                case 29:
                    addJunction(result);
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

//    public class CompPin {
//        int x, y, length, designator, option;
//        String name;
//
//        public CompPin(Map<String, String> record) {
//            x = Integer.parseInt(record.get("LOCATION.X"));
//            y = Integer.parseInt(record.get("LOCATION.Y"));
//            length = Integer.parseInt(record.get("PINLENGTH"));
//            designator = Integer.parseInt(record.get("DESIGNATOR"));
//            option = Integer.parseInt(record.get("PINCONGLOMERATE"));
//            if (record.get("NAME ") != null)
//                name = record.get("NAME");
//        }
//
//        public void draw(Canvas canvas, Paint paint) {
//            switch (option & 0x03) {
//                case 0:
//                    canvas.drawLine(x, y, x + 10, y, paint);
//                    break;
//                case 1:
//                    canvas.drawLine(x, y, x, y + 10, paint);
//                    break;
//                case 2:
//                    canvas.drawLine(x, y, x - 10, y, paint);
//                    break;
//                case 3:
//                    canvas.drawLine(x, y, x, y - 10, paint);
//                    break;
//            }
//        }
//    }

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
                    lines.add(new Line(x, y, x + 10, y, 0xff0000));
                    break;
                case 1:
                    lines.add(new Line(x, y, x, y + 10, 0xff0000));
                    break;
                case 2:
                    lines.add(new Line(x, y, x - 10, y, 0xff0000));
                    break;
                case 3:
                    lines.add(new Line(x, y, x, y - 10, 0xff0000));
                    break;


            }
        }

    public void addWire(Map<String, String> record) {
            int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
            int x[] = new int[size];
            int y[] = new int[size];
            for (int i = 0; i < size; i++) {
                x[i] = Integer.parseInt((String) record.get("X" + String.valueOf(i + 1)));
                y[i] = Integer.parseInt((String) record.get("Y" + String.valueOf(i + 1)));
            }

            int color = Integer.parseInt(record.get("COLOR"));

            for (int i = 0; i < size-1; i++) {
                lines.add( new Line(x[i], y[i], x[i+1], y[i+1], color ) );
            }
        }

        public void addCompLine(Map<String, String> record) {
            int size = Integer.parseInt(record.get("LOCATIONCOUNT"));
            int x[] = new int[size];
            int y[] = new int[size];
            for (int i = 0; i < size; i++) {
                x[i] = Integer.parseInt((String) record.get("X" + String.valueOf(i + 1)));
                y[i] = Integer.parseInt((String) record.get("Y" + String.valueOf(i + 1)));
            }

            int color = Integer.parseInt(record.get("COLOR"));

            for (int i = 0; i < size-1; i++) {
                lines.add( new Line(x[i], y[i], x[i+1], y[i+1], color ) );
            }
        }


        public void addJunction(Map<String, String> record) {
            Circle circle = new Circle();
            circle.x = Integer.parseInt(record.get("LOCATION.X"));
            circle.y = Integer.parseInt(record.get("LOCATION.Y"));
            circle.radius = 2;
            circle.color = Integer.parseInt(record.get("COLOR"));
        }

    public void addEntry(Map<String, String> record) {
        Line line = new Line();
        line.x1 = Integer.parseInt(record.get("LOCATION.X"));
        line.y1 = Integer.parseInt(record.get("LOCATION.Y"));
        line.x2 = Integer.parseInt(record.get("CORNER.X"));
        line.y2 = Integer.parseInt(record.get("CORNER.Y"));
        line.color = Integer.parseInt(record.get("COLOR"));
        lines.add(new Line());
    }


    public class Line {
        public int x1,y1,x2,y2,color;

        public Line() {}

        public Line( int x1, int y1, int x2, int y2, int color ) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }

        public void draw( Canvas canvas, Paint paint ) {
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }

    public class Circle {
        public int x,y,radius,color;

        public void draw( Canvas canvas, Paint paint ) {
            canvas.drawCircle( x, y, radius, paint );
        }
    }

}




package ca.sapphire.schview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
 *
 * File Info:
 *      Fonts: fontidcount, size#=, fontname#=  eg SIZE1=10 FONTNAME1=Times.New.Roman
 */

public class StreamFile {
    public final static String TAG = "StreamFile";
    BufferedInputStream bis;
    public List<Map<String, String>> records = new ArrayList<Map<String, String>>();
    public int recordsRead = 0;
//    public int fpr = 0;


    public ArrayList<Line> lines = new ArrayList<>();
    public ArrayList<Circle> circles = new ArrayList<>();
    public ArrayList<Polygon> polygons = new ArrayList<>();
//    public ArrayList<Font> fonts = new ArrayList<>();
    public ArrayList<Text> texts = new ArrayList<>();

    public Render renderer = new Render();
    public Options options;

    public ArrayList<PowerPort> powerPorts = new ArrayList<>();


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
                case 6:
                    addCompLine(result);
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
                    powerPorts.add(new PowerPort(result, renderer) );
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

//    public void addFonts(Map<String, String> record) {
//        int size = Integer.parseInt(record.get("FONTIDCOUNT"));
//        for (int i = 0; i < size; i++) {
//            int fontSize = Integer.parseInt((String) record.get("SIZE" + String.valueOf(i + 1)));
//            fonts.add( new Font( fontSize, null ) );
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

    public void addText( Map<String, String> record) {
// Text (loc x/y, text, fontid, color
        int x = Integer.parseInt(record.get("LOCATION.X"));
        int y = Integer.parseInt(record.get("LOCATION.Y"));
        int fontId = Integer.parseInt(record.get("FONTID"));
        int color = Integer.parseInt(record.get("COLOR"));

        texts.add(  new Text( x, y, fontId, record.get("TEXT")));
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
            lines.add( new Line(x[i], y[i], x[i+1], y[i+1], color ) );
        }

    }

    public void addCornerLine( Map<String, String> record) {
        Line line = new Line();
        line.x1 = Integer.parseInt(record.get("LOCATION.X"));
        line.y1 = -Integer.parseInt(record.get("LOCATION.Y"));
        line.x2 = Integer.parseInt(record.get("CORNER.X"));
        line.y2 = -Integer.parseInt(record.get("CORNER.Y"));
        line.color = Integer.parseInt(record.get("COLOR"));
        lines.add(line);
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

        polygons.add( new Polygon(x, y, area, true ));
        polygons.add( new Polygon( x, y, outline, false ));
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

        polygons.add( new Polygon(x, y, area, true ));
        polygons.add( new Polygon( x, y, outline, false ));
    }

    public void addBus( Map<String, String> record) {
        addMultiLine( record );
    }

    public void addJunction(Map<String, String> record) {
        Circle circle = new Circle();
        circle.x = Integer.parseInt(record.get("LOCATION.X"));
        circle.y = Integer.parseInt(record.get("LOCATION.Y"));
        circle.radius = 2;
        circle.color = Integer.parseInt(record.get("COLOR"));
    }

    public void addEntry(Map<String, String> record) {
        addCornerLine( record );
    }


    public class Line {
        public int x1,y1,x2,y2,color;

        public Line() {}

        public Line( int x1, int y1, int x2, int y2, int color ) {
            this.x1 = x1;
            this.y1 = -y1;
            this.x2 = x2;
            this.y2 = -y2;
            this.color = color;
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setColor( altiumToRGB(color) );
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }

    public class Circle {
        public int x,y,radius,color;

        public void draw( Canvas canvas, Paint paint ) {
            paint.setColor( altiumToRGB(color) );
            canvas.drawCircle( x, y, radius, paint );
        }
    }

    public class Polygon {
        public Path path = new Path();
        public int color;
        public boolean filled;

        public Polygon( int[] x, int[] y, int color, boolean filled )
        {
            this.color = color;
            this.filled = filled;
            path.moveTo(x[0], -y[0]);
            for (int i = 1; i < x.length; i++) {
                path.lineTo( x[i], -y[i] );
            }
            path.lineTo(x[0], -y[0]);
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setColor( altiumToRGB(color));
            if( filled ) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPath(path, paint);
            }
            else {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, paint);
            }
        }
    }

    public class Text {
        int x, y, fontId;
        String name;

        public Text( int x, int y, int fontID, String name ) {
            this.x = x;
            this.y = -y;
            this.fontId = fontID;
            this.name = name;
        }

        public void draw( Canvas canvas, Paint paint ) {
            paint.setTextSize( renderer.fonts.get( fontId ).size );
//            paint.setTextSize( fonts.get( fontId ).size );
            canvas.drawText( name, x, y, paint );
        }
    }

    public int altiumToRGB( int altColor )
    {
        int red = ( altColor & 0xff ) << 16;
        int grn = altColor & 0xff00;
        int blu = ( altColor & 0xff0000 ) >> 16;
        return 0xff000000 | red | grn | blu;
    }

}




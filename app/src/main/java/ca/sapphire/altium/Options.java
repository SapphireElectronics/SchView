package ca.sapphire.altium;

// Todo: see if line drawing works on all devices, or whether drawLines is needed
// Todo: move calculations etc. into render.

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

import ca.sapphire.graphics.Text;
import ca.sapphire.graphics.GrEngine;

/**
 * Contains an Altium Options record
 */

public enum Options implements Object {
    INSTANCE;

    public static final int[] xSheetConst = { 0, 0, 0, 0, 0, 950, 1500 };
    public static final int[] ySheetConst = { 0, 0, 0, 0, 0, 750, 950 };
    public static final int[] xZonesConst = { 0, 0, 0, 0, 0, 4, 6 };
    public static final int[] yZonesConst = { 0, 0, 0, 0, 0, 4, 4 };

    boolean customSheet, titleBlockOn;

    byte fontCount, fontSize[], fontRotation[];
    public int xSheet = 0;
    public int ySheet = 0;
    public int width = 0;
    public int height = 0;
    int xMargin = 20;
    int yMargin = -20;
    int xZones = 0;
    int yZones = 0;
    int sheetStyle = 0;
    int grid = 0;
    String fontName[];


    public void put( String[] pairs ) {
        Map<String, String> result = new HashMap<>();

        for (String pair : pairs) {
            if (pair.trim().isEmpty()) continue;

            String[] data = pair.split("=");
            if (data.length == 2) {
                result.put(data[0], data[1]);
            }
        }
        put( result );
    }

    public void put( Map<String, String> record ) {
        customSheet = Utility.getBooleanValue(record, "VIEWCUSTOMSHEET", false);
        titleBlockOn = Utility.getBooleanValue(record, "TITLEBLOCKON", false);

        if( customSheet ) {
            xSheet = Utility.getIntValue( record, "CUSTOMX", 0);
            ySheet = -Utility.getIntValue( record, "CUSTOMY", 0);
            xMargin = Utility.getIntValue( record, "CUSTOMNMARGINWIDTH", 20);
            yMargin = -xMargin;
            xZones = Utility.getIntValue( record, "CUSTOMXZONES", 0);
            yZones = Utility.getIntValue( record, "CUSTOMYZONES", 0);
        }
        else {
            sheetStyle = Utility.getIntValue( record, "SHEETSTYLE", 0);
            if( sheetStyle >= xSheetConst.length )
                sheetStyle = 0;
            if( sheetStyle > 0) {
                width = xSheetConst[ sheetStyle ];
                height = ySheetConst[ sheetStyle ];
                xSheet = width;
                ySheet = -height;
                xZones = xZonesConst[ sheetStyle ];
                yZones = yZonesConst[ sheetStyle ];
            }
        }

        fontCount = Utility.getByteValue( record, "FONTIDCOUNT", (byte) 0 );

        fontSize = new byte[fontCount];
        fontRotation = new byte[fontCount];
        fontName = new String[fontCount];

        for (int i = 0; i < fontCount; i++) {
            fontSize[i] = Utility.getByteValue(record, "SIZE" + String.valueOf(i + 1));
            fontRotation[i] = Utility.getByteValue(record, "ROTATION" + String.valueOf(i + 1), (byte) 0);
            fontName[i] = record.get("FONTNAME" + String.valueOf(i + 1));
        }

        grid = Utility.getIntValue( record, "VISIBLEGRIDSIZE", 0);
    }

    @Override
    public void draw( Canvas canvas, Paint paint ) {
    }

    @Override
    public void read( DataInputStream dis ) {}

    @Override
    public void write( DataOutputStream dos ) {}

    @Override
    public void render() {}

    public void render( GrEngine engine ) {
        int color = 0xff404040;
        engine.addRectangle( 0, 0, xSheet, ySheet, color );
        engine.addRectangle( xMargin, yMargin, xSheet-xMargin, ySheet-yMargin, color);

        for (int i = 1; i < xZones; i++) {
            int xLoc = i*xSheet/xZones;
            engine.addLine(xLoc, 0, xLoc, yMargin, color);
            engine.addLine(xLoc, ySheet, xLoc, ySheet - yMargin, color);
        }
        for (int i = 1; i < yZones; i++) {
            int yLoc = i*ySheet/yZones;
            engine.addLine(0, yLoc, xMargin, yLoc, color);
            engine.addLine(xSheet, yLoc, xSheet - xMargin, yLoc, color);
        }

        int tColor = 0xff404040;
        int tSize = fontSize[0];

        for (int i = 0; i < xZones; i++) {
            int xLoc = (2*i+1)*xSheet/(xZones*2);
            engine.addText( String.valueOf( i+1 ), xLoc, yMargin/2, tColor, tSize, GrEngine.Halign.CENTER, GrEngine.Valign.CENTER );
            engine.addText( String.valueOf( i+1 ), xLoc, ySheet-yMargin/2, tColor, tSize, GrEngine.Halign.CENTER, GrEngine.Valign.CENTER );
        }

        int character = 64 + yZones;
        for (int i = 0; i < yZones; i++) {
            int yLoc = (2*i+1)*ySheet/(yZones*2);
            String ch = Character.toString ((char) (character--));

            engine.addText( ch, xMargin/2, yLoc, tColor, tSize, GrEngine.Halign.CENTER, GrEngine.Valign.CENTER );
            engine.addText( ch, xSheet-xMargin/2, yLoc, tColor, tSize, GrEngine.Halign.CENTER, GrEngine.Valign.CENTER );
        }

        // standard title block
        if( titleBlockOn ) {
            float tbX = xSheet - 20;
            float tbY = -20;

            engine.addLine(tbX, tbY - 10, tbX - 350, tbY - 10, color);
            engine.addLine(tbX, tbY - 20, tbX - 350, tbY - 20, color);
            engine.addLine(tbX, tbY - 50, tbX - 350, tbY - 50, color);
            engine.addLine(tbX, tbY - 80, tbX - 350, tbY - 80, color);

            engine.addLine(tbX - 100, tbY - 20, tbX - 100, tbY - 50, color);
            engine.addLine(tbX - 150, tbY, tbX - 150, tbY - 20, color);
            engine.addLine(tbX - 300, tbY - 20, tbX - 300, tbY - 50, color);
            engine.addLine(tbX - 350, tbY, tbX - 350, tbY - 80, color);
        }

//        if( grid > 0 ) {
//            color = 0xffc0c0c0;
//            for (int i = xMargin+grid; i <= (xSheet-xMargin)-grid; i+=grid) {
//                engine.addLine( i, yMargin, i, ySheet-yMargin, color );
//            }
//        }
    }
}

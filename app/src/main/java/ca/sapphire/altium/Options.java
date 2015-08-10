package ca.sapphire.altium;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Map;

import ca.sapphire.graphics.Text;

/**
 * Contains an Altium Options record
 */

public enum Options implements Object {
    INSTANCE;

    public static final int[] xSheetConst = { 0, 0, 0, 0, 0, 950, 1500 };
    public static final int[] ySheetConst = { 0, 0, 0, 0, 0, 750, 950 };
    public static final int[] xZonesConst = { 0, 0, 0, 0, 0, 4, 6 };
    public static final int[] yZonesConst = { 0, 0, 0, 0, 0, 4, 6 };

    boolean customSheet;

    byte fontCount, fontSize[], fontRotation[];
    int xSheet = 0;
    int ySheet = 0;
    int xMargin = 20;
    int yMargin = -20;
    int xZones = 0;
    int yZones = 0;
    int sheetStyle = 0;
    String fontName[];

    public void put( Map<String, String> record ) {
        customSheet = Utility.getBooleanValue(record, "VIEWCUSTOMSHEET", false);

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
                xSheet = xSheetConst[ sheetStyle ];
                ySheet = -ySheetConst[ sheetStyle ];
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
    }

    @Override
    public void draw( Canvas canvas, Paint paint ) {
        paint.setColor(0xffc0c0c0);
        paint.setStrokeWidth(0);
        canvas.drawLine(0, 0, xSheet, 0, paint);
        canvas.drawLine(xSheet, 0, xSheet, ySheet, paint);
        canvas.drawLine( 0, ySheet, 0, ySheet, paint );
        canvas.drawLine( 0, ySheet, 0, 0, paint );
        canvas.drawLine( xMargin, yMargin, xSheet-xMargin, yMargin, paint );
        canvas.drawLine( xSheet-xMargin, yMargin, xSheet-xMargin, ySheet-xMargin, paint );
        canvas.drawLine( xSheet-xMargin, ySheet-yMargin, xMargin, ySheet-yMargin, paint );
        canvas.drawLine( xMargin, ySheet-yMargin, xMargin, yMargin, paint );

        for (int i = 1; i < xZones; i++) {
            int xLoc = i*xSheet/xZones;
            canvas.drawLine( xLoc, 0, xLoc, yMargin, paint );
            canvas.drawLine( xLoc, ySheet, xLoc, ySheet-yMargin, paint );
        }
        for (int i = 1; i < yZones; i++) {
            int yLoc = i*ySheet/yZones;
            canvas.drawLine( 0, yLoc, xMargin, yLoc, paint );
            canvas.drawLine( xSheet, yLoc, xSheet-xMargin, yLoc, paint );
        }

        for (int i = 0; i < xZones; i++) {
            int xLoc = (2*i+1)*xSheet/(xZones*2);
            Text tag = new Text( String.valueOf( i+1 ), new PointF( xLoc, yMargin/2 ), 0xffc0c0c0, Text.Halign.CENTER, Text.Valign.CENTER );
            tag.draw(canvas, paint);
            tag = new Text( String.valueOf( i+1 ), new PointF( xLoc, ySheet-yMargin/2 ), 0xffc0c0c0, Text.Halign.CENTER, Text.Valign.CENTER );
            tag.draw(canvas, paint);
        }

        for (int i = 0; i < yZones; i++) {
            int yLoc = (2*i+1)*ySheet/(yZones*2);
//            String ch = new String( Character.toChars( i+1 ) );
            String ch = new String( "*" );
            Text tag = new Text( ch, new PointF( xMargin/2, yLoc ), 0xffc0c0c0, Text.Halign.CENTER, Text.Valign.CENTER );
            tag.draw( canvas, paint );
            tag = new Text( ch, new PointF( xMargin-xMargin/2, yLoc ), 0xffc0c0c0, Text.Halign.CENTER, Text.Valign.CENTER );
            tag.draw(canvas, paint);
        }

    }

    @Override
    public void read( DataInputStream dis ) {}

    @Override
    public void write( DataOutputStream dos ) {}

    @Override
    public void render() {}
}

package ca.sapphire.schview;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apreston on 7/30/2015.
 */
public class StreamFile {
    public final static String TAG = "StreamFile";
    BufferedInputStream bis;
    public List<Map<String, String>> records = new ArrayList<Map<String, String>>();
    public int recordsRead = 0;
//    public int fpr = 0;

    public ArrayList<int[]> wires = new ArrayList<int[]>();

    public StreamFile( String fileName ) {
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

        if( record != null ){
            if (record.equals("27")) {
                int x1 = Integer.parseInt(result.get("X1"));
                int y1 = Integer.parseInt(result.get("Y1"));
                int x2 = Integer.parseInt(result.get("X2"));
                int y2 = Integer.parseInt(result.get("Y2"));

                wires.add(new int[]{x1, y1, x2, y2});
            }
        }


        line = null;
        recordsRead++;
        return result;
    }


    public String readLine() throws IOException {
        int length = readInt();
//        fpr += 4;
        if (length == -1) return null;

        byte[] buffer = new byte[length];


        if( buffer == null )
            return "";

        if( bis.read(buffer, 0, length) != length ) {
            Log.i(TAG, "Didn't read enough bytes");
        }
//        fpr += length;

        if (buffer[0] == 0) return null;

        return new String(buffer).split("\u0000")[0];
    }

    public int readInt() throws IOException {
        return (((int)(bis.read()&0xff)      ) |
                ((int)(bis.read()&0xff) << 8 ) |
                ((int)(bis.read()&0xff) << 16) |
                ((int)(bis.read()&0xff) << 24)   );
    }

}

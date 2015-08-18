package ca.sapphire.schview;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

/**
 * Streamed File Object
 * A list if sectors read from a Compound File, used by a Random Access File to read the actual data
 *
 * Allocates an internal buffer that is two sectors long, assumes we'll never read more than
 * "sector" bytes at a time.  Once the buffer pointer is greater and the end of a sector,
 * the buffer is compacted and a new sector read into the buffer after what is already there
 */


public class StreamedFile {
    public static final String TAG = "FileStream";
    public static final int BUFFER_SECTORS = 4;

    RandomAccessFile raf;
    List<Integer> sectorList;
    int currentSector;
    int sectorSize;
    byte[] transfer;
    ByteBuffer bb;
    boolean eof = false;

    public StreamedFile( RandomAccessFile raf, List<Integer> sectorList, int sectorSize ) {
        this.raf = raf;
        this.sectorList = sectorList;
        this.sectorSize = sectorSize;
        currentSector = 0;
        transfer = new byte[sectorSize];
        bb = ByteBuffer.allocate(sectorSize * BUFFER_SECTORS);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.limit( 0 );

        try {
            seekToNextSector();
            check();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int available() {
        return bb.remaining();
    }

    public int readInt() throws IOException {
        int returnInt = bb.getInt();
        check();
        return returnInt;
    }

    public void readBytes( byte[] buf, int bytesToRead ) throws IOException {
        try {
            bb.get( buf, 0, bytesToRead );
        } catch (BufferUnderflowException e) {
            Log.e(TAG, "ByteBuffer is too small.");
            Log.e(TAG, "Position, Limit: " + bb.position() + ", " + bb.limit());
            Log.e(TAG, "Size request: " + bytesToRead );
            e.printStackTrace();
            throw e;
        }
        check();
    }

    public void check() throws IOException {
        // don't bother compacting if only a few bytes have been read.
        if( eof || ( bb.position() < 16 ) )
            return;

        bb.compact();

        // Compact puts buffer into Write mode, fill as much as possible
        while( ( bb.limit() - bb.position() ) >= sectorSize ) {
            readSector();
        }

        bb.flip();
    }

    private void seekToNextSector() throws IOException {
        if( currentSector < sectorList.size() ) {
            raf.seek(512 + sectorList.get(currentSector++) * sectorSize);
        }
        else {
            Log.i(TAG, "At EOF");
            eof = true;
        }
    }

    public void readSector() throws IOException {
        // seek has already been done so just need to read and transfer to the byte buffer
        if( eof ) return;
        try {
            // read sector, transfer it to byte buffer, then seek to next sector
            raf.readFully( transfer );
            bb.put( transfer );
            seekToNextSector();
        } catch (EOFException e) {
            Log.e( TAG, "Attempted read past end of file.");
        }
    }
}

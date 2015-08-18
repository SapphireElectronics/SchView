package ca.sapphire.schview;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
    RandomAccessFile raf;
    List<Integer> sectorList;
    int currentSector;
    int sectorSize;
    byte[] bytes;
    byte[] transfer;
    ByteBuffer bb;
    boolean eof = false;

    public StreamedFile( RandomAccessFile raf, List<Integer> sectorList, int sectorSize ) {
        this.raf = raf;
        this.sectorList = sectorList;
        this.sectorSize = sectorSize;
        currentSector = 0;
        transfer = new byte[sectorSize];
        bb = ByteBuffer.allocate( sectorSize*2 );
        bb.order(ByteOrder.LITTLE_ENDIAN);

        try {
            seekToNextSector();
            readSector();
            readSector();
            bb.position( 0 );

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
        bb.get( buf, 0, bytesToRead );
        check();
    }

    public void check() throws IOException {
        if( bb.position() >= sectorSize ) {
            bb.compact();
            readSector();
            bb.position( 0 );
        }
    }

    private void seekToNextSector() throws IOException {
        if( currentSector >= sectorList.size() ) {
            eof = true;
            return;
        }

        raf.seek(512 + sectorList.get(currentSector++ ) * sectorSize );
    }

    public void readSector() throws IOException {
        // seek has already been done so just need to read and transfer to the bytebuffer
        if( eof ) return;
        try {
            // see if there's a full sector to read
            raf.readFully( transfer );
            bb.put( transfer );
            // seek to the next sector, hopefully will lower occurrence of blocking.
            // if next sector doesn't exist we're at the end of the file
            seekToNextSector();
        } catch (EOFException e) {
            // got here because there's not a full sector left to read
            // read bytes until we get to EOF (bytesRead = -1)
            int bytesRead;
            while( (bytesRead = raf.read( transfer) ) > 0 )
            {
                bb.put( transfer, 0, bytesRead );
            }
            // set the size of the buffer to only be the amount of data remaining to EOF
            // this makes it so the remaining() function works properly
            bb.limit( bb.remaining() );
            eof = true;
        }
    }
}

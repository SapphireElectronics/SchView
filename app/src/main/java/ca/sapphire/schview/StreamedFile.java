package ca.sapphire.schview;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Streamed File Object
 * A streamed file is either:
 * - a list of sectors based on a Random Access File (as decoded from a Compound File
 * - a storage based file initially written from a sector list as above
 */

public class StreamedFile {
    RandomAccessFile raf;
    List<Integer> sectorList;
    int currentSector;
    int sectorSize;
    byte[] buffer;

    public void StreamedFile( RandomAccessFile raf, List<Integer> sectorList, int sectorSize ) {
        this.raf = raf;
        this.sectorList = sectorList;
        this.sectorSize = sectorSize;
        currentSector = 0;
        buffer = new byte[sectorSize];
    }

    public void readNextSector() {
        if( currentSector >= sectorList.size() )
            return;

        try {
            raf.seek( 512 + sectorList.get( currentSector++ ) * sectorSize );
            raf.readFully( buffer, 0, sectorSize );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

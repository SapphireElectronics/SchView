package ca.sapphire.schview;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Reads a Compound Object File
 */
public class CompoundFile {
    RandomAccessFile raf;
    boolean littleEndian = true;

    public final static String TAG = "CompoundFile";

    Header header;

    int[] sat;

    ArrayList<byte[]> satSectors = new ArrayList<>();     // sector into for SAT
    ArrayList<byte[]> dirSectors = new ArrayList<>();     // sector info for Directory
//    ArrayList<byte[]> shortSector = new ArrayList<>();    // sector info for Short SAT

    ArrayList<Integer> dirID = new ArrayList<>();        // list of sectors in Directory stream
    ArrayList<Integer> fileID = new ArrayList<>();                        // list of sectors in HeaderFile stream
    ArrayList<Integer> dirTraverse = new ArrayList<>();  // list of directories compiled when traversing the directory tree

    int mainDataSecID;
    int mainDataSize = 0;

    int sectorBytes;

    StreamFile sf;

    public boolean done = false;

    public CompoundFile( String fileName ) {
        try {
            raf = new RandomAccessFile( fileName, "r" );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Read HEADER
        // header is always 512 bytes
        byte[] buffer = new byte[512];
        readNextSector( buffer, 512 );

        header = new Header();
        if( header.read( buffer ) < 0 ) {
            Log.i(TAG, "Unable to validate the header entry.");
            return;
        }

        Log.i( TAG, "Total sectors: " + header.numberOfSectors );
        Log.i( TAG, "Total short sectors: " + header.numberOfShortSectors );
        Log.i( TAG, "Total sectors in table: " + header.masterNumberOfSectors );

        Log.i( TAG, "Directory sector: " + header.directorySectorID );
        Log.i( TAG, "Short sector: " + header.shortSectorID );
        Log.i( TAG, "Master sector: " + header.masterSectorID );

        // Read MSAT
        // currently only 109 entries are supported (entries are stored in Header)
        if( header.numberOfSectors > 109 || header.masterSectorID != -2 ) {
            Log.i( TAG, "Too many Master Sectors to read.");
            return;
        }

        // Read sectors
        sectorBytes = 1 << header.sectorSize;

        // currently at end of header, which is start of Sector 0
        // byte count for the start of a sector is 512 (for header) + sector*sectorSize

        for (int i = 0; i < header.numberOfSectors; i++) {
            try {
                raf.seek( 512 + header.msat[i] * sectorBytes );
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] newSector = new byte[sectorBytes];
            readNextSector( newSector, sectorBytes );

/**
 * Seems to be a slight bug in the Altium file writer.  SAT sectors are supposed to start with -3
 * as a flag to indicate it's a SAT chain sector, but in at least one (valid) Altium file, the
 * -3 is actually on the last sector id of the previous SAT sector, and the next SAT sector starts
 * with the next location, not -3.  Therefore, ignore the first value in the SAT sector as needing
 * to be -3 and just add the sectors to the SAT sector list
 */
//            if( getInt( newSector, 0 ) == -3)
            satSectors.add( newSector );
        }

        Log.i(TAG, "Read in " + header.numberOfSectors + " sectors.");

        // Read SAT
        // only one sector to read, generally Sector 0
        sat = new int[sectorBytes/4*satSectors.size()];

        for (int j = 0; j < satSectors.size(); j++) {
            ByteBuffer satBuffer = ByteBuffer.wrap(satSectors.get(j));
            satBuffer.order( ByteOrder.LITTLE_ENDIAN );
            for (int i = 0; i < sectorBytes / 4; i++) {
                sat[i+j*sectorBytes/4] = satBuffer.getInt(i*4);
            }
        }

        // Read SSAT

        // Read Directory
        // Walk the Directory chain in the SAT until we get the value -2
        int directorySector = header.directorySectorID;

        while( directorySector != -2 ) {
            dirID.add(header.directorySectorID);
            directorySector = sat[directorySector];
        }

        for (int i = 0; i < dirID.size(); i++) {
            try {
                raf.seek( 512 + dirID.get(i) * sectorBytes );
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] newSector = new byte[sectorBytes];
            readNextSector(newSector, sectorBytes);

            dirSectors.add(newSector);
        }

        traverse( 0 );

        // at this point, we should have mainDataSecID and mainDataSize set

        if( mainDataSize < 0 ) {
            Log.i(TAG, "Main data not found");
            return;
        }

        // Walk the main data chain in the SAT until we get the value -2
        int fileSecID = mainDataSecID;

        try {
            while( fileSecID != -2 ) {
                fileID.add( fileSecID );
                fileSecID = sat[ fileSecID ];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // make sure manifest allows write to external storage or this will fail.
        BufferedOutputStream bos = null;
        String streamFileName = fileName + ".str";
        byte[] bf = new byte[sectorBytes];

        try {
            bos = new BufferedOutputStream(new FileOutputStream(streamFileName));
            for( int sector : fileID ) {
                raf.seek( 512 + sector * sectorBytes );
                raf.readFully(bf, 0, sectorBytes);
                bos.write( bf );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if( bos != null)
                bos.close();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sf = new StreamFile( streamFileName );

        done = true;
    }

    public void traverse( int id ) {
        dirTraverse.add( id );

        Directory dir = new Directory();

        int dirsPerSector = sectorBytes/128;

        int secID = id/dirsPerSector;            // four IDs per sector
        int offset = (id % dirsPerSector) * 128;

        dir.read( dirSectors.get( secID ), offset );

        Log.i(TAG, "Dir " + dir.name);

        if(  dir.name.equals( "FileHeader")) {
            Log.i( TAG, "Found FileHeader.");
            mainDataSecID = dir.sectorID;
            mainDataSize = dir.streamSize;
        }

        if( dir.rootDirID != -1 ) {
            traverse(dir.rootDirID);
        }

        if( dir.leftDirID != -1 ) {
            traverse(dir.leftDirID);
        }

        if( dir.rightDirID != -1 ) {
            traverse(dir.rightDirID);
        }

    }

    public void readNextSector( byte[] buffer, int bytes ) {
        try {
            raf.readFully( buffer, 0, bytes );
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "readNextSector: unable to read in a full sector.");
        }
    }

    public class Header {
        public final byte[] fileID = new byte[]{-48, -49, 17, -32, -95, -79, 26, -31};  // d0 cf 11 e0 a1 b1 1a e1
        public byte[] headerID = new byte[8];
        public boolean littleEndian;
        short sectorSize;
        short shortSectorSize;
        int numberOfSectors;
        int directorySectorID;
        int standardStreamSize;
        int shortSectorID;
        int numberOfShortSectors;
        int masterSectorID;
        int masterNumberOfSectors;
        public int[] msat = new int[109];

        public int read(byte[] buffer) {
            // see if it's actually a Compound Document File
            ByteBuffer bb = ByteBuffer.wrap( buffer );
            bb.order(ByteOrder.LITTLE_ENDIAN);

            System.arraycopy(buffer, 0, headerID, 0, 8);

            if (!Arrays.equals(headerID, fileID)) {
                Log.i(TAG, "Not a Compound Document File.  ID did not match.");
                return -1;
            }

            littleEndian = (buffer[28] == (byte) 0xfe) && (buffer[29] == (byte) 0xff);
            sectorSize = bb.getShort(30);                  // offset 30, size 2
            shortSectorSize = bb.getShort(32);             // offset 32, size 2
            numberOfSectors = bb.getInt(44);                 // offset 44, size 4
            directorySectorID = bb.getInt(48);               // offset 48, size 4
            standardStreamSize = bb.getInt(56);              // offset 56, size 4
            shortSectorID = bb.getInt(60);                   // offset 60, size 4
            numberOfShortSectors = bb.getInt(64);            // offset 64, size 4
            masterSectorID = bb.getInt(68);                  // offset 68, size 4
            masterNumberOfSectors = bb.getInt(72);           // offset 72, size 4

            for (int i = 0; i < 109; i++) {
                msat[i] = bb.getInt(76 + (i * 4));
            }
            return 0;
        }
    }

    public class Directory {
        public char[] nameChar = new char[32];
        public short nameSize;
        public byte type;
//        public byte colour;
        public int leftDirID;
        public int rightDirID;
        public int rootDirID;
        //        public byte[] uniqueID = new byte[16];
//        public int flags;
//        public byte[] timeStampCreation = new byte[8];
//        public byte[] timeStampModification = new byte[8];
        public int sectorID;
        public int streamSize;

        public String name;

        public void read( int directoryIndex ) {
            int dirsPerSector = sectorBytes / 128;
            int secID = dirID.get(directoryIndex / dirsPerSector);     // four IDs per sector
            int offset = (directoryIndex % dirsPerSector) * 128;

            byte[] buffer = dirSectors.get(secID);

            read( buffer, offset );
        }

        public void read( byte[] buffer, int index) {
            ByteBuffer bb = ByteBuffer.wrap( buffer );
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.position( index );

            for (int i=0; i<32; i++)
                nameChar[i] = bb.getChar();

            nameSize = bb.getShort( index+64 );
            name = new String( nameChar, 0, nameSize/2-1 );

            type = bb.get( index+66 );
//            colour = buffer[index+67];
            leftDirID = bb.getInt( index+68 );
            rightDirID = bb.getInt( index+72 );
            rootDirID = bb.getInt( index+76 );
//            System.arraycopy( buffer, index+80, uniqueID, 0, 16 );
//            flags = getInt( buffer, index+96 );
//            System.arraycopy( buffer, index+100, timeStampCreation, 0, 8 );
//            System.arraycopy( buffer, index+108, timeStampModification, 0, 8 );
            sectorID = bb.getInt( index+116 );
            streamSize = bb.getInt( index+120 );
            // total of 128 bytes
        }
    }
}

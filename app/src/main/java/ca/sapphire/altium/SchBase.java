package ca.sapphire.altium;

import java.util.Map;

/**
 * Created by Admin on 07/08/15.
 */
public class SchBase{
    int recordNumber = 1;
    int ownerIndex = -2;        // OWNERINDEX
    int sheetIndex = -2;        // INDEXINSHEET

    public SchBase( Map<String, String> record ) {
        if (record.get("OWNERINDEX") != null)
            ownerIndex = Utility.getIntValue(record, "OWNERINDEX");
        if (record.get("INDEXINSHEET") != null)
            ownerIndex = Utility.getIntValue(record, "INDEXINSHEET");
    }
}

package medraine.about.dpm;

import android.provider.BaseColumns;

/**
 * Created by Raine on 23.12.2016.
 */

final class PFeedReaderContract {
    public PFeedReaderContract() {}
    static abstract class PFeedEntry implements BaseColumns {
        static final String TABLE_NAME = "data";
        static final String COLUMN_NAME_ENTRY_ID = "data_id";
        static final String COLUMN_NAME_TITLE = "keyTitle";
        static final String COLUMN_NAME_PASS = "keyPssw";
        static final String COLUMN_NAME_USER = "keyUser";
        static final String COLUMN_NAME_PLACE = "keyPlace";
    }
}

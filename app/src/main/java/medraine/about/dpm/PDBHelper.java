package medraine.about.dpm;

import android.content.Context;

import net.sqlcipher.DatabaseErrorHandler;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by Raine on 23.12.2016.
 */
/*class Instance {
    private PDBHelper instance;
    boolean getInstanceBool() {
        return instance == null;
    }
    PDBHelper getInstance() {
        return instance;
    }
    void setInstance(Context context) {
        instance = new PDBHelper(context);
    }
}*/
class PDBHelper extends SQLiteOpenHelper {
    private static PDBHelper instance;

    //static Instance instance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PasswordStorage.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            PFeedReaderContract.PFeedEntry.TABLE_NAME + " (" +
            PFeedReaderContract.PFeedEntry._ID + " INTEGER PRIMARY KEY," +
            PFeedReaderContract.PFeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + "," +
            PFeedReaderContract.PFeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + "," +
            PFeedReaderContract.PFeedEntry.COLUMN_NAME_PASS + TEXT_TYPE + "," +
            PFeedReaderContract.PFeedEntry.COLUMN_NAME_USER + TEXT_TYPE + "," +
            PFeedReaderContract.PFeedEntry.COLUMN_NAME_PLACE + TEXT_TYPE + ")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLES IF EXISTS " +
            PFeedReaderContract.PFeedEntry.TABLE_NAME;

    /*public PDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public PDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook) {
        super(context, name, factory, version, hook);
    }

    public PDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, SQLiteDatabaseHook hook, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, hook, errorHandler);
    }*/

    private PDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static synchronized PDBHelper getInstance(Context context) {
        if(instance == null) {
            instance = new PDBHelper(context);
        }
        return instance;
        /*if(instance.getInstanceBool()) {
            instance.setInstance(context);
        }
        return instance.getInstance();*/
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

package medraine.about.dpm;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvPssw;
    ArrayList<PasswordEntry> pe;
    SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("MULTI", false);
                AddingDialog addingDialog = new AddingDialog();
                addingDialog.setArguments(bundle);
                addingDialog.show(getFragmentManager(), "adding");

                Snackbar.make(view, "Password added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //SQLiteDatabase.loadLibs(this);

        pe = new ArrayList<>();
        sharedPreference = getSharedPreferences("arkas", MODE_PRIVATE);
        String checkout = sharedPreference.getString("DATASET", null);

        if(checkout == null) {

            pe.add(new PasswordEntry("Facebook", 25, "mirrors.edga@mail.ru", "facebook.com"));
            pe.add(new PasswordEntry("VK", 26, "+380633149289", "vk.com"));

            for(int i = 0; i < pe.size()-1; i++) {
                pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
            }

            SharedPreferences.Editor ed= sharedPreference.edit();
            ed.putString("DATASET", "1");
            ed.apply();
            insertSthToDB();
            /*DBInsertTask d = new DBInsertTask();
            d.execute(PDBHelper.getInstance(this).getWritableDatabase(getIntent().getStringExtra("DATA")));*/
            Log.d("TAG!!!", "COMMIT SUCCESSFUL");
        }
        else {
            getSthFromDB();
            for(int i = 0; i < pe.size()-1; i++) {
                pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
            }
        }

        PasswordsAdapter passwordsAdapter = new PasswordsAdapter(this, pe);

        //final Context internal = this;

        lvPssw = (ListView) findViewById(R.id.lvPssw);
        lvPssw.setAdapter(passwordsAdapter);
        lvPssw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Password copied", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Password", pe.get(position).getPssw());
                clipboardManager.setPrimaryClip(clipData);
            }
        });
        lvPssw.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("POSITION", position);
                EditingDialog editingDialog = new EditingDialog();
                editingDialog.setArguments(bundle);
                editingDialog.show(getFragmentManager(), "editing");
                return false;
            }
        });
    }

    public void addDataSet(PasswordEntry p) {
        pe.add(p);
        PasswordsAdapter ps = (PasswordsAdapter)lvPssw.getAdapter();
        ps.notifyDataSetChanged();
        for(int i = 0; i < pe.size()-1; i++) {
            pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
        }
        insertSthToDB();
    }

    public void regenerateDataSet(int position, int length) {
        Log.d("TAG", "MAIN GET!");
        pe.get(position).setPssw(pe.get(0).reGenerate(length));
        Log.d("TAG", "DATA " + position +" " + length);
        PasswordsAdapter ps = (PasswordsAdapter)lvPssw.getAdapter();
        ps.notifyDataSetChanged();
        lvPssw.refreshDrawableState();
        for(int i = 0; i < pe.size()-1; i++) {
            pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
        }
        insertSthToDB();
    }

    public void editDataSet(int position) {

        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        bundle.putBoolean("MULTI", true);
        bundle.putString("TITLE", pe.get(position).getTitle());
        bundle.putString("PSSW", pe.get(position).getPssw());
        bundle.putString("USER", pe.get(position).getUsr());
        bundle.putString("PLACE", pe.get(position).getPlace());
        AddingDialog addingDialog = new AddingDialog();
        addingDialog.setArguments(bundle);
        addingDialog.show(getFragmentManager(), "re-editing");

        PasswordsAdapter ps = (PasswordsAdapter)lvPssw.getAdapter();
        ps.notifyDataSetChanged();
        for(int i = 0; i < pe.size()-1; i++) {
            pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
        }
        insertSthToDB();
    }

    public void getDataSet(int position, PasswordEntry p) {
        pe.set(position, p);
        PasswordsAdapter ps = (PasswordsAdapter)lvPssw.getAdapter();
        ps.notifyDataSetChanged();
        for(int i = 0; i < pe.size()-1; i++) {
            pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
        }
        insertSthToDB();
    }

    public void deleteDataSet(int position) {
        pe.remove(position);
        PasswordsAdapter ps = (PasswordsAdapter)lvPssw.getAdapter();
        ps.notifyDataSetChanged();
        for(int i = 0; i < pe.size()-1; i++) {
            pe.get(i).setPssw(reCheck(pe.get(i), pe.get(i+1)));
        }
        insertSthToDB();
    }

    public String reCheck(PasswordEntry p1, PasswordEntry p2) {
        String ps1 = p1.getPssw();
        String ps2 = p2.getPssw();
        int s ;
        int v ;
        boolean data;
        int second = 0;
        if(ps1.length() >= ps2.length()) {
            s = ps1.length();
            v = ps2.length();
            data = true;
        }
        else {
            s = ps2.length();
            v = ps1.length();
            data = false;
        }
        int unique = 0;
        for(int i = 0; i < s; i++) {
            if(second < v) {
                if(data) {
                    if (ps1.toCharArray()[i] == ps2.toCharArray()[second]) {
                        unique++;
                    }
                }
                else {
                    if (ps2.toCharArray()[i] == ps1.toCharArray()[second]) {
                        unique++;
                    }
                }
            }
            second++;
        }
        if(unique > 4) {
            return p1.reGenerate(p1.getLength());
        }

        return p1.getPssw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        insertSthToDB();
    }


    private void insertSthToDB() {
        SQLiteDatabase db = PDBHelper.getInstance(this).getWritableDatabase(getIntent().getStringExtra("DATA"));
        db.execSQL("DELETE FROM "+ PFeedReaderContract.PFeedEntry.TABLE_NAME);

        ContentValues values = new ContentValues();
        for(int i = 0; i < pe.size(); i++) {
            values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_ENTRY_ID, i);
            values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_TITLE, pe.get(i).getTitle());
            values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PASS, pe.get(i).getPssw());
            values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_USER, pe.get(i).getUsr());
            values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PLACE, pe.get(i).getPlace());

            db.insert(PFeedReaderContract.PFeedEntry.TABLE_NAME, null, values);
            values = new ContentValues();
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + PFeedReaderContract.PFeedEntry.TABLE_NAME+";", null);
        Log.d(MainActivity.class.getSimpleName(), "Rows count: " + cursor.getCount());
        cursor.close();
        db.close();
    }

    private void getSthFromDB() {
        SQLiteDatabase db = PDBHelper.getInstance(this).getWritableDatabase(getIntent().getStringExtra("DATA"));

        Cursor cursor = db.query(PFeedReaderContract.PFeedEntry.TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_ENTRY_ID);
            int titleColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_TITLE);
            int psswColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PASS);
            int userColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_USER);
            int placeColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PLACE);

            do {

                pe.add(cursor.getInt(idColIndex), new PasswordEntry(cursor.getString(titleColIndex),
                        cursor.getString(psswColIndex),
                        cursor.getString(userColIndex),
                        cursor.getString(placeColIndex)));

            } while (cursor.moveToNext());
        }
        else {
            Toast.makeText(this, "No data found!", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        db.close();
    }

    class DBInsertTask extends AsyncTask<SQLiteDatabase, Void, Void> {

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {
            params[0].execSQL("DELETE FROM "+ PFeedReaderContract.PFeedEntry.TABLE_NAME);

            ContentValues values = new ContentValues();
            for(int i = 0; i < pe.size(); i++) {
                values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_ENTRY_ID, i);
                values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_TITLE, pe.get(i).getTitle());
                values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PASS, pe.get(i).getPssw());
                values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_USER, pe.get(i).getUsr());
                values.put(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PLACE, pe.get(i).getPlace());

                params[0].insert(PFeedReaderContract.PFeedEntry.TABLE_NAME, null, values);
                values = new ContentValues();
            }

            Cursor cursor = params[0].rawQuery("SELECT * FROM " + PFeedReaderContract.PFeedEntry.TABLE_NAME+";", null);
            Log.d(MainActivity.class.getSimpleName(), "Rows count: " + cursor.getCount());
            cursor.close();
            params[0].close();
            return null;
        }
    }

    class DBGetTask extends AsyncTask<SQLiteDatabase, Void, Void> {

        @Override
        protected Void doInBackground(SQLiteDatabase... params) {
            Cursor cursor = params[0].query(PFeedReaderContract.PFeedEntry.TABLE_NAME, null, null, null, null, null, null);
            if(cursor.moveToFirst()) {
                int idColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_ENTRY_ID);
                int titleColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_TITLE);
                int psswColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PASS);
                int userColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_USER);
                int placeColIndex = cursor.getColumnIndex(PFeedReaderContract.PFeedEntry.COLUMN_NAME_PLACE);

                do {

                    pe.add(cursor.getInt(idColIndex), new PasswordEntry(cursor.getString(titleColIndex),
                            cursor.getString(psswColIndex),
                            cursor.getString(userColIndex),
                            cursor.getString(placeColIndex)));

                } while (cursor.moveToNext());
            }
            else {
                Toast.makeText(MainActivity.this, "No data found!", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            params[0].close();
            return null;
        }
    }
}

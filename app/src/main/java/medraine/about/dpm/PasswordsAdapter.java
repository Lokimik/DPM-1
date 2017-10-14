package medraine.about.dpm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Raine on 04.12.2016.
 */

class PasswordsAdapter extends BaseAdapter {
    private ArrayList<PasswordEntry> entries = new ArrayList<>();
    private LayoutInflater inflater;
    private Context ctx;

    PasswordsAdapter(Context context, ArrayList<PasswordEntry> entries) {
        ctx = context;
        this.entries = entries;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null ) {
            view = inflater.inflate(R.layout.entry, parent, false);
        }

        PasswordEntry pe = getEntry(position);

        new DownloadImageTask((ImageView) view.findViewById(R.id.ivFavicon))
                .execute("http://www.google.com/s2/favicons?domain=www."+pe.getPlace());
        ((TextView) view.findViewById(R.id.tvTitle)).setText(pe.getTitle());
        ((TextView) view.findViewById(R.id.tvUser)).setText(pe.getUsr());
        ((TextView) view.findViewById(R.id.tvPassword)).setText(pe.getPssw());
        ((TextView) view.findViewById(R.id.tvPlace)).setText(pe.getPlace());

        return view;
    }

    private PasswordEntry getEntry(int position){
        return ((PasswordEntry) getItem(position));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView temp;

        DownloadImageTask(ImageView temp) {
            this.temp = temp;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap favicon = null;
            try {
                InputStream is = new URL(url).openStream();
                favicon = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return favicon;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            temp.setImageBitmap(result);
            temp.setScaleX(2);
            temp.setScaleY(2);
        }
    }
}
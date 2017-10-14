package medraine.about.dpm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * Created by Raine on 27.12.2016.
 */

public class EditingDialog extends DialogFragment implements View.OnClickListener {
    private View form = null;
    private Button btnRgn;
    private Button btnEdit;
    private Button btnDelete;
    private int random = 1;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstance) {
        getDialog().setTitle("Entry Tools");
        getDialog().setCanceledOnTouchOutside(true);

        form = layoutInflater.inflate(R.layout.editing, null);

        final int fixed = 21;

        SecureRandom rnd = new SecureRandom(ByteBuffer.allocate(Long.SIZE/Byte.SIZE).putLong(System.currentTimeMillis()*(long)fixed).array());
        random = rnd.nextInt(fixed+4)+fixed-4;

        btnRgn = (Button) form.findViewById(R.id.btnRgn);
        btnRgn.setOnClickListener(this);

        btnEdit = (Button) form.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(this);

        btnDelete = (Button) form.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        return form;
    }

    public void onClick(View v) {
        Bundle bundle = this.getArguments();
        switch(v.getId()) {
            case R.id.btnRgn:
                ((MainActivity)EditingDialog.this.getActivity()).regenerateDataSet(bundle.getInt("POSITION"), random);
                getDialog().cancel();
                break;
            case R.id.btnEdit:
                ((MainActivity)EditingDialog.this.getActivity()).editDataSet(bundle.getInt("POSITION"));
                getDialog().cancel();
                break;
            case R.id.btnDelete:
                ((MainActivity)EditingDialog.this.getActivity()).deleteDataSet(bundle.getInt("POSITION"));
                getDialog().cancel();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}

package medraine.about.dpm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Raine on 14.12.2016.
 */

public class AddingDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private View form = null;
    private  EditText etPassword;
    private EditText etTitle;
    private EditText etUser;
    private EditText etPlace;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        form = getActivity().getLayoutInflater().inflate(R.layout.adding, null);

        etPassword = (EditText) form.findViewById(R.id.etPassword);
        etTitle = (EditText) form.findViewById(R.id.etTitle);
        etUser = (EditText) form.findViewById(R.id.etUser);
        etPlace = (EditText) form.findViewById(R.id.etPlace);
        if(this.getArguments().getBoolean("MULTI")) {
            etTitle.setText(this.getArguments().getString("TITLE"));
            etPassword.setText(this.getArguments().getString("PSSW"));
            etUser.setText(this.getArguments().getString("USER"));
            etPlace.setText(this.getArguments().getString("PLACE"));
        }
        else {
            int fixed = 21;

            SecureRandom rnd = new SecureRandom(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(System.currentTimeMillis() * (long) fixed).array());

            etPassword.setText(new PasswordEntry().reGenerate(rnd.nextInt(fixed + 4) + fixed - 4));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return(builder.setTitle("New Password Entry")
                .setView(form)
                .setPositiveButton("Add", this)
                .setNegativeButton("Cancel", null).create());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(!this.getArguments().getBoolean("MULTI")) {
            ((MainActivity) AddingDialog.this.getActivity()).addDataSet(new PasswordEntry(etTitle.getText().toString(),
                    etPassword.getText().toString(),
                    etUser.getText().toString(),
                    etPlace.getText().toString()));
        }
        else {
            ((MainActivity) AddingDialog.this.getActivity()).getDataSet(this.getArguments().getInt("POSITION"), new PasswordEntry(etTitle.getText().toString(),
                    etPassword.getText().toString(),
                    etUser.getText().toString(),
                    etPlace.getText().toString()));
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

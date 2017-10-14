package medraine.about.dpm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseCorruptException;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences sharedPreferences;
    private String salt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SQLiteDatabase.loadLibs(this);

        sharedPreferences = getSharedPreferences("karas", MODE_PRIVATE);
        String checkout = sharedPreferences.getString("SALT", null);
        if(checkout == null) {
            SharedPreferences.Editor ed= sharedPreferences.edit();
            ed.putString("SALT", new PasswordEntry().reGenerate(24));
            ed.commit();
            //ed.apply();
        }
        else {
            salt = sharedPreferences.getString("SALT", null);
        }

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("Unused Login");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            if(!sharedPreferences.getBoolean("DB", false)) {

                SharedPreferences.Editor ed= sharedPreferences.edit();
                ed.putBoolean("DB", true);
                ed.apply();

                SQLiteDatabase db = PDBHelper.getInstance(this).getWritableDatabase(mEmailView.getText().toString() +
                        mPasswordView.getText().toString());
                Log.d("MAIN" , org.apache.commons.codec.binary.StringUtils.newStringUtf16( org.apache.commons.codec.digest.DigestUtils.sha512(mEmailView.getText().toString() +
                        mPasswordView.getText().toString() + salt)));
                db.close();
            }

            try {
                Log.d(LoginActivity.class.getSimpleName(), "String: " + mEmailView.getText().toString() +
                        mPasswordView.getText().toString());
                Log.d("MAIN" , org.apache.commons.codec.binary.StringUtils.newStringUtf16( org.apache.commons.codec.digest.DigestUtils.sha512(mEmailView.getText().toString() +
                        mPasswordView.getText().toString() + salt)));
                SQLiteDatabase db = PDBHelper.getInstance(this).getWritableDatabase(mEmailView.getText().toString() +
                        mPasswordView.getText().toString());

                /*SQLiteDatabase db = PDBHelper.getInstance(this).getWritableDatabase(new String(org.apache.commons.codec.digest.DigestUtils.sha512(mEmailView.getText().toString() +
                        mPasswordView.getText().toString())));*/

                Intent in = new Intent(this, MainActivity.class);
                in.putExtra("DATA",
                        mEmailView.getText().toString() +
                                mPasswordView.getText().toString());
                db.close();
                Log.d("TAG", "LOGIN!");
                startActivity(in);
            }
            catch(SQLException ex) {
//            catch(SQLiteDatabaseCorruptException ex) {
                Log.d("TAG", "ERROR!");
                Snackbar.make(mLoginFormView, "NO MATCH!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    private boolean isEmailValid(String email) {

        return !email.isEmpty();
        //return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);

        return strength.getCrackTimesDisplay().getOnlineThrottling100perHour().equals("centuries") &&
                strength.getCrackTimesDisplay().getOnlineNoThrottling10perSecond().equals("centuries") &&
                strength.getCrackTimesDisplay().getOfflineSlowHashing1e4perSecond().equals("centuries") &&
                strength.getCrackTimesDisplay().getOfflineFastHashing1e10PerSecond().equals("centuries");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
}


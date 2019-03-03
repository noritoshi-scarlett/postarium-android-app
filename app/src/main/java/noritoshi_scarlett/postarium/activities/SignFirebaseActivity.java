package noritoshi_scarlett.postarium.activities;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonSession;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignFirebaseActivity extends AppCompatActivity {

    public static final String POSTARIUM_USER_SETTINGS = "PostariumSettings";
    public static final String ACCOUNT_EMAIL = "account_email";

    // VIEW'S VARIABLES
    Button btnOk;
    Button btnSignUp;
    Button btnAbout;
    EditText editPassword;
    EditText editEmail;
    ProgressDialog progressDialog;

    //DATA VARIABLES
    JsonSession.Session session;
    // API AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        btnOk = (Button) findViewById(R.id.signOk);
        btnSignUp = (Button) findViewById(R.id.signUp);
        btnAbout = (Button) findViewById(R.id.signAbout);
        editEmail = (EditText) findViewById(R.id.signEmail);
        editPassword = (EditText) findViewById(R.id.signPassword);
        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        // PASSWORD TEXTVIEW
        Typeface typeface = editPassword.getTypeface();
        editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPassword.setTypeface(typeface);
        editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //VIEW SETS
        btnOk.setEnabled(false);

        // LISTENERS
        btnOk.setOnClickListener(new View.OnClickListener() {

            String resultDesc = getResources().getString(R.string.sign_cant_login);
            Boolean resultSuccess = false;

            @Override
            public void onClick(View view) {
                if (isValidForm()) {
                    signIn(editEmail.getText().toString(), editPassword.getText().toString());
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            String resultDesc = getResources().getString(R.string.sign_cant_login);
            Boolean resultSuccess = false;

            @Override
            public void onClick(View v) {
                if (isValidForm()) {
                    signUp(editEmail.getText().toString(), editPassword.getText().toString());
                }
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_postarium_base)));
                //startActivity(intent);
            }
        });

        // ABOUT POSTARIUM
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });

        // TODO -> ista forów w aplikacji

        // Wywołanie metody sprawdzającej, czy oba pola sa uzupełnione
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setEnabledBtnSign();
            }
        });
        // j.w.
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                setEnabledBtnSign();
            }
        });

        // SHARED PREFERENCES -> Pobranie osobistych ustawień aplikacji
        // (zawieraja dane sesji oraz informacje, czy użytkownik jest zalogowany)
        SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
        // Wpisanie adresu email z ustawień i umieszczenie kursora za tekstem
        String email = settings.getString(ACCOUNT_EMAIL, null);
        if (email != null) {
            editEmail.setText(email);
            editEmail.setSelection(editEmail.length());
        }

        // API AUTH
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("SIGN", "onAuthStateChanged:signed_in:" + user.getUid());
                    displayMainActivity();
                } else {
                    // TODO -> Pozwól się zalogować
                    // User is signed out
                    Log.d("SIGN", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // usunięcie okna dialogu, żeby (prawdopodobnie) nie nachodził na Main
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp(String email, String password) {
        setSignInEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SIGN", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast toast = Toast.makeText(SignFirebaseActivity.this, R.string.sign_up_auth_failed,
                                    Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                            setSignInEnabled(true);
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        setSignInEnabled(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SIGN", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("SIGN", "signInWithEmail:failed", task.getException());
                            Toast toast = Toast.makeText(SignFirebaseActivity.this, R.string.sign_in_auth_failed,
                                    Toast.LENGTH_SHORT);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                            setSignInEnabled(true);
                        }
                    }
                });
    }


    protected void setSignInEnabled(boolean enable) {
        btnOk.setEnabled(enable);
        editEmail.setEnabled(enable);
        editPassword.setEnabled(enable);
        if (!enable) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.sign_try_login));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void signInWithPassword(String email, String password) {
        editEmail.setText(email);
        editPassword.setText(password);
        btnOk.setEnabled(true);
        btnOk.callOnClick();
    }

    /**
     * Ustawia przycisk aktywny, jeśli oba pola tekstowe są niepuste
     */
    private void setEnabledBtnSign() {
        btnOk.setEnabled(! (editPassword.getText().toString().equals("") || editEmail.getText().toString().equals("")));
    }

    private boolean isValidForm() {
        // Sprawdzenie technicznej poprawności danych logowania
        // TODO -> czy nalezy cos tu jeszcze dodac (pewnie tak, ale co?)
        boolean formError = true;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
            editEmail.setError(getResources().getString(R.string.sign_email_error));
            formError = false;
        }
        if (editPassword.getText().toString().length() > 20) {
            editPassword.setError(getResources().getString(R.string.sign_pssword_error));
            formError = false;
        }
        return formError;
    }

    /**
     * Próba zalogowania, z obsługą żądania i reakcji UI
     */

//            // Zapytanie POST i jego obsługa
//            RequestBody formBody = new FormBody.Builder()
//                    .add("submit_sign_in", "1")
//                    .add("sign_in_device", "mobile")
//                    .add("sign_in_email", editEmail.getText().toString())
//                    .add("sign_in_password", editPassword.getText().toString() )
//                    .build();
//
//            Postarium.WebRequests.post("", formBody, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(getBaseContext(), getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
//                            progressDialog.dismiss();
//                            btnOk.setEnabled(true);
//                        }
//                    });
//                }
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//
//                        // TODO -> Rzucanie wyjątku przez parser JSONa -> trzeba to obsłużyć
//                        JsonParser parser = new JsonParser();
//                        JsonObject alert = parser.parse(response.body().string()).getAsJsonObject();
//
//                        resultDesc = alert.get("desc").getAsString();
//                        resultSuccess = alert.get("success").getAsBoolean();
//
//                        if (resultSuccess) {
//                            // TODO -> Rzucanie wyjątku przez parser JSONa -> trzeba to obsłużyć
//                            JsonElement jsonSession = alert.get("detail");
//                            JsonSession sessionParser = new JsonSession();
//                            sessionParser.parse(jsonSession);
//
//                            session = sessionParser.getSession();
//
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    progressDialog.setMessage(resultDesc);
//                                }
//                            });
//                            //menuDisplay();
//                            return;
//                        }
//                    }
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(getBaseContext(), resultDesc, Toast.LENGTH_LONG).show();
//                            editPassword.setText("");
//                            progressDialog.dismiss();
//                        }
//                    });
//                }
//            });
//
//        }
//    }

    /**
     * Przejście do głównej aktywności;
     * Ustawienie danych sesji w SharedPreferences
     */
    private void displayMainActivity() {
        if (getUserInformation()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast toast = Toast.makeText(SignFirebaseActivity.this, R.string.sign_in_auth_failed,
                    Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
            view.setPaddingRelative(15, 10, 10, 15);
            toast.show();
            setSignInEnabled(true);
        }
    }
    private Boolean getUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("app_sign_success_any", true)
                    .putString("account_id", uid)
                    .putString("account_name", name)
                    .putString("account_email", email)
                    //.putInt("account_permission", session.getPermission())
                    //.putString("account_lastVisit", session.getLastVisit())
                    //.putBoolean("account_logged", session.isLogged())
                    .apply();
            return true;
        } else {
            return false;
        }
    }

}
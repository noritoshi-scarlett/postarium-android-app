package noritoshi_scarlett.postarium.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import noritoshi_scarlett.postarium.jsonPojo.JsonSession;
import okhttp3.internal.Util;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // STATIC VALUES
    public static final String POSTARIUM_USER_SETTINGS = "PostariumSettings";
    public static final String ACCOUNT_EMAIL = "account_email";
    // VIEW'S VARIABLES
    private ImageView signImageLogo;
    private LinearLayout linearLayout;
    private Button btnOk;
    private Button btnSignUp;
    private Button btnAbout;
    private EditText editPassword;
    private EditText editEmail;
    private ProgressDialog progressDialog;
    // DATA VARIABLES
    private String resultDesc;
    private boolean resultSuccess = false;
    private JsonSession.Session session;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving;
    private static final int RC_SAVE = 1;
    private static final int RC_READ = 3;

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        // VIEW PREPARE -> SIGN IN / SIGN UP
        resultDesc = getResources().getString(R.string.sign_cant_login);
        signImageLogo = (ImageView) findViewById(R.id.signImageLogo);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        editEmail = (EditText) findViewById(R.id.signEmail);
        editPassword = (EditText) findViewById(R.id.signPassword);
        // PASSWORD TEXTVIEW
        Typeface typeface = editPassword.getTypeface();
        editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPassword.setTypeface(typeface);
        editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        progressDialog = new ProgressDialog(this, R.style.AppTheme_ProgressDialog);
        btnAbout = (Button) findViewById(R.id.signAbout);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp = (Button) findViewById(R.id.signUp);
        // TODO -> rejestracja w aplikacji
        // TODO -> ista forów w aplikacji
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.url_postarium_base)));
                startActivity(intent);
            }
        });
        btnOk = (Button) findViewById(R.id.signOk);
        signImageLogo.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
        btnOk.setOnClickListener(new ButtonSign());
        // SHARED PREFERENCES -> Wpisanie adresu email z ustawień i umieszczenie kursora za tekstem
        SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
        String email = settings.getString(ACCOUNT_EMAIL, null);
        if (email != null) {
            editEmail.setText(email);
            editEmail.setSelection(editEmail.length());
        }
        // GOOGLE API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Auth", "onConnected");
        requestCredentials();
    }
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("Auth", "onConnectionSuspended: " + cause);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Auth", "onConnectionFailed: " + connectionResult);
        connectProblemDialog();
    }

    /**
     * Sprawdzenie technicznej poprawności danych logowania
     * @return isValid
     */
    private boolean isValidForm() {
        // TODO -> czy nalezy cos tu jeszcze dodac (pewnie tak, ale co?)
        boolean isValid = true;
        if (editEmail.getText().toString().equals("")) {
            isValid = false; editEmail.setError(getResources().getString(R.string.sign_email_null)); }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
            isValid = false; editEmail.setError(getResources().getString(R.string.sign_email_error)); }
        if (editPassword.getText().toString().equals("")) {
            isValid = false; editPassword.setError(getResources().getString(R.string.sign_pssword_null)); }
        if (editPassword.getText().toString().length() > 20) {
            isValid = false; editPassword.setError(getResources().getString(R.string.sign_pssword_error));
        }
        return isValid;
    }

    // Check Credential
    private void requestCredentials() {
        CredentialRequest request = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();
        Auth.CredentialsApi.request(mGoogleApiClient, request).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
                        Status status = credentialRequestResult.getStatus();
                        if (credentialRequestResult.getStatus().isSuccess()) { // AUTO Sign-in
                            Credential credential = credentialRequestResult.getCredential();
                            processRetrievedCredential(credential);
                            Log.d("Auth", "Auto sign in");
                        } else if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) { // CHOOSE ACCOUNT
                            Log.d("Auth", "Choose account");
                            resolveResult(status, RC_READ);
                        } else if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) { // WYLOGWANY lub BRAK CREDENTÓW
                            Log.d("Auth", "Sign in required");
                            btnOk.setEnabled(true);
                            signImageLogo.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        } else { // INNY PRZYPADEK
                            Log.w("Auth", "Unrecognized status code: " + status.getStatusCode());
                            btnOk.setEnabled(true);
                            signImageLogo.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );
    }

    /* AUTO SIGN-IN */
    // Use Credential
    private void processRetrievedCredential(Credential credential) {
        String accountType = credential.getAccountType();
        if(accountType == null) {
            signInWithPassword(credential.getId(), credential.getPassword());
        }
        else if (accountType.equals(IdentityProviders.GOOGLE)) {
            GoogleSignInOptions gso =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .setAccountName(credential.getId())
                    .build();
            OptionalPendingResult opr =
                    Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            //...
        }
    }

    /**
     * Automatyczna procedura logowania
     * @param email email
     * @param password hasło
     */
    private void signInWithPassword(String email, String password) {
        editEmail.setText(email);
        editPassword.setText(password);
        btnOk.setEnabled(true);
        btnOk.callOnClick();
    }

    /* SELECT ACCOUNT */
    // send login(?)
    private void resolveResult(Status status, int requestCode) {
        if (mIsResolving) {
            Log.w("Auth", "resolveResult: already resolving.");
            return;
        }
        Log.d("Auth", "Resolving: " + status);
        if (status.hasResolution()) {
            Log.d("Auth", "STATUS: RESOLVING");
            try {
                status.startResolutionForResult(this, requestCode);
                mIsResolving = true;
            } catch (IntentSender.SendIntentException e) {
                Log.e("Auth", "STATUS: Failed to send resolution.", e);
                connectProblemDialog();
            }
        } else {
            Log.e("Auth", "STATUS: FAIL");
            if (requestCode == RC_SAVE) {
                menuDisplay();
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Auth", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                processRetrievedCredential(credential);
            } else {
                Log.e("Auth", "Credential Read: NOT OK");
                setSignInEnabled(true);
            }
        } else if (requestCode == RC_SAVE) {
            Log.d("Auth", "Result code: " + resultCode);
            if (resultCode == RESULT_OK) {
                Log.d("Auth", "Credential Save: OK");
            } else {
                Log.e("Auth", "Credential Save Failed");
            }
            menuDisplay();
        }
        mIsResolving = false;
    }

    /**
     * Właczanie / wyłącznie panelu logowania
     * @param enable Enabled?
     */
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

    /**
     * Próba zalogowania, z obsługą żądania i reakcji UI
     */
    private class ButtonSign implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (! isValidForm()) { return; }

            // Reakcje UI
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.sign_try_login));
            progressDialog.show();
            btnOk.setEnabled(false);

            // Zapytanie POST i jego obsługa
            RequestBody formBody = new FormBody.Builder()
                    .add("submit_sign_in", "1")
                    .add("sign_in_device", "mobile")
                    .add("sign_in_email", editEmail.getText().toString())
                    .add("sign_in_password", editPassword.getText().toString() )
                    .build();

            Postarium.WebRequests.post("", formBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            connectProblemDialog();
                            btnOk.setEnabled(true);
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // TODO -> Rzucanie wyjątku przez parser JSONa -> trzeba to obsłużyć
                        JsonParser parser = new JsonParser();
                        JsonObject alert = parser.parse(response.body().string()).getAsJsonObject();
                        resultDesc = alert.get("desc").getAsString();
                        resultSuccess = alert.get("success").getAsBoolean();

                        if (resultSuccess) {
                            // TODO -> Rzucanie wyjątku przez parser JSONa -> trzeba to obsłużyć
                            JsonElement jsonSession = alert.get("detail");
                            JsonSession sessionParser = new JsonSession();
                            sessionParser.parse(jsonSession);

                            session = sessionParser.getSession();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.setMessage(resultDesc);
                                }
                            });
                            Credential credential = new Credential.Builder(editEmail.getText().toString())
                                    .setPassword(editPassword.getText().toString())
                                    .build();
                            saveCredential(credential);
                            return;
                        }
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toast = Toast.makeText(getBaseContext(), resultDesc, Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                            editPassword.setText("");
                            btnOk.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    });
                }
            });

        }
    }

    // Credential is valid, so save it.
    protected void saveCredential(Credential credential) {
        Auth.CredentialsApi.save(mGoogleApiClient,
                credential).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    menuDisplay();
                    Log.d("Auth", "Credential saved");
                } else {
                    Log.d("Auth", "Attempt to save credential failed " +
                            status.getStatusMessage() + " " +
                            status.getStatusCode());
                    resolveResult(status, RC_SAVE);
                }
            }
        });
    }
    /**
     * Przejście do głównej aktywności;
     * Ustawienie danych sesji w SharedPreferences
     */
    private void menuDisplay() {
        SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("app_sign_success_any", true)
                .putInt("account_id", session.getUserId())
                .putString("account_name", session.getName())
                .putString("account_email", session.getEmail())
                .putInt("account_permission", session.getPermission())
                .putString("account_lastVisit", session.getLastVisit())
                .putBoolean("account_logged", session.isLogged())
                .apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * CONNECT ERROR -> Pozwala nawiązac tryb pracy offline lub wyjść z aplikacji
     */
    private void connectProblemDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);

        builder.setMessage(R.string.offline_desc)
                .setTitle(R.string.offline_title);

        builder.setPositiveButton(R.string.offline_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.offline_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                finish();
            }
        });
        builder.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Usunięcie okna dialogu -> prawdopodobnie przy przechodzeniu do nowej aktywnosci, ale czy na pewno?
    @Override
    protected void onStop() {
        progressDialog.dismiss();
        super.onStop();
    }
}

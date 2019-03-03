package noritoshi_scarlett.postarium.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import noritoshi_scarlett.postarium.jsonPojo.JsonAlert;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ScreenActivity extends AppCompatActivity {

    public static final String POSTARIUM_USER_SETTINGS = "PostariumSettings";
    public static final String APP_SIGN_SUCCESS_ANY = "app_sign_success_any";
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hideMenu();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_screen);

        // SHARED PREFERENCES -> Pobranie osobistych ustawień aplikacji
        // (zawierają dane sesji oraz informacje, czy użytkownik jest zalogowany)
        SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
        // Czy kiedykolwiek nastąpiło już pełne zalogownaie
        boolean isAnySignSuccess = settings.getBoolean(APP_SIGN_SUCCESS_ANY, false);

        if (! isAnySignSuccess)
        {
            //jeśli nie, wyświetl powitalną wiadomość
            viewFirstMsg();
        }
        else
        {
            // jeśli tak, spróbuj się połączyć
            RequestBody formBody = new FormBody.Builder()
                    .add("sign_in_device", "mobile")
                    .build();

            Postarium.WebRequests.post("", formBody, new Callback() {
                // OFFLINE (telefon ma wyłączone łączenie z siecią)
                // (TODO - zmienić na możliwosć ponowienia połączenia)
                // Możliwy tryb pracy offline
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            connectProblemDialog();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // ONLINE
                        // ekran logowania lub menu główne
                        JsonAlert.Alert alert = new JsonAlert(response.body().string()).getAlert();
                        boolean isLogged = alert.getSuccess();
                        Postarium.getPostariData().setLogged(isLogged);

                        Intent intent;
                        if (isLogged) {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), SignActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        // OFFLINE (błędy w połączeniu z serwerem; serwer nie odpowiada)
                        // Możliwy tryb pracy offline
                        runOnUiThread(new Runnable() {
                            public void run() {connectProblemDialog();
                            }
                        });

                    }
                }
            });
        }

    }

    /**
     * FIRST RUN -> komunikat powitalno-informacyjny, można przejść tylko do logowania.
     */
    private void viewFirstMsg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);

        builder.setMessage(R.string.firstmsg_desc)
                .setTitle(R.string.firstmsg_title);

        builder.setPositiveButton(R.string.firstmsg_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    /**
     * Chowa z opóźnieniem status i navigation bar, ale chyba zbędne skoro ich nie ma
     * @param savedInstanceState stan aktywności
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    // TODO -> Czy jest to potrzebne? Chyba nie, ale trzeba przetestować stare urzadzenia
    private void hideMenu() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

package noritoshi_scarlett.postarium.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CharProfileActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    // VIEW'S VARIABLES
    private WebView webView;
    private ProgressBar progressBar;
    private JavaScriptInterface JSCharInterface;

    // DATA VARIABLES
    private int char_id = 0;
    private String char_login = "";
    private int forum_id = 0;
    private String url;

    private boolean firstBackPressed = false;

    // TODO -> Toolbar z dodatkowymi akcjami typu przeglądanie avatarów, zapisane wpisy
    // (możliwośc kopiowania zaznaczonego tekstu z pomocą przycisku w jsie i wynik przechodzi do activity, która go wkleja w sharedpref.

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // TODO -> Przerobić na fragment
    // TODO -> Dodać lepsze komunikaty zamiast Toastów
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView =findViewById(R.id.webCharProfile);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(1);

        // POBRANIE DANYCH Z INTENCJI
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            char_id = extras.getInt("char_id", 0);
            char_login = extras.getString("char_login", "");
            forum_id = extras.getInt("forum_id", 0);
            if (getSupportActionBar() != null) {
                // ikona strzałki na toolbarze
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // WEB VIEW -> Settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        JSCharInterface = new JavaScriptInterface(this);
        webView.addJavascriptInterface(JSCharInterface, "JSCharInterface");
        webView.setWebViewClient(new WebViewClient() {

            // Wyłączenie przeładowywania strony (????)
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                return false;
            }
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // PROGRESS BAR
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        // LINK -> Do profilu postaci
        url = getResources().getString(R.string.url_postarium_base)
                + "characters/profile/"
                + Integer.toString(char_id) + "/"
                + Integer.toString(forum_id) + ".html";

        webView.loadUrl(url);
    }

    /**
     * Utworzenie menu na toolbarze
     * @param menu utworzone menu
     * @return isSuccess?
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.title_activity_char_profile) + ": " + char_login);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    @Override
    public void onBackPressed() {
        if (! firstBackPressed) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.circle_char_second_touch_to_close), Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
            view.setPaddingRelative(15, 10, 10, 15);
            toast.show();
            firstBackPressed = true;
        } else {
            super.onBackPressed();
        }
    }

    private class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /**
         * Wyświetlenie komunikatu o wysyłaniu formularza
         */
        @JavascriptInterface
        public void showMsg() {
            Toast toast = Toast.makeText(mContext, "Trwa wysyłanie formularza...", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
            view.setPaddingRelative(15, 10, 10, 15);
            toast.show();
        }
    }
}

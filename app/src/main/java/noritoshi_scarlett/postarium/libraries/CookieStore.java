package noritoshi_scarlett.postarium.libraries;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieStore implements CookieJar {

    private CookieManager webviewCookieManager = CookieManager.getInstance();

    private final Set<Cookie> cookieStore = new HashSet<>();
    private SharedPreferences cookiesStorage;
    private Context context;

    public Set<Cookie> getCookieStore() {return cookieStore;}

    // TODO - przycisk do czyszczenia ciasteczek

    public CookieStore(Context context) {
        this.context = context;
        cookiesStorage = context.getSharedPreferences("PostariumCookies", 0);
        boolean isStorage = cookiesStorage.getBoolean("pbf_session", false);
        if (isStorage)
        {
            Cookie cookieStored = new Cookie.Builder()
                    .expiresAt(cookiesStorage.getLong("pbf_session-Expires", 0))
                    .path(cookiesStorage.getString("pbf_session-Path", "/"))
                    .domain(cookiesStorage.getString("pbf_session-Domain", ""))
                    .name(cookiesStorage.getString("pbf_session-Name", ""))
                    .value(cookiesStorage.getString("pbf_session-Value", ""))
                    .build();
            cookieStore.add(cookieStored);
        }
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            /*
             * Saves cookies from HTTP response
             * If the response includes a trailer this method is called second time
             */
        //Save cookies to the store
        cookieStore.addAll(cookies);

        if (cookieStore.size() > 0) {
            for (Cookie cookie : cookieStore) {
                if (cookie.expiresAt() >= System.currentTimeMillis()) {
                    if (cookie.name().equals("pbf_session")) {
                        SharedPreferences.Editor editor = cookiesStorage.edit();
                        editor.putBoolean("pbf_session", true)
                                .putLong("pbf_session-Expires", cookie.expiresAt())
                                .putInt("pbf_session-Hash", cookie.hashCode())
                                .putString("pbf_session-Path", cookie.path())
                                .putString("pbf_session-Domain", cookie.domain())
                                .putString("pbf_session-Name", cookie.name())
                                .putString("pbf_session-Value", cookie.value())
                                .apply();
                    }
                }
            }
        }

        String urlString = url.toString();
        for (Cookie cookie : cookies) {
            webviewCookieManager.setCookie(urlString, cookie.toString());
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
            /*
             * Load cookies from the jar for an HTTP request.
             * This method returns cookies that have not yet expired
             */
        List<Cookie> validCookies = new ArrayList<>();
        for (Cookie cookie : cookieStore) {
            if (cookie.expiresAt() >= System.currentTimeMillis()) {
                validCookies.add(cookie);
            }
        }
        return validCookies;
    }


}
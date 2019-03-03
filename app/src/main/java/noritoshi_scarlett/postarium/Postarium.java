package noritoshi_scarlett.postarium;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import noritoshi_scarlett.postarium.libraries.PostariData;
import noritoshi_scarlett.postarium.services.PostariumService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import noritoshi_scarlett.postarium.libraries.CookieStore;
import noritoshi_scarlett.postarium.libraries.JsonDataStorage;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Postarium extends Application {

    public static final String DEFAULT_FONT_ASSET_PATH = "fonts/Dosis-Light.ttf";

    private static Postarium instance;

    private static OkHttpClient client;
    private static WebRequests webRequests;
    private static PostariData postariData;
    private static JsonDataStorage jsonDataStorage;

    @Override
    public void onCreate() {
        super.onCreate();

        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(DEFAULT_FONT_ASSET_PATH)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        instance = this;
        initModels();
        PostariumService.startActionLoadFromFiles(this);

        // https://yakivmospan.wordpress.com/2014/04/17/best-practice-application/
        registerComponentCallbacks(new ComponentCallbacks2() {
            @Override
            public void onConfigurationChanged(Configuration configuration) {
                // determinate Configuration Change
            }

            @Override
            public void onLowMemory() {
                // use it only for older API version (<14)
                getJsonDataStorage().saveToFile();
            }

            @Override
            public void onTrimMemory(int level) {
                // Called when the operating system has determined that it is a good
                // time for a process to trim unneeded memory from its process
                if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
                    // app in background
                    getJsonDataStorage().saveToFile();
                }
                if(level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE){
                    //app was closed
                    getJsonDataStorage().saveToFile();
                }
            }
        });

    }

    public static Postarium getInstance() {
        if (instance == null) {instance = new Postarium();}
        return instance; }
    public static OkHttpClient getClient() {
        return client;
    }
    public static PostariData getPostariData() {return postariData;}
    public static JsonDataStorage getJsonDataStorage() {
        return jsonDataStorage;
    }

    private void initModels() {
        // Ustawienie HTTPS
        List<ConnectionSpec> spec = Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS);

        // Ustawienie długości oczekwania na odpowiedź
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieStore(getApplicationContext()))
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                //.connectionSpecs(spec)
                .build();
        // init POSTs and GETs
        webRequests = new WebRequests();
        // init storages
        postariData = new PostariData();
        jsonDataStorage = new JsonDataStorage(getApplicationContext());
    }

    public static class WebRequests {

        // TODO -> przenieśc enqueue(callback) do miejsca wywoływania, a tutejszy callback zbędny wtedy jest
        /**
         * POST HTTP request to Postarium
         * @param url   is always defined with url_postarium_base, use null for nothing more
         * @param formBody  form
         * @param callback  use for response
         * @return          return call
         */
        public static Call post(String url, RequestBody formBody, Callback callback) {
            Request request = new Request.Builder()
                    .url(Postarium.getInstance().getResources().getString(R.string.url_postarium_base) + url)
                    .addHeader("User-Agent", "Postarium App / " + System.getProperty("http.agent"))
                    .post(formBody)
                    .build();
            Call call = Postarium.getClient().newCall(request);
            call.enqueue(callback);
            return call;
        }

        /**
         * GET HTTP request to Postarium
         * @param url       is always defined with url_postarium_base, use null for nothing more
         * @param callback use for response
         * @return          return call
         */
        public static Call get(String url, Callback callback) {
            Request request = new Request.Builder()
                    .url(Postarium.getInstance().getResources().getString(R.string.url_postarium_base) + url)
                    .addHeader("User-Agent", "Postarium App / " + System.getProperty("http.agent"))
                    .build();
            Call call = Postarium.getClient().newCall(request);
            call.enqueue(callback);
            return call;
        }
    }
}

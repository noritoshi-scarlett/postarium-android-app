package noritoshi_scarlett.postarium.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import noritoshi_scarlett.postarium.activities.ForumActivity;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ForumPostsMsgsAdapter;

public class ForumPostsMsgsFragment extends Fragment {

    private ForumPostsMsgsAdapter adapter;

    private boolean isTablet;
    private boolean isViewingData;
    private String viewingData;

    private Context context;

    private RecyclerView listForum;

    public ForumPostsMsgsFragment() {}

    public interface GoToForumWebViewListener {
        void onSendTopicSite(String name, String login, String url);
    }

    public interface ForumPostsMsgsClickListener {
        void onClickSiteTopic(String name, String login, String url);
        void onClickPW(String url, String href);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forum_postsmsgs, container, false);
        context = getActivity().getApplicationContext();
        listForum = (RecyclerView) v.findViewById(R.id.listForum);

        if (savedInstanceState != null) {
            viewingData = savedInstanceState.getString("viewingData");
            isViewingData = savedInstanceState.getBoolean("isViewingData");
        }
        else {
            viewingData = null;
            isViewingData = false;
        }

        if (viewingData != null && isViewingData) {
            dialogPreviewSite(viewingData);
        }

        return  v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("viewingData", viewingData);
        outState.putBoolean("isViewingData", isViewingData);
    }

    public void changePostsList(ArrayList<?> forumPostsMsgs, int objectType) {
        adapter = new ForumPostsMsgsAdapter(forumPostsMsgs, objectType, new ForumPostsMsgsClickListener(){
            @Override
            public void onClickSiteTopic(String name, String login, String url) {
                if (getActivity() != null){
                    ((ForumActivity) getActivity()).goToWebView(
                            name,
                            login,
                            context.getResources().getString(R.string.url_postarium_base) + url);
                }
            }

            @Override
            public void onClickPW(String url,  String href){
                previewMsg(url, href);
            }
        });

        if (getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listForum.setAdapter(adapter);
                    final int columns = getActivity().getResources().getInteger(R.integer.forum_posts_record_columns);
                    listForum.setLayoutManager(new GridLayoutManager(getActivity(), columns));
                }
            });
        }
    }

    /**
     * podgląd wybranej wiadomości
     * @param url adres w postarium
     * @param href adres wiadomosci
     */
    private void previewMsg(final String url, final String href) {

        ((ForumActivity) getActivity()).progressBar.setVisibility(View.VISIBLE);

        RequestBody formBody = new FormBody.Builder()
                .add("msg_url", Postarium.getPostariData().getPickedForum().getForum_url() + href)
                .add("submit_char_login", "" )
                .build();

        Postarium.WebRequests.post(url, formBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    boolean haveError = false;
                    String errorMsg = ForumPostsMsgsFragment.class.getSimpleName() + "\n\n" + url + "\n" + href;
                    try {
                        JsonParser parser = new JsonParser();
                        JsonObject jMsg = parser.parse(response.body().string()).getAsJsonObject();
                        final String jResponse = jMsg.get("msg_desc").getAsString();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                dialogPreviewSite(jResponse);
                                ((ForumActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    } catch(JsonSyntaxException e) {
                        haveError = true;
                        errorMsg += "\n\nOdebrano złą wiadomość o.O\n\n" + e.getMessage();
                    } catch(JsonParseException e) {
                        haveError = true;
                        errorMsg += "\n\nW przysłanych danych znajdują się błędy ;c.\n\n" + e.getMessage();
                    }
                    if (haveError) {
                        final String finalErrorMsg = errorMsg;
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ((ForumActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                                dialog.setMessage(finalErrorMsg).show();
                                // TODO -> dopisek małym druczkiem: zrób printscreena i prześlij administracji.
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * okno dialogowe z podglądem watku lub wiadomości
     * @param data  dane HTML
     */
    private void dialogPreviewSite(String data) {
        isViewingData = true;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogThemeFullScreen);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        builder.setTitle("Podgląd wątku:");
        View view = (inflater.inflate(R.layout.alert_site_preview, null));
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setProgress(1);
        WebView web = view.findViewById(R.id.webSitePreview);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web.setBackgroundColor(Color.TRANSPARENT);
        web.requestFocus(View.FOCUS_DOWN);
        web.setWebViewClient(new WebViewClient() {
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
        web.setWebChromeClient(new WebChromeClient() {
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

        web.loadUrl(data);
        builder.setView(view);
        builder.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isViewingData = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //TODO ZMIENIC FUNKCJE JS!!!!

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void changeForm(String formData)
        {

        }

        @JavascriptInterface
        public void showMsg() {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Trwa wysyłanie formularza...", Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.backgroundBlueTransparent));
            view.setPaddingRelative(15, 10, 10, 15);
            toast.show();
        }
    }
}

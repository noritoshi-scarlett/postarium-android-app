package noritoshi_scarlett.postarium.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ForumWebViewAdapter;
import noritoshi_scarlett.postarium.adapters.WriterToolbarAdapter;

public class ForumWriterFragment extends Fragment {

    private boolean isViewingData;
    private String viewingData;

    private WriterToolbarAdapter adapter;

    private WebView webView;
    private RecyclerView toolbarEditRecyclerView;
    private Context context;


    public ForumWriterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forum_writer, container, false);

        context = getContext();

        toolbarEditRecyclerView = v.findViewById(R.id.toolbarEditRecyclerView);
        webView = v.findViewById(R.id.webTextPreview);

        prepareWebView();
        prepareToolbar();

        webView.loadUrl("file:///android_asset/writer_input.html");

        if (savedInstanceState != null) {
            viewingData = savedInstanceState.getString("viewingData");
            isViewingData = savedInstanceState.getBoolean("isViewingData");
        } else {
            viewingData = null;
            isViewingData = false;
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("viewingData", viewingData);
        outState.putBoolean("isViewingData", isViewingData);
    }

    /**
     * podglad wątku
     * @param url adres wątku w postarium
     */
    public void onChangeText(String name, String login, String url) {

        if (webView != null) {

            webView.loadUrl(url);


            if (adapter == null) {

            } else {

            }
        }
    }

    private void prepareToolbar() {
        toolbarEditRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapter = new WriterToolbarAdapter(context);
        toolbarEditRecyclerView.setAdapter(adapter);
    }

    private void prepareWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
            // Wyłączenie przeładowywania strony (????)
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url){
                return false;
            }
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request){
                return false;
            }
        });
    }
}

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
import android.widget.ProgressBar;
import android.widget.TextView;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ForumWebViewAdapter;

public class ForumWebViewFragment extends Fragment implements
        ForumPostsMsgsFragment.GoToForumWebViewListener,
        ForumWebViewAdapter.ForumWebViewTabClickListener {

    private boolean isViewingData;
    private String viewingData;

    private SparseArray<String> record;
    private ForumWebViewAdapter adapter;

    private WebView webView;
    private RecyclerView webSitesRecyclerView;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private Context context;


    public ForumWebViewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forum_webview, container, false);

        context = getContext();

        progressBar = v.findViewById(R.id.progressBar);
        webSitesRecyclerView = v.findViewById(R.id.webSitesRecyclerView);
        webView = v.findViewById(R.id.webSitePreview);
        textViewEmpty = v.findViewById(R.id.webSiteEmpty);

        prepareWebView();
        emptyList();
        record = new SparseArray<>(2);

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
     * Metoa interfejsu wywolywana przez Adapter
     * @param url adres strony przechowywany przez Adapter
     */
    @Override
    public void onBtnTabClick(String url) {
        if (webView != null) {
            isViewingData = true;
            webView.loadUrl(url);
        }
    }

    /**
     * Metoa interfejsu wywolywana przez Adapter
     */
    @Override
    public void emptyList() {
        webView.loadUrl("about:blank");
        webView.setVisibility(View.GONE);
        webSitesRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * podglad wątku
     * @param url adres wątku w postarium
     */
    public void onSendTopicSite(String name, String login, String url) {

        if (webView != null) {

            isViewingData = true;

            if (webSitesRecyclerView.getVisibility() != View.VISIBLE) {
                webSitesRecyclerView.setVisibility(View.VISIBLE);
            }
            if (webView.getVisibility() != View.VISIBLE) {
                webView.setVisibility(View.VISIBLE);
            }
            textViewEmpty.setVisibility(View.GONE);

            webView.loadUrl(url);
            record = new SparseArray<>(3);
            record.put(0, name);
            record.put(1, login);
            record.put(2, url);

            if (adapter == null) {
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
                itemDecoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider_horizontal_red));
                webSitesRecyclerView.setLayoutManager(layoutManager);
                webSitesRecyclerView.addItemDecoration(itemDecoration);
                adapter = new ForumWebViewAdapter(record, this);
                webSitesRecyclerView.setAdapter(adapter);
                adapter.selectBtnTab(0);
            } else {
                if (! adapter.findBtnWithTab(name)) {
                    adapter.addBtnTab(record);
                    webSitesRecyclerView.smoothScrollToPosition(webSitesRecyclerView.getAdapter().getItemCount() - 1);
                }
            }
        }
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
        webView.setWebChromeClient(new WebChromeClient() {
            // PROGRESS BAR
            @Override
            public void onProgressChanged (WebView view,int progress){
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
    }
}

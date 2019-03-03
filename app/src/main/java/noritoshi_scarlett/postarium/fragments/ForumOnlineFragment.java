package noritoshi_scarlett.postarium.fragments;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import noritoshi_scarlett.postarium.adapters.ForumNewsAdapter;
import noritoshi_scarlett.postarium.adapters.ForumOnlineAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumOnline;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumProfile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.activities.ForumActivity;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;

public class ForumOnlineFragment extends Fragment implements ForumOnlineAdapter.ViewProfileListener {

    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    private ForumOnlineAdapter adapter;
    private ArrayList<JsonForumOnline.User> usersList;


    public ForumOnlineFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_forum_online, container, false);

        context = getActivity().getApplicationContext();
        recyclerView = v.findViewById(R.id.onlineRecyclerView);
        swipeLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.backgroundRed));
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context, android.R.color.holo_blue_light),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_purple));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOnline();
            }
        });

        getOnline();

        return v;
    }

    public void getOnline() {

        if (! swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(true);
        }

        Postarium.WebRequests.get("forum/viewOnline/" + Postarium.getPostariData().getPickedForum().getForum_name() + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (swipeLayout.isRefreshing()) {
                            swipeLayout.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jResponse = response.body().string();

                    JsonForumOnline onlineParser = new JsonForumOnline(jResponse);
                    usersList = onlineParser.getUsersOnline().getUsersList();

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            adapter = new ForumOnlineAdapter(ForumOnlineFragment.this, context, usersList);
                            recyclerView.setAdapter(adapter);
                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }
                        }
                    });
                }
            }
        });

    }


    @Override
    public void onButtonProfileClick(final String name, String url) {

        if (getActivity() != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            builder.setTitle(name);

            View view = null;
            if (inflater != null) {
                view = (inflater.inflate(R.layout.alert_site_preview, null));
            }
            if (view != null) {
                builder.setView(view);
                final ProgressBar progressBar = view.findViewById(R.id.progressBar);
                WebView webView = view.findViewById(R.id.webSitePreview);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.setWebViewClient(new WebViewClient() {
                    // Wyłączenie przeładowywania strony (????)
                    @SuppressWarnings("deprecation")
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
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

                String requestUrl = context.getResources().getString(R.string.url_postarium_base)
                        + "forum/viewProfile/"
                        + Postarium.getPostariData().getPickedForum().getForum_name()
                        + "/"
                        + Postarium.getPostariData().getChar_id()
                        + ".html";

                String postData = null;
                try {
                    postData = "profile_url=" + URLEncoder.encode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (postData != null) {
                    webView.postUrl(requestUrl, postData.getBytes());
                }

                builder.setPositiveButton(getResources().getString(R.string.main_dialog_close_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            dialog.show();
                        }
                    });
                }
            }
        }
    }
}

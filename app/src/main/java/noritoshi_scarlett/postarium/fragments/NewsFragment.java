package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.NewsAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonNews;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private NewsAdapter adapter;
    private JsonNews.News newsAll;

    private SwipeRefreshLayout swipeLayout;
    private RecyclerView newsList;

    public NewsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getActivity().getApplicationContext();

        View v = inflater.inflate(R.layout.fragment_news, container, false);

        newsList = (RecyclerView)v.findViewById(R.id.newsList);

        // ODŚWIEŻANIE LISTY
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.backgroundRed));
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context, android.R.color.holo_blue_bright),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_red_light));


        newsAll = Postarium.getJsonDataStorage().getNews();
        if (newsAll != null) {
            changeNewsList(context, newsAll);
        }
        else {
            swipeLayout.setRefreshing(true);
            pickNews(context);
        }

        return v;
    }

    @Override
    public void onRefresh() {
        pickNews(getActivity().getApplicationContext());
    }

    public void pickNews(final Context context) {

        Postarium.WebRequests.get("index/getNews.html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (swipeLayout.isRefreshing()) {
                                swipeLayout.setRefreshing(false);
                            }
                            Toast toast = Toast.makeText(context, R.string.request_fail, Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                        }
                    });
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    JsonParser parser = new JsonParser();
                    String responseOnline = response.body().string();
                    JsonObject jResponse = parser.parse(responseOnline).getAsJsonObject();

                    boolean resultSuccess = jResponse.get("success").getAsBoolean();
                    if (resultSuccess) {
                        JsonElement jsonNews = jResponse.get("news").getAsJsonArray();
                        JsonNews newsParser = new JsonNews();
                        JsonObject jElement = new JsonObject();
                        jElement.add("news", jsonNews);
                        newsParser.parse(jElement);
                        newsAll = newsParser.getNews();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    changeNewsList(context, newsAll);
                                }
                            });
                        }
                    }
                    else
                    {
                        final String resultDesc = jResponse.get("desc").getAsString();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(context, resultDesc, Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                                    view.setPaddingRelative(15, 10, 10, 15);
                                    toast.show();
                                    if (swipeLayout.isRefreshing()) {
                                        swipeLayout.setRefreshing(false);
                                    }

                                }
                            });
                        }
                    }
                } else {
                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    private void changeNewsList(Context context, JsonNews.News news) {
        adapter = new NewsAdapter(context, news);
        RecyclerView.LayoutManager mLinearManager = new LinearLayoutManager(context);
        newsList.setLayoutManager(mLinearManager);
        newsList.setItemAnimator(new DefaultItemAnimator());
        newsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                swipeLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });
        newsList.setAdapter(adapter);
        if (swipeLayout.isRefreshing()) {
            // TODO - załadowano dane - toast
            swipeLayout.setRefreshing(false);
        }
    }

}

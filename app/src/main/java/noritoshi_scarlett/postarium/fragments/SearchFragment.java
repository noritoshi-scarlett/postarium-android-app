package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.activities.MainActivity;
import noritoshi_scarlett.postarium.adapters.SearchAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import noritoshi_scarlett.postarium.jsonPojo.JsonSearchResults;
import noritoshi_scarlett.postarium.services.UserProfileService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class SearchFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        SearchAdapter.UserProfileDisplayInterface {

    private static final int NOT_SELECTED_FROM_ACTIVITY_LEVEL = -100;
    private SwipeRefreshLayout swipeLayout;
    private RecyclerView resultsList;

    private Context context;
    private SearchAdapter adapter;

    private int itemNumber = NOT_SELECTED_FROM_ACTIVITY_LEVEL;
    private String keyWords = "";
    private TextView textEmpty;


    public interface SpinnerForSearchEngineInterface {
        AppCompatSpinner getSpinnerSearch();
        void showLayoutSearchEngine();
        void hideLayoutSearchEngine();
    }

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        context = getActivity().getApplicationContext();

        textEmpty = (TextView) view.findViewById(R.id.textEmpty);
        resultsList = (RecyclerView) view.findViewById(R.id.resultsList);
        textEmpty.setVisibility(View.GONE);
        resultsList.setVisibility(View.VISIBLE);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).showLayoutSearchEngine();
        }
        // ODŚWIEŻANIE LISTY
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.backgroundRed));
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context, android.R.color.holo_blue_bright),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_red_light));
        resultsList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                if(resultsList.getAdapter() != null) {
                    resultsList.getAdapter().notifyDataSetChanged();
                }
            }
        });

        // zapisane w State (odtwarzane)
        if (savedInstanceState != null) {
            keyWords = savedInstanceState.getString("search_key_words", "");
            itemNumber = savedInstanceState.getInt("search_key_words", NOT_SELECTED_FROM_ACTIVITY_LEVEL);
            // jest query z Activity -> szukaj
            if (! keyWords.equals("")) {
                pickResults(context, NOT_SELECTED_FROM_ACTIVITY_LEVEL);
            }
        }
        // próba pobrania z argumentów
        if (keyWords.equals("")) {
            keyWords = this.getArguments().getString("search_key_words", "");
            // jest query z Activity -> szukaj
            if (! keyWords.equals("")) {
                pickResults(context, NOT_SELECTED_FROM_ACTIVITY_LEVEL);
            }
        }
        if (itemNumber == NOT_SELECTED_FROM_ACTIVITY_LEVEL) {
            itemNumber = this.getArguments().getInt("item_number", NOT_SELECTED_FROM_ACTIVITY_LEVEL);
        }
        // jest numer forum z Activity -> pokaż userów
        if (itemNumber != NOT_SELECTED_FROM_ACTIVITY_LEVEL) {
            pickResults(context, itemNumber);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("search_key_words", keyWords);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideLayoutSearchEngine();
        }
    }

    @Override
    public void onRefresh() {
        if (! keyWords.equals("")) {
            pickResults(getActivity().getApplicationContext(), NOT_SELECTED_FROM_ACTIVITY_LEVEL);
        } else {
            needQueryShowToast();
        }
    }

    public void loadNewQuery(String query) {
        if (! keyWords.equals("")) {
            keyWords = query;
            swipeLayout.setRefreshing(true);
            pickResults(getActivity().getApplicationContext(), NOT_SELECTED_FROM_ACTIVITY_LEVEL);
        } else {
            needQueryShowToast();
        }
    }

    private void needQueryShowToast() {
        Toast toast = Toast.makeText(context, R.string.search_need_any_chars, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
        view.setPaddingRelative(15, 10, 10, 15);
        toast.show();
    }

    public void pickResults(final Context context, int itemNumber) {

        FormBody.Builder formBody = new FormBody.Builder();

        if (getActivity() != null) {
            if (itemNumber == NOT_SELECTED_FROM_ACTIVITY_LEVEL) {
                AppCompatSpinner spinnerSearchType = ((MainActivity) getActivity()).getSpinnerSearch();
                JsonLibrary.Forum item = (JsonLibrary.Forum) spinnerSearchType.getSelectedItem();
                switch (item.getForum_id()) {
                    case -3:
                        return;
                    case -2:
                        formBody.add("sign_in_device", "mobile")
                                .add("search_type", "char")
                                .add("search_text", keyWords)
                                .add("submit_search_search", "1");
                        break;
                    case -1:
                        formBody.add("sign_in_device", "mobile")
                                .add("search_type", "user")
                                .add("search_text", keyWords)
                                .add("submit_search_search", "1");
                        break;
                    default:
                        formBody.add("sign_in_device", "mobile")
                                .add("search_forum", Integer.toString(item.getForum_id()))
                                .add("submit_search_view", "1");
                        break;
                }
            } else {
                // numer forum jest pobierany z funkcji w MainActivity
                formBody.add("sign_in_device", "mobile")
                        .add("search_forum", Integer.toString(itemNumber))
                        .add("submit_search_view", "1");
            }

            Postarium.WebRequests.post("search.html", formBody.build(), new Callback() {
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
                                textEmpty.setVisibility(View.GONE);
                                resultsList.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {

                        final JsonSearchResults jsonSearchResults = new JsonSearchResults();
                        jsonSearchResults.parse(response.body().string());

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    changeResultsList(context, jsonSearchResults.getSearchResults().getRecords());
                                }
                            });
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(context, R.string.request_fail, Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                                    view.setPaddingRelative(15, 10, 10, 15);
                                    toast.show();
                                    if (swipeLayout.isRefreshing()) {
                                        swipeLayout.setRefreshing(false);
                                    }
                                    textEmpty.setVisibility(View.GONE);
                                    resultsList.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void changeResultsList(Context context, ArrayList<JsonSearchResults.Result> results) {
        // komunikat o pustej liście wyników
        if (results == null) {
            resultsList.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        }
        else {
            textEmpty.setVisibility(View.GONE);
            resultsList.setVisibility(View.VISIBLE);
            if (adapter == null || resultsList.getAdapter() == null) {
                adapter = new SearchAdapter(this, results);
                // TODO -> więcej kolumn dla dużych widoków -> WYKORYSTAC ZMIENNĄ !!!!
                RecyclerView.LayoutManager mGridManager = new GridLayoutManager(context, 2);
                resultsList.setLayoutManager(mGridManager);
                //resultsList.setItemAnimator(new DefaultItemAnimator());
                resultsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        GridLayoutManager gridManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                        swipeLayout.setEnabled(gridManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
                    }
                });
                resultsList.setAdapter(adapter);
            } else {
                if (resultsList.getAdapter() != null) {
                    ((SearchAdapter) resultsList.getAdapter()).swap(results);

                }
            }
        }
        if (swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void displayUserProfile(int user_id) {
        UserProfileService.startActionProfileView(context, user_id);
    }
}

package noritoshi_scarlett.postarium.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.activities.ForumActivity;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ViewPagerAdapter;
import noritoshi_scarlett.postarium.interfaces.PostariDataRelase;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumPostsMsgs;

public class ForumPagerFragment extends Fragment implements PostariDataRelase {

    private TabLayout tabLayoutDetail;
    private ViewPager viewPagerDetail;
    private ViewPagerAdapter adapterDetail;

    private boolean isTablet;
    private float pageWidth;

    private JsonForumPostsMsgs.ForumPostsMsgs forumPostsMsgs;
    private JsonForumComponents.ShortcutsArray forumShrotcuts;

    public ForumPagerFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forum_pager, container, false);

        Bundle bundle = new Bundle();

        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            pageWidth = 0.5f;
        } else {
            pageWidth = 1.0f;
        }
        bundle.putFloat("pageWidth", pageWidth);

        if (getActivity() != null) {
            tabLayoutDetail = getActivity().findViewById(R.id.tabLayoutDetail);
        }
        viewPagerDetail = v.findViewById(R.id.viewPagerPostsMsgs);
        adapterDetail = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(),
                getActivity().getApplicationContext(),
                getResources().getStringArray(R.array.forum_tab_postsmsgs_titles),
                pageWidth);
        viewPagerDetail.setOffscreenPageLimit(4);
        for (int i = 0; i < 4; i++) {
            ForumPostsMsgsFragment forumPostsMsgsFragment = new ForumPostsMsgsFragment();
            forumPostsMsgsFragment.setArguments(bundle);
            adapterDetail.addFragment(forumPostsMsgsFragment);
        }
        tabLayoutDetail.post(new Runnable() {
            @Override
            public void run() {
                viewPagerDetail.setAdapter(adapterDetail);
                tabLayoutDetail.setupWithViewPager(viewPagerDetail);
                int length = tabLayoutDetail.getTabCount();
                for (int i = 0; i < length; i++) {
                    tabLayoutDetail.getTabAt(i).setCustomView(adapterDetail.getTabDetailView(i));
                }
            }
        });

        ((ForumActivity) getActivity()).setPostariListener(this);

        forumPostsMsgs = Postarium.getJsonDataStorage().getForumPostsMsgses(Postarium.getPostariData().getPickedForum().getForum_id());
        forumShrotcuts = Postarium.getJsonDataStorage().getForumShortcuts(Postarium.getPostariData().getPickedForum().getForum_id());
        if (forumPostsMsgs != null || forumShrotcuts != null) {
            changePostsList();
            changeShortcuts();
        } else {
            getForumLists(getActivity());
        }

        return v;
    }

    /**
     * pobieranie listy tematów i wiadomości
     * @param activity   Aktywność
     */
    public void getForumLists(final Activity activity)
    {
        Postarium.WebRequests.get("forum/getPostsMsgs/" + Postarium.getPostariData().getPickedForum().getForum_name() + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String jResponse = response.body().string();

                    Gson gson = new GsonBuilder().create();
                    forumPostsMsgs = gson.fromJson(jResponse, JsonForumPostsMsgs.ForumPostsMsgs.class);
                    Postarium.getJsonDataStorage().setForumPostsMsgses(forumPostsMsgs, Postarium.getPostariData().getPickedForum().getForum_id());

                    changePostsList();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ((ForumActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
            }
        });

        Postarium.WebRequests.get("forum/viewQuick/" + Postarium.getPostariData().getPickedForum().getForum_name() + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String jResponse = response.body().string();

                    Gson gson = new GsonBuilder().create();
                    forumShrotcuts = gson.fromJson(jResponse, JsonForumComponents.ShortcutsArray.class);
                    Postarium.getJsonDataStorage().setForumShortcuts(forumShrotcuts, Postarium.getPostariData().getPickedForum().getForum_id());

                    changeShortcuts();

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ((ForumActivity) getActivity()).progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }
            }
        });
    }

    /**
     * Wpisanie danych do fragmentów
     */
    private void changePostsList() {
        adapterDetail.setAdapterDataForFragment(0, forumPostsMsgs.getNewPosts());
        adapterDetail.setAdapterDataForFragment(1, forumPostsMsgs.getLastPosts());
        adapterDetail.setAdapterDataForFragment(2,
                Postarium.getJsonDataStorage().getForumPrivMsgses(Postarium.getPostariData().getPickedForum().getForum_id()));
    }

    private void changeShortcuts() {
        adapterDetail.setAdapterDataForFragment(3, forumShrotcuts.getQuick());
    }

}

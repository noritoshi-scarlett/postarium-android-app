package noritoshi_scarlett.postarium.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import noritoshi_scarlett.postarium.adapters.SearchAdapter;
import noritoshi_scarlett.postarium.adapters.UserAdapter;
import noritoshi_scarlett.postarium.services.UserProfileService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.jsonPojo.JsonOnline;
import noritoshi_scarlett.postarium.R;

public class MainSiteFragment extends Fragment implements SearchAdapter.UserProfileDisplayInterface {

    private TextView textLoggedUsers;
    private TextView textActiveUsers;
    private RecyclerView loggedRecyclerView;
    private RecyclerView activeRecyclerView;

    private ArrayList<JsonOnline.User> loggedList;
    private ArrayList<JsonOnline.User> activeList;

    private Context context;

    public MainSiteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main_site, container, false);
        context = getActivity().getApplicationContext();

        textLoggedUsers = v.findViewById(R.id.textLoggedUsers);
        textActiveUsers = v.findViewById(R.id.textActiveUsers);
        loggedRecyclerView = v.findViewById(R.id.loggedRecyclerView);
        activeRecyclerView = v.findViewById(R.id.activeRecyclerView);

        if (savedInstanceState != null) {
            loggedRecyclerView.setVisibility(View.GONE);
            textLoggedUsers.setVisibility(View.VISIBLE);
            activeRecyclerView.setVisibility(View.GONE);
            textActiveUsers.setVisibility(View.VISIBLE);
            loadLists(Postarium.getJsonDataStorage().getOnlinePostarium().getUsers10(),
                    Postarium.getJsonDataStorage().getOnlinePostarium().getUsers48());
        } else {
            loggedRecyclerView.setVisibility(View.VISIBLE);
            textLoggedUsers.setVisibility(View.GONE);
            activeRecyclerView.setVisibility(View.VISIBLE);
            textActiveUsers.setVisibility(View.GONE);
            manageUserLists(getActivity().getApplicationContext());
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void manageUserLists(final Context context) {

        Postarium.WebRequests.get("index/getOnline.html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.request_fail), Toast.LENGTH_LONG);
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

                    String responseOnline = response.body().string();

                    JsonOnline.Online onlineParser = new JsonOnline(responseOnline).getOnline();
                    loggedList = onlineParser.getLists().getUsers10();
                    activeList = onlineParser.getLists().getUsers48();

                    Postarium.getJsonDataStorage().setOnlinePostarium(onlineParser.getLists());

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loadLists(loggedList, activeList);
                            }
                        });
                    }
                }
            }
        });
    }

    private void loadLists(ArrayList<JsonOnline.User> loggedList, ArrayList<JsonOnline.User> activeList) {

        int columns = 2;
        if (context.getResources().getBoolean(R.bool.isTablet)) {
            columns = 4;
        }

        if (loggedList == null) {
            loggedRecyclerView.setVisibility(View.GONE);
            textLoggedUsers.setVisibility(View.VISIBLE);
        } else {
            loggedRecyclerView.setVisibility(View.VISIBLE);
            textLoggedUsers.setVisibility(View.GONE);
            loggedRecyclerView.setLayoutManager(new GridLayoutManager(context, columns));
            final UserAdapter adapterLogged = new UserAdapter(MainSiteFragment.this, loggedList);
            loggedRecyclerView.setAdapter(adapterLogged);
        }
        if (activeList == null) {
            activeRecyclerView.setVisibility(View.GONE);
            textActiveUsers.setVisibility(View.VISIBLE);
        } else {
            activeRecyclerView.setVisibility(View.VISIBLE);
            textActiveUsers.setVisibility(View.GONE);
            activeRecyclerView.setLayoutManager(new GridLayoutManager(context, columns));
            final UserAdapter adapterActive = new UserAdapter(MainSiteFragment.this, activeList);
            activeRecyclerView.setAdapter(adapterActive);
        }
    }

    @Override
    public void displayUserProfile(int user_id) {
        UserProfileService.startActionProfileView(context, user_id);
    }
}

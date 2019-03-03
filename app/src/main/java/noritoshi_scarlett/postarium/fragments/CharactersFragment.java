package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.activities.CharProfileActivity;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.adapters.CharactersCircleAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.libraries.CharactersDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class CharactersFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        CharactersDialog.CharactersCircleInterface,
        CharactersCircleAdapter.MenuItemInterface{

    // VIEW'S VARIABLES
    private RecyclerView charactersCircleList;
    private SwipeRefreshLayout swipeLayout;
    private FloatingActionButton floatBtnAdd;

    // DATA VARIABLES
    private Context context;
    private JsonCharacters.CharactersCircle charactersCircle;
    private ArrayList<JsonCharacters.Character> charactersList ;
    private boolean moreInfoIsShow;
    private boolean privacyPolicyIsShow;

    public CharactersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_characters, container, false);

        // DODAWANIE POSTACI
        floatBtnAdd = (FloatingActionButton) v.findViewById(R.id.floatBtnAdd);
        floatBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharactersDialog dialog = new CharactersDialog(getActivity(), R.style.DialogTheme);
                dialog.setListener(CharactersFragment.this);
                dialog.create().show();
            }
        });
        // ODŚWIEŻANIE LISTY
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.backgroundRed));
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context, android.R.color.holo_blue_bright),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_red_light));
        // LISTA POSTACI
        charactersCircleList = (RecyclerView) v.findViewById(R.id.charactersCircleList);

        moreInfoIsShow = savedInstanceState != null && savedInstanceState.getBoolean("moreInfoIsShow");
        privacyPolicyIsShow = savedInstanceState != null && savedInstanceState.getBoolean("privacyPolicyIsShow");

        charactersCircle = Postarium.getJsonDataStorage().getCharactersCircle();
        if (charactersCircle != null) {
            changeCharactersCircleList(context, charactersCircle);
        } else {
            swipeLayout.setRefreshing(true);
            pickCharactersCircle(context);
        }
        // Przycisk informacyjny oraz tekst informacyjny (click i wyświetlnie)
        Button btnMoreInfo = (Button) v.findViewById(R.id.btnMoreInfo);
        Button btnPrivacyPolicy = (Button) v.findViewById(R.id.btnPrivacyPolicy);
        TextView textInfo = (TextView) v.findViewById(R.id.textCircleCharsDesc);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean prefValue = prefs.getBoolean("settings_use_info", true);
        if (! prefValue && btnMoreInfo != null) {
            btnMoreInfo.setVisibility(View.GONE);
        }
        if (! prefValue && textInfo != null) {
            textInfo.setVisibility(View.GONE);
        }

        if (btnMoreInfo != null) {
            btnMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMoreInfo();
                }
            });
        } else {
            moreInfoIsShow = false;
        }
        if (moreInfoIsShow) {
            showMoreInfo();
        }

        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyPolicy();
            }
        });
        if (privacyPolicyIsShow) {
            showPrivacyPolicy();
        }

        return v;
    }

    @Override
    public void onRefresh() {
        pickCharactersCircle(getActivity().getApplicationContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("moreInfoIsShow", moreInfoIsShow);
        outState.putBoolean("privacyPolicyIsShow", privacyPolicyIsShow);
    }

    private void changeCharSettings(int position) {
        CharactersDialog dialog = new CharactersDialog(getActivity(), R.style.DialogTheme, charactersList.get(position));
        dialog.setListener(this);
        dialog.create().show();
    }

    public void showMoreInfo() {
        moreInfoIsShow = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setMessage(R.string.circle_chars_desc)
                .setTitle(R.string.circle_chars_title);

        builder.setPositiveButton(R.string.circle_chars_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                moreInfoIsShow = false;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                moreInfoIsShow = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // !!!
    public void showPrivacyPolicy() {
        privacyPolicyIsShow = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        builder.setView(R.layout.alert_char_privacy)
                .setTitle(R.string.circle_char_privacy_title);

        builder.setPositiveButton(R.string.circle_chars_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                privacyPolicyIsShow = false;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                privacyPolicyIsShow = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void pickCharactersCircle(final Context context) {

        Postarium.WebRequests.get("characters/lists.html", new Callback() {
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
                    JsonCharacters charactersParser = new JsonCharacters();
                    charactersParser.parse(response.body().string());
                    charactersCircle = charactersParser.getCharacters();

                    Postarium.getJsonDataStorage().setCharactersCircle(charactersCircle);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                changeCharactersCircleList(context, charactersCircle);
                            }
                        });
                    }

                } else {
                    if (swipeLayout.isRefreshing()) {
                        swipeLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    private void changeCharactersCircleList(Context context, JsonCharacters.CharactersCircle characters) {

        this.charactersList = new ArrayList<>();
        for(JsonCharacters.CharactersForum charForForum : characters.getListCharactersCircle()) {
            for(JsonCharacters.Character character : charForForum.getListCharactersForForum()) {
                this.charactersList.add(character);
            }
        }
        //TODO -> zapisz dane do pliku i do klasy postarium
        CharactersCircleAdapter adapter = new CharactersCircleAdapter(context, this.charactersList, this);
        RecyclerView.LayoutManager mGridManager = new GridLayoutManager(context, 2);
        charactersCircleList.setLayoutManager(mGridManager);
        charactersCircleList.setItemAnimator(new DefaultItemAnimator());
        //wylaczene swipelayout kiedy recyclerviw jest przescrollowany w dół
        charactersCircleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager gridManager = ((GridLayoutManager) recyclerView.getLayoutManager());
                swipeLayout.setEnabled(gridManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });
        charactersCircleList.setAdapter(adapter);
        if (swipeLayout.isRefreshing()) {
            // TODO - załadowano dane - toast
            swipeLayout.setRefreshing(false);
        }
    }

    // INTERFEJS DLA DIALOGU POSTACI
    @Override
    public void refreshListOfCharacters() {
        pickCharactersCircle(getActivity().getApplicationContext());
    }

    // INTERFEJS DLA ITEMU Z RECYCLER VIEW'A
    @Override
    public void showCharProfile(JsonCharacters.Character character) {
        Intent intent = new Intent(context, CharProfileActivity.class);
        intent.putExtra("char_id", character.getChar_id());
        intent.putExtra("char_login", character.getChar_login());
        intent.putExtra("forum_id", character.getForum_id());
        startActivity(intent);
    }
    @Override
    public void showCharSettings(int position) { changeCharSettings(position); }
}

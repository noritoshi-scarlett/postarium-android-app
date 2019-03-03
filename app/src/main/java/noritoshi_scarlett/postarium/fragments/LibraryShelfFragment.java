package noritoshi_scarlett.postarium.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.util.LinkifyCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.LibraryAdapter;
import noritoshi_scarlett.postarium.interfaces.NeedRefreshListEvent;
import noritoshi_scarlett.postarium.interfaces.RefreshingListInterface;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;

public class LibraryShelfFragment extends Fragment implements
        LibraryAdapter.ExpandedItemInterface,
        RefreshingListInterface {

    // DATA VARIABLES
    private boolean moreInfoIsShow;
    private ArrayList<JsonLibrary.Forum> libraryData;
    private String libraryType;
    private JsonLibrary.Forum pickedForum;
    public NeedRefreshListEvent needRefreshListener;

    // VIEW'S VARIABLES
    private Context context;
    private RelativeLayout layoutMain;
    private FloatingActionButton floatBtn;
    private RecyclerView libraryList;
    private TextView textEmpty;
    private SwipeRefreshLayout swipeLayout;

    public interface  UpdateForumListInterface {
        void updateList();
    }

    public LibraryShelfFragment() {}

    /**
     * statyczny "konstruktor", wykorzystywany przez ViewPager
     * @param parentFragment fragment z ViewPagerem
     * @param libraryType rodzaj listy
     * @return nowy fragment
     */
    public static LibraryShelfFragment init(LibraryFragment parentFragment, String libraryType) {
        LibraryShelfFragment fragment = new LibraryShelfFragment();
        Bundle args = new Bundle();
        args.putString("libraryType", libraryType);
        fragment.setListener(parentFragment);
        fragment.setArguments(args);
        return fragment;
    }

    public void setListener( NeedRefreshListEvent callback) {
        try {
            needRefreshListener = callback;
        } catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + " must implement NeedRefreshListEvent" );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity().getApplicationContext();
        View v =  inflater.inflate(R.layout.fragment_library_shelf, container, false);
        libraryList = v.findViewById(R.id.libraryList);
        textEmpty = v.findViewById(R.id.textEmpty);

        Bundle bundle = getArguments();
        libraryType = bundle.getString("libraryType", "");
        moreInfoIsShow = savedInstanceState != null && savedInstanceState.getBoolean("moreInfoIsShow");
        // FLOATING BUTTON
        if (libraryType.equals("SUBSCRIBE")) {
            floatBtn = v.findViewById(R.id.floatBtnRemove);
            floatBtn.hide();
            floatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subscribeDiaglog(false);
                }
            });
        } else if (libraryType.equals("UNSUBSCRIBE")) {
            floatBtn = v.findViewById(R.id.floatBtnAdd);
            floatBtn.hide();
            floatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subscribeDiaglog(true);
                }
            });
        }

        // ODŚWIEŻANIE LISTY
        swipeLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.backgroundRed));
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(context, android.R.color.holo_blue_bright),
                ContextCompat.getColor(context, android.R.color.holo_green_light),
                ContextCompat.getColor(context, android.R.color.holo_orange_light),
                ContextCompat.getColor(context, android.R.color.holo_red_light));
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Użycie interfejsu do odświeżenia wszystkich list
                needRefreshListener.needRefresh();
            }
        });

        // Przycisk informacyjny oraz tekst informacyjny  (click i wyświetlnie)
        Button btnMoreInfo = v.findViewById(R.id.btnMoreInfo);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean prefValue = prefs.getBoolean("settings_use_info", true);
        if (libraryType.equals("SUBSCRIBE")) {
            if (!prefValue && btnMoreInfo != null) {
                btnMoreInfo.setVisibility(View.GONE);
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
        } else {
            btnMoreInfo.setVisibility(View.GONE);
        }

        selectData();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("moreInfoIsShow", moreInfoIsShow);
        // TODO -> aktualnie wybrane forum i floatbtn
    }

    // TODO -> inne info w zależności od używanej zakładki
    public void showMoreInfo() {
        moreInfoIsShow = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        // dodanie linkó do email
        final SpannableString message = new SpannableString(getResources().getString(R.string.library_desc));
        Linkify.addLinks(message, Linkify.EMAIL_ADDRESSES);
        builder.setMessage(message)
                .setTitle(R.string.library_subtitle);
        builder.setPositiveButton(R.string.library_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                moreInfoIsShow = true;
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
        // wyświetlanie linku do email
        TextView msgTxt = dialog.findViewById(android.R.id.message);
        if (msgTxt != null) { msgTxt.setMovementMethod(LinkMovementMethod.getInstance()); }
    }

    // ZAŁADUJ DANE (JEŚLI SĄ W PAMIECI)
    public void selectData() {
        if (Postarium.getJsonDataStorage().getLibrary() != null) {
            switch (libraryType) {
                case "SUBSCRIBE":
                    libraryData = Postarium.getJsonDataStorage().getLibrary().getSubscribe();
                    break;
                case "UNSUBSCRIBE":
                    libraryData = Postarium.getJsonDataStorage().getLibrary().getUnsubscribe();
                    break;
                case "BETA":
                    libraryData = Postarium.getJsonDataStorage().getLibrary().getBeta();
                    break;
            }
            if (libraryData != null) {
                changeLibraryList(libraryData);
            }
        }
    }

    // TODO -> Przenisć pusty layout do Recyclera
    // ADAPTER Z DANYMI, WŁĄCZANIE SWIPE
    private void changeLibraryList(ArrayList<JsonLibrary.Forum> libraryData) {
        // komunikat o pustej liście potrzebny dla swipa
        if (libraryData.size() == 0) {
            textEmpty.setVisibility(View.VISIBLE);
            if (floatBtn != null) { floatBtn.hide(); }
        }
        // posprzątaj po pustej liście
        else {
            //if (floatBtn != null) { if (floatBtn.getVisibility() == View.GONE) { floatBtn.show(); } }
            textEmpty.setVisibility(View.GONE);
        }
        // jesli nie ma adaptera -> utwórz
        if (libraryList.getAdapter() == null) {
            LibraryAdapter adapter = new LibraryAdapter(libraryData, this, libraryList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            libraryList.setLayoutManager(mLayoutManager);
            //libraryList.setItemAnimator(new DefaultItemAnimator());
            libraryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                    swipeLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
                }
            });
            libraryList.setAdapter(adapter);
        }
        // jest adapter -> tylko podmień dane
        else {
            ((LibraryAdapter) libraryList.getAdapter()).swap(libraryData);
        }
        // zakończ refresz swipe'a
        if (swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
    }

    /**
     * Sprawdza czy wybrano forum
     * Wyświetla Snackbar
     * Wysyła żądanie odświeżenia list
     * @param isSub czy kliknięto przycisk do subskrybowania
     */
    private void subscribeDiaglog(final Boolean isSub) {

        // wyłącz przycisk
        floatBtn.setEnabled(false);

        if (pickedForum != null) {

            final String message;
            if (isSub) {
                message = getResources().getString(R.string.library_forum_sub_success) + " " + pickedForum.getForum_title();
            } else {
                message = getResources().getString(R.string.library_forum_unsub_success) + " " + pickedForum.getForum_title();
            }

            Postarium.WebRequests.get("library/index/" + (isSub ? "sub/" : "unsub/") + pickedForum.getForum_id() + ".html", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            floatBtn.setEnabled(true);
                            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.request_fail), Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG);
                                View sbView = snackbar.getView();
                                sbView.setPadding(10, 10, 10, 60);
                                sbView.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                                snackbar.show();
                                needRefreshListener.needRefresh();
                                floatBtn.setEnabled(true);
                                floatBtn.hide();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                floatBtn.setEnabled(true);
                                Toast toast = Toast.makeText(context, context.getResources().getString(R.string.library_forum_not_picked), Toast.LENGTH_LONG);
                                View view = toast.getView();
                                view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                                view.setPaddingRelative(15, 10, 10, 15);
                                toast.show();
                            }
                        });
                    }
                }
            });
        } else {
            floatBtn.setEnabled(true);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, context.getResources().getString(R.string.library_forum_not_picked), Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                    view.setPaddingRelative(15, 10, 10, 15);
                    toast.show();
                }
            });
        }
    }

    /**
     * pokazywanie FAB
     * @param forum wybrane w liście forum
     */
    @Override
    public void isShownListener(JsonLibrary.Forum forum) {
        pickedForum = forum;
        if (floatBtn != null) {
            floatBtn.setEnabled(true);
            floatBtn.show();
        }
    }

    /**
     * pokazywanie FAB
     * @param forum wybrane w liście forum
     */
    @Override
    public void isHiddenListener(JsonLibrary.Forum forum) {
        pickedForum = forum;
        if (floatBtn != null) {
            floatBtn.hide();
        }
    }

    /**
     * odświeżenie zawartości listy
     * @param library nowe dane
     */
    @Override
    public void refreshingList(ArrayList<JsonLibrary.Forum> library) {
        swipeLayout.setRefreshing(true);
        changeLibraryList(library);
    }

    /**
     * zatrzymanie odświeżania listy
     */
    @Override
    public void refreshingStop() {
        if (swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(false);
        }
    }
}

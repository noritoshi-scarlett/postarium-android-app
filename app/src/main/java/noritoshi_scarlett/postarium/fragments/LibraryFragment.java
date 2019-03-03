package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import noritoshi_scarlett.postarium.libraries.CustomFragmentStatePagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.activities.MainActivity;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.interfaces.NeedRefreshListEvent;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;

public class LibraryFragment extends Fragment implements NeedRefreshListEvent {

    // DATA VARIABLES
    private static final int SHELF_COUNT = 3;
    private static final String[] URL_LIBRARY_TYPE = {"SUBSCRIBE", "UNSUBSCRIBE", "BETA"};
    private CustomFragmentStatePagerAdapter adapter;
    private JsonLibrary.Library libraryData;
    public LibraryShelfFragment.UpdateForumListInterface updateListListener;

    // VIEW'S VARIABLES
    private Context context;
    private ViewPager viewPagerShelf;

    public interface TabLayoutForLibraryInterface {
        public void setupTabs(ViewPager viewPager, CustomFragmentStatePagerAdapter adapter);
        public TabLayout getTabLayout();
        public void showTabLayout();
        public void hideTabLayout();
    }

    public LibraryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        updateListListener = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_library, container, false);
        // VIEW PAGER
        viewPagerShelf = (ViewPager) v.findViewById(R.id.viewPagerShelf);
        viewPagerShelf.setOffscreenPageLimit(SHELF_COUNT);

        // TODO-> Pokaż ostatnio pokazywany tab i  przescrolluj listę

        // ZAŁADUJ DANE (JEŚLI SĄ, TO Z PAMIĘCI; NIE - Z SERWERA)
        libraryData = Postarium.getJsonDataStorage().getLibrary();
        if (libraryData == null) {
            prepareTabs(true);
        } else {
            prepareTabs(false);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareTabs(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).hideTabLayout();
        }
    }

    /**
     * Tworzenie Fragmentów dla stron ViewPagera
     */
    private void prepareTabs(Boolean needPick) {
        // użycie abstrakcjnej klasy FragmentStatePageAdaptera, która posiada metodę do customizacji layoutu
        adapter = new CustomFragmentStatePagerAdapter(getChildFragmentManager(), context) {

            private SparseArray<Fragment> fragmentArray = new SparseArray<>();
            // tworzenie/pobieranie fragmentu
            @Override
            public Fragment getItem(int position) {
                // pobierz jeśli istnieje (by nie tworzyć niepotrzebnie nowego)
                if (fragmentArray.get(position) != null) {
                    return fragmentArray.get(position);
                }
                // nie istnieje -> utwórz i zwróć
                fragmentArray.put(position, LibraryShelfFragment.init(LibraryFragment.this, URL_LIBRARY_TYPE[position]));
                return fragmentArray.get(position);
            }
            @Override
            public int getCount() { return SHELF_COUNT; }
            // tytuł taba
            @Override
            public CharSequence getPageTitle(int position) {
                return context.getResources().getStringArray(R.array.library_tab_menu_titles)[position];
            }

        };

        viewPagerShelf.setAdapter(adapter);
        // setup z TabLayout z MainActivity
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setupTabs(viewPagerShelf, adapter);
            ((MainActivity) getActivity()).showTabLayout();
            // POBIEŻ DANE, BO ICH JESZCZE NIE MA
            if (needPick) {
                pickLibrary();
            }
        } else {
            // TODO -> Fatal Error
        }
    }

    /**
     * Interfejs, który jest implementowany przez ShelfFragment,
     * który wywołuje go wówczas, gdy uaktualni swoją listę i sąsiednie wymagają updatu
     */
    @Override
    public void needRefresh() {
        if (adapter != null) {
            pickLibrary();
        }
    }

    /**
     * Pobranie całej biblioteki forów (3 kategorie)
     * Przygotowanie tabów, jeśli wczesniej tego nei zrobiono (bo nie było danych)
     */
    private void pickLibrary() {
        Postarium.WebRequests.get("library/roll/all.html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 3; i++) {
                            ((LibraryShelfFragment) adapter.getItem(i)).refreshingStop();
                        }
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

                    JsonLibrary libraryParser = new JsonLibrary();
                    libraryParser.parse(response.body().string());
                    libraryData = libraryParser.getLibrary();
                    Postarium.getJsonDataStorage().setLibrary(libraryData);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                // odświeżenie listy w menu
                                updateListListener.updateList();
                                // odświeżenie wszystkich list
                                ((LibraryShelfFragment) adapter.getItem(0)).refreshingList(libraryData.getSubscribe());
                                ((LibraryShelfFragment) adapter.getItem(1)).refreshingList(libraryData.getUnsubscribe());
                                ((LibraryShelfFragment) adapter.getItem(2)).refreshingList(libraryData.getBeta());
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                ((LibraryShelfFragment) adapter.getItem(i)).refreshingStop();
                            }
                            Toast toast =  Toast.makeText(context, context.getResources().getString(R.string.request_fail), Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                            view.setPaddingRelative(15, 10, 10, 15);
                            toast.show();
                        }
                    });
                }
            }
        });
    }
}

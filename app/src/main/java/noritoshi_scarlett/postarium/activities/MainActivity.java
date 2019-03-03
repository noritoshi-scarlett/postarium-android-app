package noritoshi_scarlett.postarium.activities;


import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.SearchFragment;
import noritoshi_scarlett.postarium.libraries.CustomFragmentStatePagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.adapters.MiniNavAdapter;
import noritoshi_scarlett.postarium.fragments.CharactersFragment;
import noritoshi_scarlett.postarium.fragments.LibraryFragment;
import noritoshi_scarlett.postarium.fragments.LibraryShelfFragment;
import noritoshi_scarlett.postarium.fragments.MainSiteFragment;
import noritoshi_scarlett.postarium.fragments.NewsFragment;
import noritoshi_scarlett.postarium.fragments.NotesFragment;
import noritoshi_scarlett.postarium.fragments.SettingsFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import noritoshi_scarlett.postarium.libraries.TextDrawable;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LibraryShelfFragment.UpdateForumListInterface,
        LibraryFragment.TabLayoutForLibraryInterface,
        SearchFragment.SpinnerForSearchEngineInterface {

    private static final long DELAY_SEARCH = 500;
    public static final String POSTARIUM_USER_SETTINGS = "PostariumSettings";
    public static final String ACCOUNT_NAME = "account_name";
    public static final String ACCOUNT_EMAIL = "account_email";
    // STATIC VARIABLES
    // TODO -> Czy nie jest to zbyt surowe ograniczenie?
    private final static int FORUMS_MAX_NUMBER = 30;

    // VIEW'S VARIABLES
    private View coordinatorLayout;
    private NavigationView navigationView;
    private MiniNavAdapter adapter;
    private ListView miniNavList;
    private Menu navigationMainMenu;
    private SubMenu subscribeForumsSubMenu;
    public ProgressBar toolbarProgress;
    private TabLayout tabLibraryShelf;
    private LinearLayout layoutSearchEngine;
    private AppCompatSpinner spinnerSearchType;

    // DATA VARIABLES
    private Timer timer;

    private List<JsonLibrary.Forum> subscribeForumsList;    // lista forów w menu
    private int currentPosition = R.id.nav_main_site;
    private String currentSubtitle;
    private Boolean settingsMenuIsShow = false;
    private Menu toolbarMenu;

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        // TOOLBAR
        currentSubtitle = getResources().getString(R.string.menu_main_item_main_site);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // PROGRESSBAR (in toolbar) and NAVIGATION
        toolbarProgress = (ProgressBar) findViewById(R.id.toolbarProgress);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.navigationLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_bar_closed, R.string.navigation_bar_open);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // NAVIGATION DRAWER (MENU BOCZNE)
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            // wstępne ustawienia
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(this);
            View header = navigationView.getHeaderView(0);
            TextView textAccountName = (TextView) header.findViewById(R.id.textAccountName);
            TextView textAccountEmail = (TextView) header.findViewById(R.id.textAccountEmail);
            // SHARED PREFERENCES -> Pobranie nicku i maila
            SharedPreferences settings = getSharedPreferences(POSTARIUM_USER_SETTINGS, 0);
            textAccountName.setText(settings.getString(ACCOUNT_NAME, getResources().getString(R.string.hello_guest)));
            textAccountEmail.setText(settings.getString(ACCOUNT_EMAIL, getResources().getString(R.string.example_email)));
            // Aktywacja pozycji dla strony głównej
            navigationMainMenu = navigationView.getMenu();
            navigationMainMenu.findItem(R.id.nav_main_site).setChecked(true);

            prepareLibraryList();
        }
        //                    ---- KOMPONENTY DLA FRAGMENTÓW ----
        // TAB LAYOUT DLA LIBRARY
        tabLibraryShelf = (TabLayout) findViewById(R.id.tabForumShelf);
        // SEARCH LAYOUT DLA SEARCH
        layoutSearchEngine = (LinearLayout) findViewById(R.id.layoutSearchEngine);
        spinnerSearchType = (AppCompatSpinner) findViewById(R.id.spinnerSearchType);
        setupSpinnerSearch();

        // Timer dla SearchView
        timer = new Timer();

        // MINI-NAV (ikony po lewej)
        // TODO -> czy to jest jeszcze konieczne/przydatne?
        adapter = new MiniNavAdapter(this);
        miniNavList = (ListView)findViewById(R.id.miniNavList);
        if (miniNavList != null) {
            miniNavList.setAdapter(adapter);
            //TODO select last use item from menu
            miniNavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterV, View v, int position, long arg3)
                {
                    displayView(adapter.getMenuId(position));
                }
            });
        }

        // WYLOGOWANIE
        if (navigationView != null) {
            View headerLayout = navigationView.getHeaderView(0);
            Button buttonSignOut = (Button) headerLayout.findViewById(R.id.buttonSignOut);
            buttonSignOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSignOut();
                }
            });
        }

        // DOMYŚLNY WIDOK / OBRÓT EKRANU -> odtworzenie stanu
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt("itemId");
            currentSubtitle = savedInstanceState.getString("itemSubtitle");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(currentSubtitle);
            }
            // Element stały z menu (nie będacy subskrybowanym forum)
            if (currentPosition > FORUMS_MAX_NUMBER) {
                displayView(currentPosition);
            }
            // zaznaczenie elementu na mini-navie
            // TODO -> select last use item from menu
            //if (miniNavList != null) {)
        }
        else {
            // nie następuje odtwarzanie stanu -> pokaż główną
            displayView(R.id.nav_main_site);
        }

        // Zmiana nazwy aktywnego elementu zgodnie z fragmentem zbieranym ze stosu
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment f = getFragmentManager().findFragmentById(R.id.frameLayout);
                if (f != null) {
                    updateBarsFindVar(f);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("itemId", currentPosition);
        outState.putString("itemSubtitle", currentSubtitle);
    }

    /**
     * kliknięcie na przycisk cofania
     */
    @Override
    public void onBackPressed() {
        // zamykanie Drawera
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.navigationLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        // odznaczenie przycisku z settings (wraz z mechaniką działania)
        if (settingsMenuIsShow) {
            changeSettingsIconCheck(toolbarMenu.findItem(R.id.action_settings), false);
        }
        // wyświetlenie informacji o braku pozostałych fragmentow na stosie
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.tootchToClose), Toast.LENGTH_LONG);
            View view = toast.getView();
            view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
            view.setPaddingRelative(15, 10, 10, 15);
            toast.show();
        }
        super.onBackPressed();
    }

    /**
     * Utworzenie menu na toolbarze
     * Oprogramowanie wyszukiwarki
     * @param menu utworzone menu
     * @return isSuccess?
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        toolbarMenu = menu;
        // Zmiana podtytułu (konieczne jest to tutaj, bo w onCreate nie działa)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(currentSubtitle);
        }

        // SEARCH ENGINE -> wyszukiwarka Postarium
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Zamykanie paska z opcjami wyszukiwania
        MenuItem menuItem =  menu.findItem(R.id.action_search);
        if (menuItem != null) {
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    String fragmentTag = SearchFragment.class.getSimpleName();
                    SearchFragment searchFragment = (SearchFragment) getFragmentManager().findFragmentByTag(fragmentTag);
                    if (searchFragment == null || ! searchFragment.isVisible()) {
                        hideLayoutSearchEngine();
                    }
                    return true;
                }
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    showLayoutSearchEngine();
                    return true;
                }
            });
            MenuItemCompat.setActionView(menuItem, searchView);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() > 1) {
                    searchingQueryUpdate(query);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, getResources().getString(R.string.search_need_more_chars), Toast.LENGTH_LONG);
                    View view = toast.getView();
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
                    view.setPaddingRelative(15, 10, 10, 15);
                    toast.show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String query) {
                // TODO -> posprzątaj po timerze przy zamykaniu Activity!
                if (query.trim().length() > 1) {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchingQueryUpdate(query);
                                        }
                                    });
                                }
                            },
                            DELAY_SEARCH);
                }
                return true;
            }
        });
        return true;
    }

    private void searchingQueryUpdate(String query) {
        Bundle bundle = new Bundle();
        bundle.putString("search_key_words", query);
        String fragmentTag = SearchFragment.class.getSimpleName();
        if (!findFragment(fragmentTag)) {
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            replaceFragment(searchFragment, fragmentTag);
        } else {
            ((SearchFragment) getFragmentManager().findFragmentByTag(fragmentTag)).loadNewQuery(query);
        }
    }

    private void searchingDisplayUpdate(int itemNumber) {
        Bundle bundle = new Bundle();
        bundle.putInt("item_number", itemNumber);
        String fragmentTag = SearchFragment.class.getSimpleName();
        if (!findFragment(fragmentTag)) {
            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            replaceFragment(searchFragment, fragmentTag);
        } else {
            ((SearchFragment) getFragmentManager().findFragmentByTag(fragmentTag)).pickResults(getApplicationContext(), itemNumber);
        }
    }

    /**
     * Zaznaczono element na toolbarze
     * @param item zaznaczony element
     * @return isSuccess?
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                item.setEnabled(false);
                if (item.isChecked()) {
                    changeSettingsIconCheck(item, false);
                    getFragmentManager().popBackStack();
                } else {
                    changeSettingsIconCheck(item, true);
                    viewSettingsApp();
                }
                item.setEnabled(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void updateList() {
        prepareLibraryList();
    }

    private void prepareLibraryList() {
        // lista subskrybowanych forów
        MenuItem subscribeForumsMenu =  navigationMainMenu.findItem(R.id.nav_forums_list);
        subscribeForumsSubMenu = subscribeForumsMenu.getSubMenu();
        subscribeForumsList = new ArrayList<>();
        if (subscribeForumsSubMenu != null) {
            // usunięcie dotychczasowych elementów
            int size = subscribeForumsSubMenu.size();
            subscribeForumsSubMenu.removeItem(0);
            for (int i = 0; i < size; i++) {
                subscribeForumsSubMenu.removeItem(i+1);
            }
            // wpisanie elementów z zapisanej listy forów
            if (Postarium.getJsonDataStorage().getLibrary() != null ) {
                if (Postarium.getJsonDataStorage().getLibrary().getSubscribe().size() != 0) {
                    ArrayList<JsonLibrary.Forum> library = Postarium.getJsonDataStorage().getLibrary().getSubscribe();
                    subscribeForumsSubMenu = subscribeForumsMenu.getSubMenu();
                    for (int i = 0; i < library.size(); i++) {
                        JsonLibrary.Forum forum = library.get(i);
                        subscribeForumsSubMenu.add(0, i + 1, 0, forum.getForum_title())
                                .setIcon(new TextDrawable(this, forum.getForum_icon(), 36f));
                        // Jeśli jest problem z zaznaczaniem, dodaj setCheckable(true).
                        // A wyłączonejest, gdyż wówczas oznacza się czerwonym paskiem
                    }
                    // Lista ta przydatna jest przy sprawdzaniu, które forum zostało kliknięte
                    subscribeForumsList = library;
            // pusta lista
                } else {
                    subscribeForumsSubMenu.add(0, 0, 0, getResources().getString(R.string.library_empty_menu))
                            .setEnabled(false)
                            .setCheckable(false);
                }
            } else {
                subscribeForumsSubMenu.add(0, 0, 0, getResources().getString(R.string.library_empty_menu))
                        .setEnabled(false)
                        .setCheckable(false);
            }
        }
    }

    /**
     * Funkcja pomocnicza dla wyświetlania fragemtnu ustawień (powyższa metoda)
     * @param item kliknięty item
     * @param state czy ma być zaznaczony
     */
    private void changeSettingsIconCheck(MenuItem item, Boolean state) {
        settingsMenuIsShow = state;
        item.setIcon(ContextCompat.getDrawable(this, state ?
                R.drawable.icon_menu_settings_pick_48 : R.drawable.icon_menu_settings_48));
        item.setChecked(state);
        MenuItem navItem = navigationMainMenu.findItem(currentPosition);
        if (navItem != null) {
            navItem.setChecked(! state);
        }
    }

    /**
     * Wyświetlenie fragmentu ustawień
     */
    private void viewSettingsApp() {
        String fragmentTag = SettingsFragment.class.getSimpleName();
        if (! findFragment(fragmentTag)) {
            SettingsFragment settingsFragment = new SettingsFragment();
            replaceFragment(settingsFragment, fragmentTag);
            currentSubtitle = getResources().getString(R.string.menu_main_item_settings);
        }
    }

    /**
     * Klikniecie w menu NavigatorDravera
     * @param   item klikniety element menu
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // odznaczenie ikonki z ustawieniami
        if (settingsMenuIsShow) {
            changeSettingsIconCheck(toolbarMenu.findItem(R.id.action_settings), false);
        }
        // zaznaczenie kliknietego elementu (o ile nie jest stroną forum)
        int id = item.getItemId();
        if (id > FORUMS_MAX_NUMBER) {
            item.setChecked(true);
        }
        // podmiana wyświetlanego fragmentu
        displayView(id);
        // zamknięcie NavigationDravera
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.navigationLayout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    // Zapisanie zmian do pliku
    // TODO - Czy aby na pewno nie trzeba zapisywać więcej? I co tu właściwie się dzieje?
    @Override
    protected void onPause() {
        Postarium.getJsonDataStorage().saveToFile();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        Postarium.getJsonDataStorage().saveToFile();
        super.onDestroy();
    }

    /**
     * zmiana nazwy aktywnego elementu na pasku
     * zmiana zaznacznego elementu w NavigationDraver i bocznym menu
     * @param fragment aktywny fragment
     */
    private void updateBarsFindVar(Fragment fragment){

        // odznacz obecny element
        updateBarsRestore(false);
        // zobacz jaki fragment jest aktywny
        String fragClassName = fragment.getClass().getName();
        if (fragClassName.equals(SettingsFragment.class.getName())) {
            currentSubtitle = getResources().getString(R.string.menu_main_item_settings);
        }
        if (fragClassName.equals(MainSiteFragment.class.getName())){
            currentPosition = R.id.nav_main_site;
            currentSubtitle = getResources().getString(R.string.menu_main_item_main_site);
        }
        else if (fragClassName.equals(NewsFragment.class.getName())){
            currentPosition = R.id.nav_news;
            currentSubtitle = getResources().getString(R.string.menu_main_item_news);
        }
        else if (fragClassName.equals(LibraryFragment.class.getName())) {
            currentPosition = R.id.nav_forums_base;
            currentSubtitle = getResources().getString(R.string.menu_main_item_forums_base);
        }
        else if (fragClassName.equals(CharactersFragment.class.getName())){
            currentPosition = R.id.nav_char_circle;
            currentSubtitle = getResources().getString(R.string.menu_main_item_char_circle);
        }
        else if (fragClassName.equals(SearchFragment.class.getName())){
            currentPosition = -1;
            currentSubtitle = getResources().getString(R.string.menu_main_item_search);
        }
        //else if (fragClassName.equals(NotesFragment.class.getName())){
        //    currentPosition = R.id.nav_notes;
        //    currentSubtitle = getResources().getString(R.string.menu_main_item_notices);
        //  TODO -> Notes fragment
        //}
        else {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                currentSubtitle = "";
            }
        }
        // aktywuj nowy element
        updateBarsRestore(true);
    }

    /**
     * Zmiana zaznaczonego elementu na menu(drawer, mini-menu)
     * @param selected czy ma być zaznaczony?
     */
    private void updateBarsRestore(boolean selected) {
        // zaznaczenie na mini-menu
        if (miniNavList != null) {
            int position = adapter.getItemPosition(currentPosition);
            if ( position != -1) {
                adapter.getViewByPosition(position, miniNavList).setSelected(selected);
            }
        }
        // zazaczenie nowego elementu na NavigationDraverze
        if (selected) {
            navigationView.setCheckedItem(currentPosition);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(currentSubtitle);
            }
        }
        // odznaczenie doychczas aktywnego elementu na NavigationDraverze
        else {
            MenuItem navItem = navigationMainMenu.findItem(currentPosition);
            if (navItem != null) {
                navItem.setChecked(false);
            }
        }
    }

    /**
     * Podmiana fragmentu
     * @param viewId    numer id fragmentu
     */
    public void displayView(int viewId) {

        // Dla przechodzenia na stronę forum
        if ((viewId > 0) && (viewId < FORUMS_MAX_NUMBER)) {
            MenuItem clickedMenu = subscribeForumsSubMenu.findItem(viewId);
            if (clickedMenu != null) {
                // IF  ::: jeśli gracz posiada na tym forum postać...
                boolean haveAnyChar = false;
                // zaznacz, jakie forum wybrano
                Postarium.getPostariData().getInstance().setPickedForum(subscribeForumsList.get(clickedMenu.getItemId() - 1));
                if (Postarium.getJsonDataStorage().getCharactersCircle() != null) {
                    for (JsonCharacters.CharactersForum characters
                            : Postarium.getJsonDataStorage().getCharactersCircle().getListCharactersCircle()) {
                        if (characters.getForum_id() == Postarium.getPostariData().getInstance().getPickedForum().getForum_id()) {
                            haveAnyChar = true;
                            Postarium.getPostariData().getInstance().setCharacters(characters.getListCharactersForForum());
                            break;
                        }
                    }
                }
                // THEN ...to przejdź
                if (haveAnyChar) {
                    Intent intent = new Intent(getApplicationContext(), ForumActivity.class);
                    startActivity(intent);
                }
                // ELSE ...wyświetl komunikat o potrzebie dopisania postaci
                else {
                    needCharactersDisplaySnackBar();
                }
            }
            return;
        }

        // Pozycje z menu, które mają swoje dedykowane fragmenty
        String fragmentTag;
        currentSubtitle = getString(R.string.menu_main_item_main_site);
        switch (viewId) {
            case R.id.nav_main_site:
                fragmentTag = MainSiteFragment.class.getSimpleName();
                if (! findFragment(fragmentTag)) {
                    replaceFragment(new MainSiteFragment(), fragmentTag);
                    currentSubtitle = getResources().getString(R.string.menu_main_item_main_site);
                }
                break;
            case R.id.nav_forums_base:
                fragmentTag = LibraryFragment.class.getSimpleName();
                if (! findFragment(fragmentTag)) {
                    replaceFragment(new LibraryFragment(), fragmentTag);
                    currentSubtitle = getResources().getString(R.string.menu_main_item_forums_base);
                }
                break;
            case R.id.nav_char_circle:
                fragmentTag = CharactersFragment.class.getSimpleName();
                if (! findFragment(fragmentTag)) {
                    replaceFragment(new CharactersFragment(), fragmentTag);
                    currentSubtitle = getResources().getString(R.string.menu_main_item_char_circle);
                }
                break;
            case R.id.nav_news:
                fragmentTag = NewsFragment.class.getSimpleName();
                if (! findFragment(fragmentTag)) {
                    replaceFragment(new NewsFragment(), fragmentTag);
                    currentSubtitle = getResources().getString(R.string.menu_main_item_news);
                }
                break;
            case R.id.nav_notes:
                fragmentTag = NotesFragment.class.getSimpleName();
                if (! findFragment(fragmentTag)) {
                    replaceFragment(new NotesFragment(), fragmentTag);
                    currentSubtitle = getResources().getString(R.string.menu_main_item_notes);
                }
                break;
        }
        // podmień podtytuł na toolbarze
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(currentSubtitle);
        }
    }

    /**
     * Szukanie fragmentu na stosie
     * @param fragmentTag nazwa fragmentu
     * @return czyZnaleziono?
     */
    private boolean findFragment(String fragmentTag){
        FragmentManager manager = getFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(fragmentTag, 0);
        return (fragmentPopped || manager.findFragmentByTag(fragmentTag) != null);
    }

    /**
     * podmiana widocznego fragmentu oraz koloru jego tła
     * @param fragment fragment który ma być pokazany
     * @param fragmentTag nazwa fragmentu, który ma być pokazany
     */
    private void replaceFragment(Fragment fragment, String fragmentTag) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.frameLayout, fragment, fragmentTag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(fragmentTag);
        ft.commit();
    }

    /**
     * DIALOG -> wyświetlany po kliknięciu na ikonkę wyjścia
     */
    private void dialogSignOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);

        //LOGOUT
        builder.setTitle(R.string.sign_out_title);
        builder.setPositiveButton(R.string.sign_out_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Postarium.WebRequests.get("sign.html", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // TODO -> Brak połączenia z internetem, co zrobić?
                        // TODO -> Dezaktywuj ikonę
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });
        // CLOSE APP
        // TODO -> Całkiem zamykaj, a nie tylko chowaj do tła
        // TODO EDIT: Tak sięnie da, więc opcja w sumie chujowa
        builder.setNegativeButton(R.string.sign_out_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * wyświetlenie komunikatu o braku postaci dla wybranego forum
     */
    private void needCharactersDisplaySnackBar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getString(R.string.circle_char_empty_try_forum), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.circle_char_goto), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayView(R.id.nav_char_circle);
                    }
                });
        snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.buttonSuccessFocus));
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(4);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textPrimary));
        Button button = (Button) sbView.findViewById(android.support.design.R.id.snackbar_action);
        button.setMaxLines(4);
        snackbar.show();
    }

    @Override
    public void setupTabs(ViewPager viewPager, CustomFragmentStatePagerAdapter adapter) {

        tabLibraryShelf.setupWithViewPager(viewPager);
        int length = tabLibraryShelf.getTabCount();
        TabLayout.Tab tab;
        for (int i = 0; i < length; i++) {
            // customizacja layoutu
            tab = tabLibraryShelf.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLibraryShelf;
    }

    @Override
    public void showTabLayout() {
        if (tabLibraryShelf.getVisibility() != View.VISIBLE) {
            tabLibraryShelf.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideTabLayout() {
        if (tabLibraryShelf.getVisibility() == View.VISIBLE) {
            tabLibraryShelf.setVisibility(View.GONE);
        }
    }


    private void setupSpinnerSearch() {
        // LISTA FORÓW
        final ArrayList<JsonLibrary.Forum> library = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject element = new JsonObject();

        element.addProperty("forum_title", getResources().getString(R.string.search_type_users));
        element.addProperty("forum_id", "-1");
        JsonLibrary.Forum item = gson.fromJson(element, JsonLibrary.Forum.class);
        library.add(item);
        element.addProperty("forum_title", getResources().getString(R.string.search_type_chars));
        element.addProperty("forum_id", "-2");
        item = gson.fromJson(element, JsonLibrary.Forum.class);
        library.add(item);
        element.addProperty("forum_title", getResources().getString(R.string.search_type_forum));
        element.addProperty("forum_id", "-3");
        item = gson.fromJson(element, JsonLibrary.Forum.class);
        library.add(item);

        if (Postarium.getJsonDataStorage().getLibrary() != null) {
            library.addAll(Postarium.getJsonDataStorage().getLibrary().getSubscribe());
        }

        ArrayAdapter<JsonLibrary.Forum> adapter = new ArrayAdapter<JsonLibrary.Forum>(getApplicationContext(), R.layout.item_spinner, library) {
            // CUSTOM FONT
            final Typeface tf = TypefaceUtils.load(getResources().getAssets(),"fonts/Dosis-Light.ttf");
            // Disable header for forums
            @Override
            public boolean isEnabled(int position) {
                return position != 2;
            }
            // Affects default (closed) state of the spinner
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTypeface(tf);
                return view;
            }
            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                mTextView.setTypeface(tf);
                int padding_in_dp = 8;  // 8 dps
                final float scale = getResources().getDisplayMetrics().density;
                int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
                switch (position) {
                    case 0: case 1:
                        mTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textHover));
                        mTextView.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
                        break;
                    case 2:
                        mTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textLink));
                        mTextView.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
                        break;
                    default:
                        mTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textPrimary));
                        mTextView.setPadding(padding_in_px * 3, padding_in_px, padding_in_px, padding_in_px);
                        break;
                }
                return mView;
            }
        };
        spinnerSearchType.setAdapter(adapter);
        spinnerSearchType.setSelection(0);
        // W przypadku zaznaczenia forum z listy, konieczne jest wyświetlenie fragmentu
        spinnerSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                JsonLibrary.Forum item = (JsonLibrary.Forum) adapterView.getItemAtPosition(i);
                if (item.getForum_id() > -1) {
                    searchingDisplayUpdate(item.getForum_id());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public AppCompatSpinner getSpinnerSearch() {
        return spinnerSearchType;
    }

    @Override
    public void showLayoutSearchEngine() {
        if (layoutSearchEngine.getVisibility() != View.VISIBLE) {
            layoutSearchEngine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLayoutSearchEngine() {
        if (layoutSearchEngine.getVisibility() == View.VISIBLE) {
            layoutSearchEngine.setVisibility(View.GONE);
        }
    }
}

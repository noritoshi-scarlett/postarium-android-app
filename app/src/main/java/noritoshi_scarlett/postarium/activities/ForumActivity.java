package noritoshi_scarlett.postarium.activities;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.ForumTreeFragment;
import noritoshi_scarlett.postarium.fragments.ForumWebViewFragment;
import noritoshi_scarlett.postarium.fragments.ForumWriterFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumProfile;
import noritoshi_scarlett.postarium.libraries.TextDrawable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.adapters.ForumNewsAdapter;
import noritoshi_scarlett.postarium.adapters.ViewPagerAdapter;
import noritoshi_scarlett.postarium.fragments.ForumChatBoxFragment;
import noritoshi_scarlett.postarium.fragments.ForumOnlineFragment;
import noritoshi_scarlett.postarium.fragments.ForumPagerFragment;
import noritoshi_scarlett.postarium.fragments.ForumPostsMsgsFragment;
import noritoshi_scarlett.postarium.interfaces.PostariDataRelase;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForumActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    private JsonLibrary.Forum pickedForum;

    public ProgressBar progressBar;
    private ArrayList<ViewPagerAdapter> adapters;
    private ViewPagerAdapter adapterTabMain;

    private TabLayout tabLayoutMain;
    private TabLayout tabLayoutDetail;
    private ViewPager viewPager;

    private PostariDataRelase postariDataRelase;
    private FloatingActionButton btnPostari;
    private FloatingActionButton btnMenuChar;

    public void setPostariListener(PostariDataRelase postariDataRelase) {
        this.postariDataRelase = postariDataRelase;
    }

    private List<HashMap<String, Object>> data = new ArrayList<>();
    private boolean showedListMenu;

    private boolean isTablet;

    private boolean isViewingNews;
    private int tabMainPosition;
    private int tabDetailPosition;

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        pickedForum = Postarium.getPostariData().getPickedForum();

        isViewingNews = false;
        tabMainPosition = 0;
        tabDetailPosition = 0;

        progressBar = findViewById(R.id.progressBar);

        btnPostari = findViewById(R.id.floatBtnPostari);
        btnMenuChar =  findViewById(R.id.menuChar);


        tabLayoutMain = findViewById(R.id.tabLayoutMain);
        tabLayoutDetail = findViewById(R.id.tabLayoutDetail);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(6);

        setupAdapters();

        tabLayoutMain.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(adapterTabMain);
                tabLayoutMain.setupWithViewPager(viewPager);
                int length = tabLayoutMain.getTabCount();
                for (int i = 0; i < length; i++) {
                    tabLayoutMain.getTabAt(i).setCustomView(adapterTabMain.getTabIconView(i));
                }

                //setHalfWidth(viewPager, false);

                tabLayoutMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(final TabLayout.Tab tab) {
                        int length = tabLayoutMain.getTabCount();
                        for (int i = 0; i < length; i++) {
                            View v = tabLayoutMain.getTabAt(i).getCustomView();
                            TextView title = v.findViewById(R.id.title);
                            if (i != tab.getPosition()) {
                                title.setVisibility(View.INVISIBLE);
                            } else {
                                title.setVisibility(View.VISIBLE);
                            }
                        }
                        tab.select();
                        viewPager.setCurrentItem(tab.getPosition());
                        viewPager.refreshDrawableState();
                        tabLayoutMain.refreshDrawableState();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                switch (tab.getPosition()) {
                                    case 2:
                                        tabLayoutDetail.setVisibility(View.VISIBLE);
                                        if (! btnPostari.isShown()) {
                                            btnPostari.show();
                                        }
                                        if (! btnMenuChar.isShown()) {
                                            showFloatingActionButton(btnMenuChar);
                                        }
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                        break;
                                    case 0: case 1: case 3:
                                        tabLayoutDetail.setVisibility(View.GONE);
                                        if (btnPostari.isShown()) {
                                            btnPostari.hide();
                                        }
                                        if (! btnMenuChar.isShown()) {
                                            showFloatingActionButton(btnMenuChar);
                                        }
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                        break;
                                    case 4:
                                        tabLayoutDetail.setVisibility(View.GONE);
                                        if (btnPostari.isShown()) {
                                            btnPostari.hide();
                                        }
                                        if (! btnMenuChar.isShown()) {
                                            showFloatingActionButton(btnMenuChar);
                                        }
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                        break;
                                    case 5:
                                        tabLayoutDetail.setVisibility(View.GONE);
                                        if (btnPostari.isShown()) {
                                            btnPostari.hide();
                                        }
                                        if (btnMenuChar.isShown()) {
                                            hideFloatingActionButton(btnMenuChar);
                                        }
                                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                        break;
                                }
                            }
                        }, 400);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });
            }
        });

        tabLayoutMain.setTabTextColors(ContextCompat.getColor(getApplicationContext(), R.color.transparent),
                ContextCompat.getColor(getApplicationContext(), R.color.textPrimary));
        tabLayoutDetail.setTabTextColors(ContextCompat.getColor(getApplicationContext(), R.color.textPrimary),
                ContextCompat.getColor(getApplicationContext(), R.color.textPrimary));

        showedListMenu = false;
        boolean flag = true;
        for (JsonCharacters.CharactersForum characters
                : Postarium.getJsonDataStorage().getCharactersCircle().getListCharactersCircle()) {
            if (characters.getForum_id() == pickedForum.getForum_id()) {
                for (JsonCharacters.Character character : characters.getListCharactersForForum()) {
                    addItem(character.getChar_login(), R.drawable.icon_char_female_edit_c48, character.getChar_id());
                    if (flag) {
                        Postarium.getPostariData().setChar_id(character.getChar_id());
                        Postarium.getPostariData().setChar_login(character.getChar_login());
                        flag = false;
                    }
                }
            }
        }

        btnMenuChar.setImageDrawable(new TextDrawable(
                getApplicationContext(),
                Postarium.getPostariData().getChar_login().substring(0, 1),
                ContextCompat.getColor(this, R.color.textPrimary),
                50.0f));
        btnMenuChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! showedListMenu) {
                    showListMenu(btnMenuChar);
                }
                showedListMenu = ! showedListMenu;
            }
        });

        btnPostari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if (postariDataRelase != null) {
                    postariDataRelase.getForumLists(ForumActivity.this);
                }
            }
        });
    }

    private void hideFloatingActionButton(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }
        fab.hide();
    }
    private void showFloatingActionButton(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast toast = Toast.makeText(this, getResources().getString(R.string.toutchToBack), Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundBlueTransparent));
        view.setPaddingRelative(15, 10, 10, 15);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1500);
    }

    private void setupAdapters() {

        // Adaptery dlagłównych kategorii
        float pageWidth = (getResources().getBoolean(R.bool.isTablet))? 1.0f : 1.0f;
        adapterTabMain = new ViewPagerAdapter(getSupportFragmentManager(), getApplicationContext(),
                getResources().getStringArray(R.array.forum_tab_menu_titles), pageWidth);

        //ONLINE
        ForumOnlineFragment forumOnlineFragment = new ForumOnlineFragment();
        adapterTabMain.addFragment(forumOnlineFragment);
        //SHOUTBOX
        ForumChatBoxFragment forumChatBoxFragment = new ForumChatBoxFragment();
        adapterTabMain.addFragment(forumChatBoxFragment);
        //POSTY
        ForumPagerFragment forumPagerFragment = new ForumPagerFragment();
        adapterTabMain.addFragment(forumPagerFragment);
        //DRZEWO
        ForumTreeFragment treeFragment = new ForumTreeFragment();
        adapterTabMain.addFragment(treeFragment);
        //TODO DRZEWO LISTY
        ForumWriterFragment writerFragment = new ForumWriterFragment();
        adapterTabMain.addFragment(writerFragment);
        // WIADOMOSCI
        ForumWebViewFragment forumWebViewFragment = new ForumWebViewFragment();
        adapterTabMain.addFragment(forumWebViewFragment);
    }

    // Use this to add items to the list that the ListPopupWindow will use
    private void addItem(String title, int iconResourceId, int id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("charTitle", title);
        map.put("charIcon", iconResourceId);
        map.put("charId", id);
        data.add(map);
    }

    // Call this when you want to show the ListPopupWindow
    private void showListMenu(final View anchor) {
        final ListPopupWindow popupWindow = new ListPopupWindow(this);

        ListAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_char_menu, // You may want to use your own cool layout
                new String[] {"charTitle", "charIcon", "charId"}, // These are just the keys that the data uses
                new int[] {R.id.charTitle, R.id.charIcon, R.id.charId}); // The view ids to map the data to

        //popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.transparent)));
        popupWindow.setWidth(500); // note: don't use pixels, use a dimen resource
            popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView charId = view.findViewById(R.id.charId);
                    Postarium.getPostariData().setChar_id( Integer.parseInt(charId.getText().toString()) );
                    ((FloatingActionButton) anchor).setImageDrawable(new TextDrawable(
                            getApplicationContext(),
                            charId.getText().toString().substring(0, 1),
                            50.0f));
                    popupWindow.dismiss();
                }
            });

        popupWindow.show();
    }

    private void showForumNews() {
        progressBar.setVisibility(View.VISIBLE);

        Postarium.WebRequests.get("forum/viewNews/" + pickedForum.getForum_name() + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jResponse = response.body().string();

                    Gson gson = new GsonBuilder().create();
                    final JsonForumComponents.NewsArray forumNews = gson.fromJson(jResponse, JsonForumComponents.NewsArray.class);
                    Postarium.getJsonDataStorage().setForumNews(forumNews, Postarium.getPostariData().getPickedForum().getForum_id());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            dialogPreviewSite(forumNews.getNews());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    /**
     * okno dialogowe z newsami
     * @param listNews  dane HTML
     */
    private void dialogPreviewSite(ArrayList<JsonForumComponents.News> listNews) {
        isViewingNews = true;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogThemeTransparent);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        builder.setTitle(getResources().getString(R.string.forum_news_dialog_title));

        View view = (inflater.inflate(R.layout.alert_forum_news, null));
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listNews);
        ForumNewsAdapter adapter = new ForumNewsAdapter(listNews);
        recyclerView.setAdapter(adapter);
        //final int columns = getResources().getInteger(R.integer.forum_posts_record_columns); //TODO zrbic ilosc kolumn
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        builder.setView(view);
        builder.setPositiveButton(getResources().getString(R.string.main_dialog_close_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                isViewingNews = false;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isViewingNews = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.transparentHalfDark)));
        dialog.show();
    }

    public void goToWebView(String name, String login, String url) {
        if (tabLayoutMain != null) {
            if (tabLayoutMain.getTabCount() > 4 && tabLayoutMain.getTabAt(5) != null) {
                tabLayoutMain.getTabAt(5).select();
                if (viewPager.getAdapter() != null) {
                    ((ForumWebViewFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(5)).onSendTopicSite(name, login, url);
                }
            }
        }
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width;
    }

    private void setHalfWidth(ViewPager viewPager, boolean isSecond) {
        ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        if (isSecond) {
            viewPager.setPadding(getScreenWidth() / 2, 0, 0, 0);
        } else {
            params.width = getScreenWidth() / 2;
            viewPager.requestLayout();
        }
    }

    static public void displayPopupUser(View v, final Context context,final Fragment fragment, final String name, final String link) {
        Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.AppTheme_PopupOverlay);
        PopupMenu popup = new PopupMenu(wrapper, v);
        popup.getMenuInflater().inflate(R.menu.forum_user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_menu_forum_user_link_copy:
                        //Skopiuj do schowka
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(name, link);
                        if (clipboard != null) {
                            clipboard.setPrimaryClip(clip);
                        }
                        String toastText = context.getString(R.string.forum_copy_success) + " " + name;
                        Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
                        toast.show();
                        return true;
                    case R.id.nav_menu_forum_user_pw_send:
                        //
                        return true;
                    case R.id.nav_menu_forum_user_simple_view:
                        //listener.onButtonProfileClick(name, link);
                        if (context != null) {
                            ForumActivity.showWebViewInDialog(context, fragment, name, link);
                            return true;
                        }
                        return false;
                    case R.id.nav_menu_forum_user_open_browser:
                        //Otworz w przegladarce
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setData(Uri.parse(link));
                        context.startActivity(i);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    static public void showWebViewInDialog(Context context, Fragment fragment, String name, String link) {

        if (fragment.getActivity() != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity(), R.style.DialogTheme);

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
                    postData = "profile_url=" + URLEncoder.encode(link, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (postData != null) {
                    webView.postUrl(requestUrl, postData.getBytes());
                }

                builder.setPositiveButton(context.getResources().getString(R.string.main_dialog_close_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();

                dialog.show();
            }
        }
    }
}

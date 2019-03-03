package noritoshi_scarlett.postarium.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.LibraryAdapter;
import noritoshi_scarlett.postarium.adapters.UsersAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonUserProfile;
import noritoshi_scarlett.postarium.services.UserProfileService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserActivity extends AppCompatActivity implements UsersAdapter.UserProfileDisplayInterface {

    private JsonUserProfile.UserProfile userProfile;

    private boolean isSelfProfile;
    private boolean notLogged;
    private boolean isMyFriend;
    private boolean privateLevel;

    private UsersAdapter friendsAdapter;
    private LibraryAdapter libraryAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        // FONT -> Ustawienie domyślnej czcionki w aplikacji
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        TextView textUserDesc = findViewById(R.id.textUserDesc);
        ImageView user_image_background = findViewById(R.id.user_image_background);
        ImageView user_image_avatar = findViewById(R.id.user_image_avatar);

        FloatingActionButton fabSendMessage = findViewById(R.id.fabSendMessage);
        FloatingActionButton fabFriendAdd = findViewById(R.id.fabFriendAdd);
        FloatingActionButton fabFriendRemove = findViewById(R.id.fabFriendRemove);
        fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO -> send message
            }
        });
        fabFriendAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO -> Add friend
            }
        });
        fabFriendRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO -> Del friend
            }
        });

        RecyclerView listSubscribeForums = findViewById(R.id.listSubscribeForums);
        RecyclerView listFriends = findViewById(R.id.listFriends);

        listSubscribeForums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //listFriends.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        RecyclerView.LayoutManager mGridManager = new GridLayoutManager(this, 2);
        listFriends.setLayoutManager(mGridManager);

        // GET DATA FROM INTENT
        // TODO -> in backgrund
        Intent intent = getIntent();
        String profile_json = intent.getStringExtra("profile_json");
        JsonUserProfile jsonUserProfile = new JsonUserProfile();
        jsonUserProfile.parse(profile_json);
        userProfile = jsonUserProfile.getUserProfile();

        if (userProfile.getStatus() > 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(userProfile.getProfile().get(0).getUser_name());
            }

            // OPIS
            if (userProfile.getProfile().get(0).getUser_desc() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textUserDesc.setText(Html.fromHtml(
                            userProfile.getProfile().get(0).getUser_desc(),
                            android.text.Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textUserDesc.setText(Html.fromHtml(
                            userProfile.getProfile().get(0).getUser_desc()));
                }
            }

            // AVATAR
            String url = getResources().getString(R.string.url_postarium_base) + userProfile.getProfile().get(0).getUser_avatar();
            if (! url.equals("")) {
                Glide.with(getApplicationContext())
                        .load(url)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .into(user_image_avatar);
                Glide.with(getApplicationContext())
                        .load(url)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .into(user_image_background);
                //UsersAdapter.getAvatar(user_image_background, url);
                //UsersAdapter.getAvatar(user_image_avatar, url);
            }

            // ZNAJOMI
            if (userProfile.getFriends() != null) {
                friendsAdapter = new UsersAdapter(this, userProfile.getFriends());
                listFriends.setItemAnimator(new DefaultItemAnimator());
                listFriends.setAdapter(friendsAdapter);
            }

            // FORA
            if (userProfile.getForums() != null) {
                libraryAdapter = new LibraryAdapter(userProfile.getForums(), null, listSubscribeForums);
                listSubscribeForums.setItemAnimator(new DefaultItemAnimator());
                listSubscribeForums.setAdapter(libraryAdapter);
            }

        } else {
            //TODO -> Error, back
        }


    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // TODO -> Jesteście znajomymi etc.
        if (getSupportActionBar() != null) {
            if (userProfile.getFriend_state() != null) {
                getSupportActionBar().setSubtitle("");
            }
        }
        return true;
    }


    public void displayUserProfile(int user_id) {
        UserProfileService.startActionProfileView(this, user_id);
    }
}

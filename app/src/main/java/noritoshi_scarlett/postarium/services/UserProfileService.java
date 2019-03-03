package noritoshi_scarlett.postarium.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.activities.UserActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserProfileService extends IntentService {

    private static final String ACTION_USER_PROFILE_VIEW = "noritoshi_scarlett.postarium.services.action.USER_PROFILE_VIEW";
    private static final String ACTION_USER_AVATAR_DOWNLOAD = "noritoshi_scarlett.postarium.services.action.USER_AVATAR_DOWNLOAD";
    private static final String USER_ID = "noritoshi_scarlett.postarium.services.extra.USER_ID";
    private static final String AVATAR_URL = "noritoshi_scarlett.postarium.services.extra.AVATAR_URL";

    public UserProfileService() {
        super("UserProfileService");
    }

    public static void startActionProfileView(Context context, int user_id) {
        Intent intent = new Intent(context, UserProfileService.class);
        intent.setAction(ACTION_USER_PROFILE_VIEW);
        intent.putExtra(USER_ID, user_id);
        context.startService(intent);
    }
    public static void startActionAvatarDownload(Context context, String avatar_url) {
        Intent intent = new Intent(context, UserProfileService.class);
        intent.setAction(ACTION_USER_AVATAR_DOWNLOAD);
        intent.putExtra(AVATAR_URL, avatar_url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_USER_PROFILE_VIEW.equals(action)) {
                final int user_id = intent.getIntExtra(USER_ID, -1);
                handleActionProfileView(user_id);
            } else if (ACTION_USER_AVATAR_DOWNLOAD.equals(action)) {
                final String avatar_url = intent.getStringExtra(AVATAR_URL);
                handleActionAvatarDownload(avatar_url);
            }
        }
    }

    private void handleActionProfileView(int user_id) {

        final Context context = getApplicationContext();
        final Handler handler = new Handler(Looper.getMainLooper());

        Postarium.WebRequests.get("user/profile/" + user_id + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(context, R.string.request_fail, Toast.LENGTH_LONG);
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

                    final Intent intent = new Intent(context, UserActivity.class);
                    intent.putExtra("profile_json", response.body().string());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, R.string.request_fail, Toast.LENGTH_LONG);
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

    private void handleActionAvatarDownload(String avatar_url) {

        final Context context = getApplicationContext();
        final Handler handler = new Handler(Looper.getMainLooper());

        Postarium.WebRequests.get(avatar_url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    InputStream inputStream = response.body().byteStream();
                    try {
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO -> Store to SD and give from it.
                            }
                        });
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.activities.UserActivity;
import noritoshi_scarlett.postarium.jsonPojo.JsonUserProfile;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private UserActivity activity;
    private ArrayList<JsonUserProfile.SimpleProfile> profiles;
    private int count;

    public UsersAdapter() {}

    public interface UserProfileDisplayInterface {
        void displayUserProfile(int userId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected Button textUserName;
        protected ImageView imageUserAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            textUserName = (Button) itemView.findViewById(R.id.textUserName);
            imageUserAvatar = (ImageView) itemView.findViewById(R.id.imageUserAvatar);
        }
    }

    public UsersAdapter(UserActivity activity, ArrayList<JsonUserProfile.SimpleProfile> profiles) {
        this.activity = activity;
        this.profiles = profiles;
        this.count = profiles.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_user_friends, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final JsonUserProfile.SimpleProfile profile = this.profiles.get(position);

        if (profile.getUser_avatar() != null) {
            getAvatar(holder.imageUserAvatar, profile.getUser_avatar());
        } else {
           // holder.imageUserAvatar.setImageBitmap();
        }
        holder.textUserName.setText(profile.getUser_name());
        holder.textUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.displayUserProfile(profile.getUser_id());
            }
        });
    }

    @Override
    public int getItemCount() { return this.count; }


    public static void getAvatar(final ImageView imageUserAvatar, String url) {
        Postarium.WebRequests.get(url, new Callback() {
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
                                imageUserAvatar.setImageBitmap(bitmap);
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

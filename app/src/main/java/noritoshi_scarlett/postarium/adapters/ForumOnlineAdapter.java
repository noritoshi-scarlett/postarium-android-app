package noritoshi_scarlett.postarium.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.text.Html;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.activities.ForumActivity;
import noritoshi_scarlett.postarium.fragments.ForumOnlineFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumOnline;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class ForumOnlineAdapter extends RecyclerView.Adapter<ForumOnlineAdapter.ViewHolder> {

    private Context context;
    private ForumOnlineFragment listener;

    private ArrayList<JsonForumOnline.User> list;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Button viewTag;
        private TextView viewUsername;

        private ViewHolder(View itemView) {
            super(itemView);

            viewTag = itemView.findViewById(R.id.viewTag);
            viewUsername = itemView.findViewById(R.id.viewUsername);
        }
    }

    public interface ViewProfileListener {
        void onButtonProfileClick(String name, String url);
    }

    public ForumOnlineAdapter(ForumOnlineFragment listener, Context context, ArrayList<JsonForumOnline.User> list) {
        this.listener = listener;
        this.context = context;
        this.list = new ArrayList<>();
        this.list = list;
    }

    @Override
    public int getItemCount() {return list.size();}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View onlineView = inflater.inflate(R.layout.item_forum_online_user, parent, false);
        return new ViewHolder(onlineView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        int fontSize;
        switch (list.get(position).getTag().length()) {
            case 5: fontSize = 14; break;
            case 4: fontSize = 16; break;
            case 3: fontSize = 18; break;
            default: fontSize = 20; break;
        }
        holder.viewTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

        holder.viewTag.setText(list.get(position).getTag());
        holder.viewUsername.setText(list.get(position).getUsername());
        //color
        if (list.get(position).getColorText().length() > 1 && list.get(position).getColorBack().length() > 1) {
            holder.viewTag.setBackgroundColor(Color.parseColor(list.get(position).getColorBack()));
            holder.viewTag.setTextColor(Color.parseColor(list.get(position).getColorText()));
        } else {
            holder.viewTag.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueDark));
            holder.viewTag.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
        }
        //On item click
        holder.viewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String link = list.get(holder.getAdapterPosition()).getProfileLink();
                final String name = list.get(holder.getAdapterPosition()).getUsername();
                ForumActivity.displayPopupUser(v, context, listener, name, link);
            }
        });
        holder.viewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String link = list.get(holder.getAdapterPosition()).getProfileLink();
                final String name = list.get(holder.getAdapterPosition()).getUsername();
                ForumActivity.displayPopupUser(v, context, listener, name, link);
            }
        });
    }



}

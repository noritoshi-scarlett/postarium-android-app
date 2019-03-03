package noritoshi_scarlett.postarium.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.MainSiteFragment;
import noritoshi_scarlett.postarium.fragments.SearchFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonOnline;
import noritoshi_scarlett.postarium.jsonPojo.JsonSearchResults;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private MainSiteFragment fragment;

    private ArrayList<JsonOnline.User> list;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button userProfileBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            userProfileBtn = itemView.findViewById(R.id.button);
        }
    }

    public UserAdapter(MainSiteFragment fragment, ArrayList<JsonOnline.User> list) {
        super();
        this.fragment = fragment;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_simple_link, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.userProfileBtn.setText(list.get(position).getUser_name());
        holder.userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.displayUserProfile(list.get(holder.getAdapterPosition()).getUser_id());
            }
        });
    }

    @Override
    public int getItemCount() { return this.list.size(); }
}

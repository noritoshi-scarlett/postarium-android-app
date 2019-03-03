package noritoshi_scarlett.postarium.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.SearchFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonSearchResults;

public class SearchAdapter extends UsersAdapter {

    private SearchFragment fragment;

    private ArrayList<JsonSearchResults.Result> results;
    private int count;

    public class ViewHolder extends UsersAdapter.ViewHolder {

        private TextView textCharTag;
        private TextView textCharLogin;
        private TextView textCharForum;
        private TextView textUserOwnerTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            textCharTag = (TextView) itemView.findViewById(R.id.textCharTag);
            textCharLogin = (TextView) itemView.findViewById(R.id.textCharLogin);
            textCharForum = (TextView) itemView.findViewById(R.id.textCharForum);
            textUserOwnerTitle = (TextView) itemView.findViewById(R.id.textUserOwnerTitle);
        }
    }

    public SearchAdapter(SearchFragment fragment, ArrayList<JsonSearchResults.Result> results) {
        super();
        this.fragment = fragment;
        this.results = results;
        this.count = results.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.ViewHolder holder, int position) {
        final JsonSearchResults.Result result = this.results.get(position);

        ViewHolder newHolder = (ViewHolder) holder;

        // TODO -> Avatar dla gracza kiedy wyświetlam tylko gracza

        newHolder.textUserName.setText(result.getUser_name());
        if (result.getChar_login() != null) {
            newHolder.textCharTag.setText(result.getChar_tag());
            newHolder.textCharLogin.setText(result.getChar_login());
            newHolder.textCharForum.setText(result.getChar_forum());
            newHolder.textCharTag.setVisibility(View.VISIBLE);
            newHolder.textCharLogin.setVisibility(View.VISIBLE);
            newHolder.textCharForum.setVisibility(View.VISIBLE);
            newHolder.imageUserAvatar.setVisibility(View.GONE);
            newHolder.textUserOwnerTitle.setText(R.string.search_char_owner_title);
        } else {
            newHolder.textCharTag.setVisibility(View.GONE);
            newHolder.textCharLogin.setVisibility(View.GONE);
            newHolder.textCharForum.setVisibility(View.GONE);
            newHolder.imageUserAvatar.setVisibility(View.VISIBLE);
            getAvatar(holder.imageUserAvatar, result.getUser_avatar());
            newHolder.textUserOwnerTitle.setText(R.string.search_owner_title);
        }
        newHolder.textUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.displayUserProfile(result.getUser_id());
            }
        });
    }

    // podmiana danych
    public void swap(ArrayList<JsonSearchResults.Result> results){
        this.results = results;
        this.count = results.size();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return this.count; }
}

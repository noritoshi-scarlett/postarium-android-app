package noritoshi_scarlett.postarium.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.ForumPostsMsgsFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumPostsMsgs;

public class ForumPostsMsgsAdapter extends RecyclerView.Adapter<ForumPostsMsgsAdapter.ViewHolder> {

    private ForumPostsMsgsFragment.ForumPostsMsgsClickListener mListener;

    private ArrayList<?> list;

    private int objectType;

    private static boolean isLandscape;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView recordText;
        public Button recordBtnPost;

        public ViewHolder(View itemView) {
            super(itemView);

            recordBtnPost = itemView.findViewById(R.id.recordBtnPost);
            recordText = itemView.findViewById(R.id.recordText);
            isLandscape = false;
            if (recordText == null) {
                recordText = itemView.findViewById(R.id.recordTextLandscape);
                isLandscape = true;
            }
        }
    }

    public ForumPostsMsgsAdapter(ArrayList<?> list, int objectType, ForumPostsMsgsFragment.ForumPostsMsgsClickListener mListener) {
        this.mListener = mListener;
        this.objectType = objectType;

        switch (objectType) {
            case 0: case 1:
                this.list = new ArrayList<JsonForumPostsMsgs.ForumPostsMsgs>();
                break;
            case 2:
                this.list = new ArrayList<JsonForumPostsMsgs.PrivMsgs>();
                break;
            case 3:
                this.list = new ArrayList<JsonForumComponents.Shortcuts>();
                break;
        }

        this.list = list;
    }

    @NonNull
    @Override
    public ForumPostsMsgsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_forum_record_post, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ForumPostsMsgsAdapter.ViewHolder viewHolder, final int position) {

        final int char_id = Postarium.getPostariData().getChar_id();

        switch (objectType) {
            case 2: {
                final JsonForumPostsMsgs.Msg recordData = (JsonForumPostsMsgs.Msg) list.get(position);
                viewHolder.recordBtnPost.setText(recordData.getMsg_topic());
                if (isLandscape) {
                    viewHolder.recordText.setText(recordData.getMsg_author() + ",  " + recordData.getMsg_date());
                } else {
                    viewHolder.recordText.setText(recordData.getMsg_author() + "\n" + recordData.getMsg_date());
                }
                viewHolder.recordBtnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mListener != null) {
                                mListener.onClickPW(
                                          "forum/getMsg/" + Postarium.getPostariData().getPickedForum().getForum_name()
                                        + "/" + char_id + ".html", recordData.getMsg_href());
                        }
                    }
                });
                break;
            }
            case 0:case 1: {
                final JsonForumPostsMsgs.Post recordData = (JsonForumPostsMsgs.Post) list.get(position);
                viewHolder.recordBtnPost.setText(recordData.getPost_topic());
                if (isLandscape) {
                    viewHolder.recordText.setText(recordData.getPost_author() + ",  " + recordData.getPost_date());
                } else {
                    viewHolder.recordText.setText(recordData.getPost_author() + "\n" + recordData.getPost_date());
                }
                viewHolder.recordBtnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onClickSiteTopic(
                                    recordData.getPost_topic(),
                                    Postarium.getPostariData().getChar_login(),
                                    "forum/getSite/" + Postarium.getPostariData().getPickedForum().getForum_name()
                                    + ".html?site_url=" + Postarium.getPostariData().getPickedForum().getForum_url()
                                    + recordData.getPost_href_t() + "&site_title=" + recordData.getPost_topic() + "&char_id=" + char_id);
                    }
                });
                break;
            }
            case 3: {
                final JsonForumComponents.Shortcuts recordData = (JsonForumComponents.Shortcuts) list.get(position);
                viewHolder.recordBtnPost.setText(recordData.getTitle());
                viewHolder.recordText.setText(recordData.getUrl());
                viewHolder.recordText.setVisibility(View.GONE);
                viewHolder.recordBtnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null)
                            mListener.onClickSiteTopic(
                                    recordData.getTitle(),
                                    Postarium.getPostariData().getChar_login(),
                                    "forum/getSite/" + Postarium.getPostariData().getPickedForum().getForum_name()
                                    + ".html?site_url=" + Postarium.getPostariData().getPickedForum().getForum_url()
                                    + recordData.getUrl() + "&site_title=" + recordData.getTitle() + "&char_id=" + char_id);
                    }
                });
                break;
            }
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return list.size();
    }
}
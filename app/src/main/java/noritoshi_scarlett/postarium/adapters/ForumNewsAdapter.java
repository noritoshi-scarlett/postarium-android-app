package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;

import static android.text.Html.FROM_HTML_MODE_LEGACY;


public class ForumNewsAdapter extends RecyclerView.Adapter<ForumNewsAdapter.ViewHolder> {

    private ArrayList<JsonForumComponents.News> list;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textTitle;
        private TextView textDesc;
        private Button btnMore;

        private ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.newsTitle);
            textDesc = (TextView) itemView.findViewById(R.id.newsDesc);
            btnMore = (Button) itemView.findViewById(R.id.btnMore);
        }
    }

    public ForumNewsAdapter(ArrayList<JsonForumComponents.News> list) {
        this.list = new ArrayList<>();
        this.list = list;
    }

    @Override
    public int getItemCount() {return list.size();}

    @Override
    public ForumNewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_card_forum_news, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textTitle.setText(list.get(position).getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.textDesc.setText(Html.fromHtml(list.get(position).getDesc(), FROM_HTML_MODE_LEGACY));
        } else {
            holder.textDesc.setText(Html.fromHtml(list.get(position).getDesc()));
        }
        holder.textDesc.setMaxLines(4);
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.textDesc.getMaxLines() < 10) {
                    holder.textDesc.setMaxLines(200);
                } else {
                    holder.textDesc.setMaxLines(4);
                }
            }
        });
    }

}

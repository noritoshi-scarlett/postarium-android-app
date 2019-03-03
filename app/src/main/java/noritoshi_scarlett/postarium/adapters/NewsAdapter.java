package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonNews;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Context mContext;
    private int count;
    private JsonNews.News news;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textNewsTopic;
        private TextView textNewsDesc;
        private TextView textNewsAuthor;
        private TextView textNewsDate;

        private ViewHolder(View itemView) {
            super(itemView);
            textNewsTopic = (TextView) itemView.findViewById(R.id.textNewsTopic);
            textNewsDesc = (TextView) itemView.findViewById(R.id.textNewsDesc);
            textNewsAuthor = (TextView) itemView.findViewById(R.id.textNewsAuthor);
            textNewsDate = (TextView) itemView.findViewById(R.id.textNewsDate);
        }
    }

    public NewsAdapter(Context context, JsonNews.News news) {
        mContext = context;
        this.news = news;
        this.count = news.getListNews().size();
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.item_news_new, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        JsonNews.New news = this.news.getListNews().get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.textNewsTopic.setText(Html.fromHtml(news.getNews_topic(), android.text.Html.FROM_HTML_MODE_LEGACY));
            holder.textNewsDesc.setText(Html.fromHtml(news.getNews_desc(), android.text.Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.textNewsTopic.setText(Html.fromHtml(news.getNews_topic()));
            holder.textNewsDesc.setText(Html.fromHtml(news.getNews_desc()));
        }
        holder.textNewsAuthor.setText(news.getNews_author());
        holder.textNewsDate.setText(news.getNews_date());
    }

    @Override
    public int getItemCount() {
        return this.count;
    }
}

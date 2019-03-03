package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonNotes;

public class NotesAdapter extends BaseAdapter {

    private Context mContext;
    private int count;
    private JsonNotes.Notes notes;

    public NotesAdapter(Context context, JsonNotes.Notes notes) {
        mContext = context;
        this.notes = notes;
        this.count = notes.getListNotes().size();
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return count;
    }

    public String getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //TODO Change to NOTES !!!

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.item_news_new, parent, false);

        TextView textNewsTopic = (TextView) row.findViewById(R.id.textNewsTopic);
        TextView textNewsDesc = (TextView) row.findViewById(R.id.textNewsDesc);
        TextView textNewsAuthor = (TextView) row.findViewById(R.id.textNewsAuthor);
        TextView textNewsDate = (TextView) row.findViewById(R.id.textNewsDate);

        //textNewsTopic.setText(Html.fromHtml(notes.getListNews().get(position).getNews_topic()));
        //textNewsDesc.setText(Html.fromHtml(notes.getListNews().get(position).getNews_desc()));
        //textNewsAuthor.setText(notes.getListNews().get(position).getNews_author());
        //textNewsDate.setText(notes.getListNews().get(position).getNews_date());
        return (row);
    }
}

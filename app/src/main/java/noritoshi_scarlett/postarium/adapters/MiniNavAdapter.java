package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import noritoshi_scarlett.postarium.R;

public class MiniNavAdapter extends BaseAdapter {

    private Context mContext;
    private TypedArray ids;
    private TypedArray icons;

    public MiniNavAdapter(Context context) {

        mContext = context;
        this.ids = context.getResources().obtainTypedArray(R.array.mini_menu_ids);
        this.icons = context.getResources().obtainTypedArray(R.array.mini_menu_icons);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return ids.length();
    }

    public int getMenuId(int position) {
        return ids.getResourceId(position, 0);
    }

    public int getItemPosition(int id) {
        for(int i = 0; i < ids.length(); i++) {
            if (ids.getResourceId(i, 0) == id) {
                return i;
            }
        }
        return -1;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public String getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.item_mini_nav, parent, false);
        ImageView icon;
        icon = (ImageView) row.findViewById(R.id.imgIcon);
        icon.setImageResource(icons.getResourceId(position, -1));

        return (row);
    }
}
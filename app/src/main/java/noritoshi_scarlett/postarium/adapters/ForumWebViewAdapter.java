package noritoshi_scarlett.postarium.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.fragments.ForumWebViewFragment;


public class ForumWebViewAdapter extends RecyclerView.Adapter<ForumWebViewAdapter.ViewHolder> {

    final static private int KEY_SITE_NAME = 0;
    final static private int KEY_SITE_LOGIN = 1;
    final static private int KEY_SITE_URL = 2;

    private int selectedItem = -1;
    private List<SparseArray<String>> list;
    private ForumWebViewFragment listener;

    public interface ForumWebViewTabClickListener {
        void onBtnTabClick(String url);
        void emptyList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Button btnTab;
        private Button btnDelete;
        private ViewHolder(View itemView) {
            super(itemView);
            btnTab = itemView.findViewById(R.id.btnTab);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public ForumWebViewAdapter(SparseArray<String> record, ForumWebViewFragment listener) {
        this.listener = listener;
        this.list = new ArrayList<>();
        this.list.add(record);
    }

    @Override
    public int getItemCount() {return list.size();}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View onlineView = inflater.inflate(R.layout.item_forum_webview_btntab, parent, false);
        return new ViewHolder(onlineView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String name = list.get(position).get(KEY_SITE_NAME);
        if (name.length() > 25) {
            name =  name.substring(0, 20).trim() + "…";
        }
        holder.btnTab.setText(String.format("%s {%s)", name, list.get(position).get(KEY_SITE_LOGIN)));
        holder.btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem = holder.getAdapterPosition();
                listener.onBtnTabClick(list.get(holder.getAdapterPosition()).get(KEY_SITE_URL));
                notifyItemRangeChanged(0, getItemCount());
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = holder.getAdapterPosition();
                boolean haveNext = (list.size() > currentItem + 1);
                if (list.get(currentItem) != null) {
                    list.remove(currentItem);
                    notifyItemRemoved(currentItem);
                    if (list.size() == 0) {
                        listener.emptyList();
                    } else {
                        if (haveNext) {
                            selectedItem = currentItem;
                        } else {
                            selectedItem = currentItem - 1;
                        }
                        notifyItemChanged(selectedItem);
                        listener.onBtnTabClick(list.get(selectedItem).get(KEY_SITE_URL));
                    }
                }
            }
        });

        if (selectedItem == position) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnTab.setEnabled(false);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnTab.setEnabled(true);
        }
    }

    public void addBtnTab(SparseArray<String> record) {
        if (list != null) {
            list.add(record);
            selectedItem = list.size() - 1;
            notifyItemInserted(list.size() - 1);
            notifyItemRangeChanged(0, list.size());
        }
    }

    public boolean findBtnWithTab(String name) {
        if (this.list != null) {
            for (int i = 0; i < list.size(); i++) {
                if ( (this.list.get(i)).get(KEY_SITE_NAME).equals(name)) {
                    if (selectedItem >= 0) {
                        notifyItemChanged(selectedItem);
                    }
                    selectedItem = i;
                    notifyItemChanged(i);
                    listener.onBtnTabClick(list.get(i).get(KEY_SITE_URL));
                    return true;
                }
            }
        }
        return false;
    }

    public void selectBtnTab(int position) {
        if (list != null) {
            if (list.get(position) != null) {
                selectedItem = position;
                notifyItemChanged(position);
                listener.onBtnTabClick(list.get(position).get(KEY_SITE_URL));
            }
        }
    }

}

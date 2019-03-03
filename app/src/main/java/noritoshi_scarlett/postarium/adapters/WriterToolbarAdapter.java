package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import noritoshi_scarlett.postarium.R;

public class WriterToolbarAdapter extends RecyclerView.Adapter<WriterToolbarAdapter.ViewHolder> {

    private Context context;
    private TypedArray drawable_main;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageButton btnOption;

        private ViewHolder(View itemView) {
            super(itemView);
            btnOption = itemView.findViewById(R.id.btnOption);
        }
    }

    public WriterToolbarAdapter(Context context) {
        this.context = context;
        this.drawable_main = this.context.getResources().obtainTypedArray(R.array.forum_toolbar_icons);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View onlineView = inflater.inflate(R.layout.item_forum_toolbar, parent, false);
        return new ViewHolder(onlineView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.btnOption.setImageResource(drawable_main.getResourceId(position, -1));
    }

    @Override
    public int getItemCount() {
        return drawable_main.length();
    }
}

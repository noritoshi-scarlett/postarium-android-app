package noritoshi_scarlett.postarium.adapters;

import android.support.annotation.NonNull;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.fragments.LibraryShelfFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import noritoshi_scarlett.postarium.R;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private ArrayList<JsonLibrary.Forum> library;
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;
    private RecyclerView recyclerView;
    private LibraryShelfFragment libraryFragment;

    public interface ExpandedItemInterface {
        void isShownListener(JsonLibrary.Forum forum);
        void isHiddenListener(JsonLibrary.Forum forum);
    }

    // elementy z item layoutu
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textForumName, textForumDesc, textForumUrl;

        public ViewHolder(View view) {
            super(view);
            textForumName = view.findViewById(R.id.textForumName);
            textForumDesc = view.findViewById(R.id.textForumDesc);
            textForumUrl = view.findViewById(R.id.textForumUrl);
        }
    }

    // konstruktor dostarczający zasoby do wyświetlenia
    public LibraryAdapter(ArrayList<JsonLibrary.Forum> library, LibraryShelfFragment libraryFragment, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.library = library;
        this.libraryFragment = libraryFragment;
    }

    // konstrukor wiązący layout z row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_forum, parent, false);

        return new ViewHolder(itemView);
    }

    // bindowanie zasobów z interfejsem
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final JsonLibrary.Forum forum = library.get(position);
        holder.textForumName.setText(forum.getForum_title());
        holder.textForumDesc.setText(forum.getForum_desc());
        holder.textForumUrl.setText(forum.getForum_url());

        // animacja
       // final ChangeBounds transition = new ChangeBounds();
        //transition.setDuration(250);

        final boolean isExpanded = (position == mExpandedPosition);
        holder.textForumDesc.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        if (isExpanded) {
            if (libraryFragment != null) {
                libraryFragment.isShownListener(forum);
                previousExpandedPosition = position;
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(recyclerView);//, transition);
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(holder.getAdapterPosition());
                // listener do pokazywania/ukrywania FAB
                if (libraryFragment != null) {
                    if (isExpanded) {
                        libraryFragment.isHiddenListener(forum);
                    } else {
                        libraryFragment.isShownListener(forum);
                    }
                }
            }
        });
    }

    // podmiana danych
    public void swap(ArrayList<JsonLibrary.Forum> library){
        this.library = library;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return library.size();
    }
}

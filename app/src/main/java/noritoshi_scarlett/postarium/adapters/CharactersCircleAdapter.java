package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.fragments.CharactersFragment;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.R;

public class CharactersCircleAdapter extends RecyclerView.Adapter<CharactersCircleAdapter.ViewHolder> {

    private Context context;
    private int count;
    private ArrayList<JsonCharacters.Character> charactersCircle ;
    private CharactersFragment fragment;

    public interface MenuItemInterface {
        void showCharProfile(JsonCharacters.Character character);
        void showCharSettings(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textCharTag;
        TextView textCharLogin;

        public ViewHolder(View itemView) {
            super(itemView);
            textCharTag = (TextView) itemView.findViewById(R.id.textCharTag);
            textCharLogin = (TextView) itemView.findViewById(R.id.textCharLogin);
        }
    }

    public CharactersCircleAdapter(Context context, ArrayList<JsonCharacters.Character> charactersCircle, CharactersFragment fragment) {
        this.charactersCircle = charactersCircle;
        this.count = this.charactersCircle.size();
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_characters_circle, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final JsonCharacters.Character character = charactersCircle.get(position);
        final int finalPosition = position;

        holder.textCharTag.setText(character.getChar_tag());
        holder.textCharLogin.setText(character.getChar_login());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(view.getContext(), R.style.AppTheme_PopupOverlay);
                PopupMenu popup = new PopupMenu(wrapper, holder.itemView);
                popup.getMenuInflater().inflate(R.menu.character_menu, popup.getMenu());
                        MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) popup.getMenu(), holder.itemView);
                menuHelper.setForceShowIcon(true);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuCharProfile:
                                fragment.showCharProfile(character);
                                break;

                            case R.id.menuCharSettings:
                                fragment.showCharSettings(finalPosition);
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
                menuHelper.show();
            }
        });

    }

    @Override
    public int getItemCount() { return count; }
}

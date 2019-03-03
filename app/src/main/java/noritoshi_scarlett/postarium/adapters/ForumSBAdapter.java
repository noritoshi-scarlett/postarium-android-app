package noritoshi_scarlett.postarium.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.activities.ForumActivity;
import noritoshi_scarlett.postarium.libraries.URLImageParser;


public class ForumSBAdapter extends RecyclerView.Adapter<ForumSBAdapter.ViewHolder> {

    private static final int ITEM_TYPE_THEY = 0;
    private static final int ITEM_TYPE_THEY_MERGE = 1;
    private static final int ITEM_TYPE_ME = 2;
    private static final int ITEM_TYPE_ME_LAST = 3;

    private static final int DATE_SAME_DAY = 0;
    private static final int DATE_NEW_DAY = 1;
    private static final int DATE_MORE_THAN_10_MIN = 2;

    private JsonArray list;
    private Context context;
    private Fragment listener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView forumSBMsgAuthor;
        private TextView forumSBMsgDate;
        private TextView forumSBMsgDescText;
        private Button forumSBMsgMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            forumSBMsgAuthor = itemView.findViewById(R.id.forumSBMsgAuthor);
            forumSBMsgDate = itemView.findViewById(R.id.forumSBMsgDate);
            forumSBMsgDescText = itemView.findViewById(R.id.forumSBMsgDescText);
            forumSBMsgMenu = itemView.findViewById(R.id.forumSBMsgMenu);
        }
    }

    public class ViewHolderWithTag extends ViewHolder {

        private Button forumSBMsgTag;

        public ViewHolderWithTag(View itemView) {
            super(itemView);
            forumSBMsgTag = itemView.findViewById(R.id.forumSBMsgTag);
        }
    }

    public ForumSBAdapter(JsonArray list, Context context, Fragment listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM_TYPE_THEY: {
                contactView = inflater.inflate(R.layout.item_forum_sb_text, parent, false);
                return new ViewHolderWithTag(contactView);
            }
            case ITEM_TYPE_THEY_MERGE: {
                contactView = inflater.inflate(R.layout.item_forum_sb_text_merge, parent, false);
                return new ViewHolder(contactView);
            }
            case ITEM_TYPE_ME: {
                contactView = inflater.inflate(R.layout.item_forum_sb_text_me, parent, false);
                return new ViewHolder(contactView);
            }
            case ITEM_TYPE_ME_LAST: default: {
                contactView = inflater.inflate(R.layout.item_forum_sb_text_me_last, parent, false);
                return new ViewHolderWithTag(contactView);
            }
        }
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;

        boolean rtnValue = false;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        // color is light
        if (brightness >= 200) {
            rtnValue = true;
        }

        return rtnValue;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        // dane z jsona
        final JsonObject current = list.get(position).getAsJsonObject();
        String user = current.get("n").toString();
        //String user_link = forum.getForum_url() + current.get("u").getAsString();
        final String message = current.get("m").toString();
        String date = current.get("t").toString();
        // holder
        // ustawianie wartości
        if (holder.getItemViewType() == 0 || holder.getItemViewType() == 3) {
            holder.forumSBMsgAuthor.setText(user);
            if (date.contains("Wczoraj") || date.contains("Dzisiaj")) {
                holder.forumSBMsgDate.setText(String.format("[%s", date.substring(9, date.length())));
            } else {
                holder.forumSBMsgDate.setText(date);
            }
            String f_name = user.split(" ")[0];
            if (f_name.length() < 6) {
                ((ViewHolderWithTag)holder).forumSBMsgTag.setText(f_name);
                int fontSize;
                switch (f_name.length()) {
                             case 5: fontSize = 12; break;
                             case 4: fontSize = 13; break;
                    case 3: default: fontSize = 14; break;
                }

                ((ViewHolderWithTag)holder).forumSBMsgTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
            } else {
                ((ViewHolderWithTag)holder).forumSBMsgTag.setText(f_name.substring(0, 4));
                ((ViewHolderWithTag)holder).forumSBMsgTag.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            }
            String color = current.get("color").getAsString();
            if (!color.equals("")) {
                if (isBrightColor(Color.parseColor(color))) {
                    ((ViewHolderWithTag)holder).forumSBMsgTag.setTextColor(Color.BLACK);
                } else {
                    ((ViewHolderWithTag)holder).forumSBMsgTag.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                }
                ((ViewHolderWithTag)holder).forumSBMsgTag.setBackgroundColor(Color.parseColor(color));
            } else {
                ((ViewHolderWithTag)holder).forumSBMsgTag.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
                ((ViewHolderWithTag)holder).forumSBMsgTag.setBackgroundColor(ContextCompat.getColor(context, R.color.buttonPrimary));
            }
        }

        Spanned textImg;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textImg = Html.fromHtml(
                    message,
                    android.text.Html.FROM_HTML_MODE_LEGACY,
                    new URLImageParser(holder.forumSBMsgDescText, context),
                    null);
        } else {
            textImg = Html.fromHtml(
                    message,
                    new URLImageParser(holder.forumSBMsgDescText, context),
                    null);
        }
        holder.forumSBMsgDescText.setText(textImg);
        holder.forumSBMsgDescText.setMaxLines(20);

        if (holder.getItemViewType() == 0 || holder.getItemViewType() == 3) {
            ((ViewHolderWithTag)holder).forumSBMsgTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String link = Postarium.getPostariData().getPickedForum().getForum_url()
                            + "" + list.get(holder.getAdapterPosition()).getAsJsonObject().get("u").toString();
                    final String name = list.get(holder.getAdapterPosition()).getAsJsonObject().get("n").toString();
                    ForumActivity.displayPopupUser(v, context, listener, name, link);
                }
            });
        }
        // ustawienie przycisku edyci i koloru tła własnych wiadomości
//        if (charactersName.indexOf(user) != -1) {
//            //TODO sensowniejszy wygląd i umieszczenie
//            //holder.forumSBMsgMenu.setVisibility(View.VISIBLE);
//            textHolder.forumSBMsgMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //TODO EDIT MSG
//                }
//            });
//        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        URLImageParser.clear(holder.forumSBMsgDescText);
    }

    @Override
    public int getItemViewType(int position) {
        JsonObject current = list.get(position).getAsJsonObject();
        return current.get("type").getAsInt();
    }

    public void addMessage(JsonArray messages) {

        if (messages != null && this.list != null) {

            int positionLast = this.list.size() - 1;

            // USUWANIE DUPLIKATOW
            int lastID = this.list.get(positionLast).getAsJsonObject().get("i").getAsInt();
            int newID;
            for (int i = 0; i < messages.size(); i++) {
                newID = messages.get(i).getAsJsonObject().get("i").getAsInt();
                if (newID <= lastID) {
                    messages.remove(i);
                }
            }

            if (this.list.size() > 0 && messages.size() > 0) {

                // PRZYGOTOWANIE MERGOWANIA
                if (this.list.get(positionLast) != null && messages.get(0) != null) { // JESLI ISNIEJE OSTATNI STARY I PIERWSZY NOWY

                    JsonObject last = this.list.get(positionLast).getAsJsonObject();
                    JsonObject fristNew = messages.get(0).getAsJsonObject();
                    String prevUser = last.get("u").getAsString();
                    String user = fristNew.get("u").getAsString();

                    String prevDate = last.get("t").getAsString();
                    String date = fristNew.get("t").getAsString();
                    int dateCycle = DATE_SAME_DAY;

                    // DATA TO DZIS LUB WCZORAJ
                    if (prevDate.length() < 16 && date.length() < 16) {
                        if (prevDate.contains("Wczoraj") && date.contains("Dzisiaj")) {
                            dateCycle = DATE_NEW_DAY;
                        }
                    }

                    if (dateCycle == DATE_SAME_DAY) {
                        if (last.get("type").getAsInt() > ITEM_TYPE_THEY_MERGE) {
                            // OSTATNI JEST MOIM WPISEM (TAK JAK PIERWSZY NOWY) - ZMIEN STRUKTURE OST.
                            if (user.equals(prevUser)) {
                                // BYL OSTATNI -> BEDZIE ZWYKLYM
                                if (last.get("type").getAsInt() == ITEM_TYPE_ME_LAST) {
                                    last.remove("type");
                                    last.addProperty("type", ITEM_TYPE_ME);
                                    notifyItemChanged(positionLast);
                                }
                            }
                        } else {
                            if (prevUser.equals(user)) { // OSTATNI JEST WPISEM TEGO SAMEGO USERA CO PIERWSZY NOWY - ZMIEN SRUKTURE OSTATNIEGO
                                fristNew.addProperty("type", ITEM_TYPE_THEY_MERGE);
                            }
                        }
                    }

                    this.list.addAll(messages);
                    notifyItemRangeInserted(positionLast + 1, messages.size());
                }
            }
        }
    }

}

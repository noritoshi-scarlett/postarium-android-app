package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ForumSBAdapter;

public class ForumChatBoxFragment extends Fragment {

    public static final int ITEM_TYPE_THEY = 0;
    public static final int ITEM_TYPE_THEY_MERGE = 1;
    public static final int ITEM_TYPE_ME = 2;
    public static final int ITEM_TYPE_ME_LAST = 3;

    private static final int DATE_SAME_DAY = 0;
    private static final int DATE_NEW_DAY = 1;
    private static final int DATE_MORE_THAN_10_MIN = 2;

    private static final String SB_MODE_ADD = "add";
    private static final String SB_MODE_EDIT = "edit";
    private static final String SB_MODE_DELETE = "delete";
    private static final String SB_IS_REFRESH = "refresh";
    private static final int REFRESH_DELAYED = 5000;

    private ArrayList<String> charactersName;

    private AppCompatEditText SBEditText;
    private FloatingActionButton SBBtnSend;
    private RecyclerView SBList;

    private Context context;
    private int g_lastMessageID;

    private ForumSBAdapter adapter;

    private Handler handler;
    private Runnable messageLoading;
    private int numberOfFailureConnections = 0;

    public ForumChatBoxFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();

        this.charactersName = new ArrayList<>();
        for(JsonCharacters.Character character : Postarium.getPostariData().getInstance().getCharacters()) {
            this.charactersName.add(character.char_login);
        }

        View v = inflater.inflate(R.layout.fragment_forum_chat_box, container, false);
        SBEditText = v.findViewById(R.id.forumSbEdtText);
        SBBtnSend = v.findViewById(R.id.forumSbBtnSend);
        SBList = v.findViewById(R.id.forumSBList);

        g_lastMessageID = 0;

        messageLoading = new Runnable() {
            public void run() {
                RequestBody formBody = new FormBody.Builder()
                        .add("last", Integer.toString(g_lastMessageID))
                        .build();
                getMessages(formBody, SB_IS_REFRESH);
            }
        };
        handler = new Handler();
        RequestBody formBody = new FormBody.Builder()
                .add("last", Integer.toString(g_lastMessageID))
                .build();
        getMessages(formBody, SB_IS_REFRESH);

        SBBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        SBEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        SBEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        SBEditText.setOnEditorActionListener(new AppCompatEditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(messageLoading);
    }

    public void scheduleGetMessages() {
        handler.postDelayed(messageLoading, REFRESH_DELAYED);
        if (numberOfFailureConnections > 10) {
            // TODO -> Break refreshing
        }
    }

    public void sendMessage() {

        int char_id = Postarium.getPostariData().getChar_id();
        String message = SBEditText.getText().toString();
        if (! message.equals("")) {
            SBBtnSend.setEnabled(false);
            RequestBody formBody = new FormBody.Builder()
                    .add("last", Integer.toString(g_lastMessageID))
                    .add("mode", SB_MODE_ADD)
                    .add("uid", Integer.toString(char_id))
                    .add("message", message)
                    .build();
            getMessages(formBody, SB_MODE_ADD);
        }
    }

    public void getMessages(final RequestBody formBody, final String mode) {

        int char_id = Postarium.getPostariData().getChar_id();

        Postarium.WebRequests.post(
                "forum/viewSB/" + Postarium.getPostariData().getPickedForum().getForum_name()
                        + "/" + char_id + ".html", formBody, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //TODO
                        if (! SBBtnSend.isEnabled()) { SBBtnSend.setEnabled(true); }
                        numberOfFailureConnections++;
                        scheduleGetMessages();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {

                            String resp = response.body().string();
                            if (! resp.equals("\r\n\r\n")) {
                                JsonParser parser = new JsonParser();
                                JsonObject jObject = parser.parse(resp).getAsJsonObject();

                                final JsonArray preparedMessages = prepareMessages(jObject);

                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (adapter == null) {
                                                adapter = new ForumSBAdapter(preparedMessages, context, ForumChatBoxFragment.this);
                                                SBList.setAdapter(adapter);
                                                SBList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                SBList.scrollToPosition(SBList.getAdapter().getItemCount() - 1);
                                            } else {
                                                int last_visible = ((LinearLayoutManager) SBList.getLayoutManager()).findLastVisibleItemPosition();
                                                boolean needScroll = (last_visible >= SBList.getAdapter().getItemCount() -1);

                                                adapter.addMessage(preparedMessages);

                                                // TODO -> smoth
                                                if (needScroll) {
                                                    SBList.smoothScrollToPosition(SBList.getAdapter().getItemCount() - 1);
                                                }
                                                // W ELSE
                                                // powiadomienie
                                                // ikonka wiadomosci jsli nie ma scrolla
                                            }
                                            if (mode.equals(SB_MODE_ADD)) {
                                                SBEditText.setText("");
                                                SBBtnSend.setEnabled(true);
                                                SBList.scrollToPosition(SBList.getAdapter().getItemCount() - 1);
                                            }
                                        }
                                    });
                                }
                            }
                            scheduleGetMessages();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!SBBtnSend.isEnabled()) {
                                            SBBtnSend.setEnabled(true);
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private JsonArray prepareMessages(JsonObject jObject) {

        String prevUser = "";
        String prevDate = "";
        int dateCycle;
        JsonArray d = jObject.get("d").getAsJsonArray();

        // OGRANICZENIE DO 200 WPISOW
        int underlimit = d.size() - 200;
        if (underlimit > 0) {
            for (int i = 0; i < underlimit; i++) {
                d.remove(i);
            }
        }

        for (int x = 0; x < d.size(); x++) {
            JsonObject current = d.get(x).getAsJsonObject();
            String user = current.get("n").toString();
            String message = current.get("m").toString();
            String colorMsg = current.get("c").toString();
            g_lastMessageID = current.get("i").getAsInt();
            String date = current.get("t").toString();
            String user_link = current.get("u").toString();
            current.remove("u");
            current.addProperty("u", user_link.replace("&amp;", "&"));
            dateCycle = DATE_SAME_DAY;

            // USTAWIENIE TYPU WIDOKU

            // TO JEST MOJ WPIS...
            if (charactersName.indexOf(user) != -1) {
                // ...I TEJ SAMEJ POSTACI CO POPRZEDNI
                if (user.equals(prevUser)) {
                    // OSTATNI ROBIL ZA CALY TO BEDZIE ZWKLYM
                    if (d.get(x - 1).getAsJsonObject().get("type").getAsInt() == ITEM_TYPE_ME_LAST) {
                        d.get(x - 1).getAsJsonObject().remove("type");
                        d.get(x - 1).getAsJsonObject().addProperty("type", ITEM_TYPE_ME);
                    }
                }
                // OBECNY NA RAZIE BEDZIE OSTATNIM
                current.addProperty("type", ITEM_TYPE_ME_LAST);
            // TO NIE JEST MOJ WPIS...
            } else {
                if (prevUser.equals(user)) {

                    // DATA TO DZIS LUB WCZORAJ
                    if (prevDate.length() > 12 && prevDate.length() < 16 && date.length() < 16) {
                        if (prevDate.contains("Wczoraj") && date.contains("Dzisiaj")) {
                            dateCycle = DATE_NEW_DAY;
                        } else {
                            String[] prevTimeString = prevDate.substring(9).split(":");
                            String[] currTimeString = date.substring(9).split(":");
                            int prevTimeMinutes, currTimeMinutes;
                            if (prevTimeString.length >1 && currTimeString.length > 1) {
                                prevTimeMinutes = Integer.parseInt(prevTimeString[1].replaceAll("[\\D]", ""));
                                currTimeMinutes = Integer.parseInt(currTimeString[1].replaceAll("[\\D]", ""));
                                if (prevTimeMinutes + 10 < currTimeMinutes) {
                                    dateCycle = DATE_MORE_THAN_10_MIN;
                                } else if (prevTimeMinutes > currTimeMinutes) {
                                    dateCycle = DATE_MORE_THAN_10_MIN;
                                }
                            }
                        }
                    }

                    if (dateCycle == DATE_SAME_DAY) {
                        current.addProperty("type", ITEM_TYPE_THEY_MERGE);
                    } else {
                        current.addProperty("type", ITEM_TYPE_THEY);
                    }
                } else {
                    current.addProperty("type", ITEM_TYPE_THEY);
                }
            }

            prevDate = date;
            prevUser = user;

            //poszukiwanie koloru
            String color = "";
            if (colorMsg.length() > 12) {
                int colorStart = colorMsg.indexOf("color:") + 6;
                if (colorMsg.charAt(colorStart) == '#') {
                    if (colorMsg.charAt(colorStart + 4) != ';'
                            && colorMsg.charAt(colorStart + 4) != ' '
                            && colorMsg.charAt(colorStart + 4) != '}') {
                        color = colorMsg.substring(colorStart, colorStart + 7);
                    } else {
                        color = colorMsg.substring(colorStart, colorStart + 4);
                        String temp = color.substring(0, 1) + color.substring(1, 2) + color.substring(1, 2);
                        temp += color.substring(2, 3) + color.substring(2, 3);
                        temp += color.substring(3, 4) + color.substring(3, 4);
                        color = temp;
                    }
                }
            }
            current.addProperty("color", color);
            // poszukiwanie img
            int tagStart = message.indexOf("<img");
            if (tagStart > -1) {
                // dodawanie adresu forum
                message = message.replace("src=\"", "src=\"" + Postarium.getPostariData().getPickedForum().getForum_url());
                message = message.replace("align=\"top\"", "");
                Log.d("img:", message);
                current.addProperty("m", message);

            }
        }
        return d;
    }
}

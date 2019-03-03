package noritoshi_scarlett.postarium.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumPostsMsgs;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import noritoshi_scarlett.postarium.jsonPojo.JsonNews;
import noritoshi_scarlett.postarium.jsonPojo.JsonNotes;

public class PostariumService extends IntentService {

    private static final String ACTION_LOAD_FROM_FILES = "noritoshi_scarlett.postarium.services.action.LOAD_FROM_FILES";

    public PostariumService() {
        super("PostariumService");
    }

    public static void startActionLoadFromFiles(Context context) {
        Intent intent = new Intent(context, PostariumService.class);
        intent.setAction(ACTION_LOAD_FROM_FILES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOAD_FROM_FILES.equals(action)) {
                loadFromFiles();
            }
        }
    }

    private void loadFromFiles() {
        SharedPreferences storage = getApplicationContext().getSharedPreferences("PostariumData", 0);
        String storedJson;
        Gson gson = new GsonBuilder().create();
        // Dane globalData
        storedJson = storage.getString("News", "");
        if (! storedJson.equals("")) {
            Postarium.getJsonDataStorage().setNews(gson.fromJson(storedJson, JsonNews.News.class));
        }
        // Dane userData
        storedJson = storage.getString("Library", "");
        if (! storedJson.equals("")) {
            Postarium.getJsonDataStorage().setLibrary(gson.fromJson(storedJson, JsonLibrary.Library.class));
        }
        storedJson = storage.getString("CharactersCircle", "");
        if (! storedJson.equals("")) {
            Postarium.getJsonDataStorage().setCharactersCircle(gson.fromJson(storedJson, JsonCharacters.CharactersCircle.class));
        }
        storedJson = storage.getString("Notes", "");
        if (! storedJson.equals("")) {
            Postarium.getJsonDataStorage().setNotes(gson.fromJson(storedJson, JsonNotes.Notes.class));
        }
        // Dane forumData
        JsonLibrary.Library library = Postarium.getJsonDataStorage().getLibrary();
        if (library != null) {
            if (library.getSubscribe() != null) {
                if (library.getSubscribe().size() < 0) {
                    for (JsonLibrary.Forum forum : library.getSubscribe()) {
                        storedJson = storage.getString("Forum" + forum.getForum_name() + "PostsMsgs", "");
                        if (! storedJson.equals("")) {
                            Postarium.getJsonDataStorage().setForumPostsMsgses(
                                    gson.fromJson(storedJson, JsonForumPostsMsgs.ForumPostsMsgs.class),
                                    forum.getForum_id());
                        }
                    }
                }
            }
        }
    }
}

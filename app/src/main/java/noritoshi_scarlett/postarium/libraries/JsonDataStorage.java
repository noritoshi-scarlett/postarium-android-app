package noritoshi_scarlett.postarium.libraries;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumComponents;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumPostsMsgs;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;
import noritoshi_scarlett.postarium.jsonPojo.JsonNews;
import noritoshi_scarlett.postarium.jsonPojo.JsonNotes;
import noritoshi_scarlett.postarium.jsonPojo.JsonOnline;

public class JsonDataStorage {

    private Context context;

    private JsonNews.News news;

    private JsonOnline.OnlineLists onlinePostarium;
    private JsonCharacters.CharactersCircle charactersCircle;
    private JsonLibrary.Library library;
    private JsonNotes.Notes notes;

    private SparseArray<JsonForumComponents.Online> jsonForumOnline;
    private SparseArray<JsonForumComponents.NewsArray> jsonForumNews;
    private SparseArray<JsonForumComponents.ShortcutsArray> jsonForumShortcuts;
    private SparseArray<JsonForumPostsMsgs.ForumPostsMsgs> forumPostsMsgses;
    private SparseArray<ArrayList<JsonForumPostsMsgs.Msg>> forumPrivMsgses;

    // TODO - przycisk do czyszczenia danych

    /**
     * konstruktor
     */
    public JsonDataStorage(Context context) {
        this.context = context;
        jsonForumOnline = new SparseArray<>();
        jsonForumNews = new SparseArray<>();
        jsonForumShortcuts = new SparseArray<>();
        forumPostsMsgses = new SparseArray<>();
        forumPrivMsgses = new SparseArray<>();
    }

    public JsonNews.News getNews() {
        return news;
    }
    public void setNews(JsonNews.News news) {
        this.news = news;
    }

    public JsonOnline.OnlineLists getOnlinePostarium() {
        return onlinePostarium;
    }
    public void setOnlinePostarium(JsonOnline.OnlineLists onlinePostarium) { this.onlinePostarium = onlinePostarium; }
    public JsonCharacters.CharactersCircle getCharactersCircle() { return charactersCircle; }
    public void setCharactersCircle(JsonCharacters.CharactersCircle charactersCircle) { this.charactersCircle = charactersCircle; }
    public JsonLibrary.Library getLibrary() {
        return library;
    }
    public void setLibrary(JsonLibrary.Library library) { this.library = library; }
    public JsonNotes.Notes getNotes() {
        return notes;
    }
    public void setNotes(JsonNotes.Notes notes) {
        this.notes = notes;
    }

    public JsonForumComponents.Online getForumOnline(int forum_id) {
        if (jsonForumOnline != null) {
            return jsonForumOnline.get(forum_id);
        } else { return null; }
    }
    public void setForumOnline(JsonForumComponents.Online forumOnline, int forum_id) {
        this.jsonForumOnline.put(forum_id, forumOnline);
    }
    public JsonForumComponents.NewsArray getForumNews(int forum_id) {
        if (jsonForumNews != null) {
            return jsonForumNews.get(forum_id);
        } else { return null; }
    }
    public void setForumNews(JsonForumComponents.NewsArray forumNews, int forum_id) {
        this.jsonForumNews.put(forum_id, forumNews);
    }
    public JsonForumComponents.ShortcutsArray getForumShortcuts(int forum_id) {
        if (jsonForumShortcuts != null) {
            return jsonForumShortcuts.get(forum_id);
        } else { return null; }
    }
    public void setForumShortcuts(JsonForumComponents.ShortcutsArray forumShortcuts, int forum_id) {
        this.jsonForumShortcuts.put(forum_id, forumShortcuts);
    }

    public JsonForumPostsMsgs.ForumPostsMsgs getForumPostsMsgses(int forum_id) {
        if (forumPostsMsgses != null) {
            return forumPostsMsgses.get(forum_id);
        } else { return null; }
    }
    public void setForumPostsMsgses(JsonForumPostsMsgs.ForumPostsMsgs forumPostsMsgses, int forum_id) {
        this.forumPostsMsgses.put(forum_id, forumPostsMsgses);

        // TODO -> dlaczego nie ma dwóch osobnych zmiennych na te dwie listy?
        JsonForumPostsMsgs.PrivMsgs listPW;
        listPW = forumPostsMsgses.getPrivMsgs();
        if (listPW.getOutboxList() != null) {
            for (JsonForumPostsMsgs.Msgs folder : listPW.getOutboxList()) {
                this.forumPrivMsgses.append(forum_id, folder.getMsgsList());
            }
        }
        if (listPW.getInboxList() != null) {
            for (JsonForumPostsMsgs.Msgs folder : listPW.getInboxList()) {
                this.forumPrivMsgses.append(forum_id, folder.getMsgsList());
            }
        }
    }

    public ArrayList<JsonForumPostsMsgs.Msg> getForumPrivMsgses(int forum_id) {
        if (forumPrivMsgses != null) {
            return forumPrivMsgses.get(forum_id);
        }
        else {
            return null;
        }
    }

    // Wywołuj przy zmianie konfiguracji danych
    public void saveToFile() {
        SharedPreferences storage = context.getSharedPreferences("PostariumData", 0);
        SharedPreferences.Editor editor = storage.edit();
        String storingJson;
        Gson gson = new GsonBuilder().create();
        // Dane globalData
        if (getNews() != null) {
            storingJson = gson.toJson(getNews());
            editor.putString("News", storingJson);
            editor.apply();
        }
        // Dane userData
        if (getLibrary() != null) {
            storingJson = gson.toJson(getLibrary());
            editor.putString("Library", storingJson);
            editor.apply();
        }
        if (getCharactersCircle() != null) {
            storingJson = gson.toJson(getCharactersCircle());
            editor.putString("CharactersCircle", storingJson);
            editor.apply();
        }
        if (getNotes() != null) {
            storingJson = gson.toJson(getNotes());
            editor.putString("Notes", storingJson);
            editor.apply();
        }
        // Dane forumData
        if (getLibrary() != null) {
            if (getLibrary().getSubscribe().size() < 0) {
                for (JsonLibrary.Forum forum : getLibrary().getSubscribe()) {
                    if (forumPostsMsgses.get(forum.getForum_id()) != null) {
                        storingJson = gson.toJson(forumPostsMsgses.get(forum.getForum_id()));
                        editor.putString("Forum" + forum.getForum_name() + "PostsMsgs", storingJson);
                        editor.apply();
                    }
                }
            }
        }
    }
}

package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.ArrayList;

public class JsonSearchResults {

    private SearchResults searchResults;

    public JsonSearchResults() { }

    public void parse(String jsonResults) {

        Gson gson = new GsonBuilder().create();
        searchResults = gson.fromJson(jsonResults, SearchResults.class);
    }

    public SearchResults getSearchResults() {return searchResults;}


    public class SearchResults {

        private int status;
        private ArrayList<Result> records;

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public ArrayList<Result> getRecords() { return records; }
        public void setRecords(ArrayList<Result> records) { this.records = records; }
    }

    public class Result {

        private int user_id;
        private String user_name;
        private String user_avatar;
        private String char_tag;
        private String char_login;
        private String char_forum;

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getUser_name() { return user_name; }
        public void setUser_name(String user_name) { this.user_name = user_name;}

        public String getUser_avatar() { return user_avatar; }
        public void setUser_avatar(String user_avatar) { this.user_avatar = user_avatar; }

        public String getChar_tag() { return char_tag; }
        public void setChar_tag(String char_tag) { this.char_tag = char_tag; }

        public String getChar_login() { return char_login; }
        public void setChar_login(String char_login) { this.char_login = char_login; }

        public String getChar_forum() { return char_forum; }
        public void setChar_forum(String char_forum) {this.char_forum = char_forum; }
    }
}

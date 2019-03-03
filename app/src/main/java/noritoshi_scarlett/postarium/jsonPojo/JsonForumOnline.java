package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonForumOnline {

    private UsersOnline usersOnline;

    public JsonForumOnline(String jsonUserOnline) {
        parse(jsonUserOnline);
    }

    public void parse(String jsonUserOnline) {

        Gson gson = new GsonBuilder().create();
        usersOnline = gson.fromJson(jsonUserOnline, UsersOnline.class);
    }

    public UsersOnline getUsersOnline() {
        return usersOnline;
    }

    public class UsersOnline {

        @SerializedName("Online")
        private ArrayList<User> usersList;

        public ArrayList<User> getUsersList() { return usersList; }
        public void setUsersList(ArrayList<User> usersList) { this.usersList = usersList; }
    }

    public class User {

        private int id;
        private String tag;
        private String username;
        private String colorText;
        private String colorBack;
        private String profileLink;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getColorText() { return colorText; }
        public void setColorText(String colorText) { this.colorText = colorText; }

        public String getColorBack() { return colorBack; }
        public void setColorBack(String colorBack) { this.colorBack = colorBack; }

        public String getProfileLink() { return profileLink; }
        public void setProfileLink(String profileLink) { this.profileLink = profileLink; }
    }
}

package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.adapters.ForumSBAdapter;

public class JsonUserProfile {

    private UserProfile userProfile;

    public JsonUserProfile() { }

    public void parse(String jsonResults) {

        Gson gson = new GsonBuilder().create();
        userProfile = gson.fromJson(jsonResults, UserProfile.class);
    }

    public UserProfile getUserProfile() { return userProfile; }

    public class UserProfile {

        private int status;
        private ArrayList<Profile> profile;
        private ArrayList<JsonLibrary.Forum> forums;
        private ArrayList<JsonCharacters.Character> characters;
        private ArrayList<SimpleProfile> friends;
        private String friend_state;

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public ArrayList<Profile> getProfile() { return profile; }
        public void setProfile(ArrayList<Profile> profile) { this.profile = profile; }

        public ArrayList<JsonLibrary.Forum> getForums() { return forums; }
        public void setForums(ArrayList<JsonLibrary.Forum> forums) { this.forums = forums; }

        public ArrayList<JsonCharacters.Character> getCharacters() { return characters; }
        public void setCharacters(ArrayList<JsonCharacters.Character> characters) { this.characters = characters; }

        public ArrayList<SimpleProfile> getFriends() { return friends; }
        public void setFriends(ArrayList<SimpleProfile> friends) { this.friends = friends; }

        public String getFriend_state() { return friend_state; }
        public void setFriend_state(String friend_state) { this.friend_state = friend_state; }
    }


    public class Profile extends SimpleProfile {

        private String user_email;
        private String user_desc;
        private int user_visible;
        private int user_getinvite;
        private int user_getmsg;

        public String getUser_email() { return user_email; }
        public void setUser_email(String user_email) { this.user_email = user_email; }

        public String getUser_desc() { return user_desc; }
        public void setUser_desc(String user_desc) { this.user_desc = user_desc; }

        public int getUser_visible() { return user_visible; }
        public void setUser_visible(int user_visible) { this.user_visible = user_visible; }

        public int getUser_getinvite() { return user_getinvite; }
        public void setUser_getinvite(int user_getinvite) { this.user_getinvite = user_getinvite; }

        public int getUser_getmsg() { return user_getmsg; }
        public void setUser_getmsg(int user_getmsg) { this.user_getmsg = user_getmsg; }
    }

    public class SimpleProfile {

        private int user_id;
        private String user_name;
        private String user_avatar;

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) { this.user_id = user_id; }

        public String getUser_name() { return user_name; }
        public void setUser_name(String user_name) { this.user_name = user_name; }

        public String getUser_avatar() { return user_avatar; }
        public void setUser_avatar(String user_avatar) { this.user_avatar = user_avatar; }
    }

}

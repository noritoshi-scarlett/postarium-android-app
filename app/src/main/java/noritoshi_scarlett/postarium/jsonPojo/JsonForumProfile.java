package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class JsonForumProfile {

    private ForumProfile profile;

    public JsonForumProfile() { }

    public void parse(String jsonResults) {

        Gson gson = new GsonBuilder().create();
        profile = gson.fromJson(jsonResults, ForumProfile.class);
    }

    public ForumProfile getForumProfile() { return profile; }

    public class ForumProfile {

        private Profile profile;

        public Profile getProfile() { return profile; }
        public void setProfile(Profile profile) { this.profile = profile; }
    }


    public class Profile {

        private String username;
        private String avatar;
        private String rank;
        private ArrayList<Record> info;
        private ArrayList<Record> contact;
        private ArrayList<Record> posts;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }

        public String getRank() { return rank; }
        public void setRank(String rank) { this.rank = rank; }

        public ArrayList<Record> getInfo() { return info; }
        public void setInfo(ArrayList<Record> info) { this.info = info; }

        public ArrayList<Record> getContact() { return contact; }
        public void setContact(ArrayList<Record> contact) { this.contact = contact; }

        public ArrayList<Record> getPosts() { return posts; }
        public void setPosts(ArrayList<Record> posts) { this.posts = posts; }
    }

    public class Record {

        private String name;
        private String value;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

}

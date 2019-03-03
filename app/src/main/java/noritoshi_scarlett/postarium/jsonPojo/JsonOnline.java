package noritoshi_scarlett.postarium.jsonPojo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonOnline {

    private Online online;

    public JsonOnline(String jsonOnline) { parse(jsonOnline); }

    public void parse(String jsonOnline) {

        Gson gson = new GsonBuilder().create();
        online = gson.fromJson(jsonOnline, Online.class);
    }

    public Online getOnline() {
        return online;
    }

    public class Online {

        @SerializedName("online")
        private OnlineLists lists;

        public OnlineLists getLists() { return lists; }
        public void setLists(OnlineLists lists) { this.lists = lists; }
    }

    public class OnlineLists {

        @SerializedName("48h")
        private ArrayList<User> users48;
        @SerializedName("10m")
        private ArrayList<User> users10;

        public ArrayList<User> getUsers48() {
            return users48;
        }
        public void setUsers48(ArrayList<User> users48) {
            this.users48 = users48;
        }

        public ArrayList<User> getUsers10() {
            return users10;
        }
        public void setUsers10(ArrayList<User> users10) {
            this.users10 = users10;
        }
    }

    public class User {
        private int user_id;
        private String user_name;
        private String user_lastactivedate;
        private String user_signindate;

        public int getUser_id() { return user_id; }
        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }
        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_lastactivedate() {
            return user_lastactivedate;
        }
        public void setUser_lastactivedate(String user_lastactivedate) { this.user_lastactivedate = user_lastactivedate; }

        public String getUser_signindate() {
            return user_signindate;
        }
        public void setUser_signindate(String user_signindate) { this.user_signindate = user_signindate; }
    }
}

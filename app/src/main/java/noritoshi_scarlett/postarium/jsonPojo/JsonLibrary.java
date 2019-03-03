package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonLibrary {

    private Library library;

    public JsonLibrary() { }

    public void parse(JsonElement jsonLibrary) {

        Gson gson = new GsonBuilder().create();
        library = gson.fromJson(jsonLibrary, Library.class);
    }
    public void parse(String jsonCharacters) {

        Gson gson = new GsonBuilder().create();
        library = gson.fromJson(jsonCharacters, Library.class);
    }

    public Library getLibrary() {
        return library;
    }

    public class Library {

        @SerializedName("subscribe")
        private ArrayList<Forum> Subscribe;
        @SerializedName("unsubscribe")
        private ArrayList<Forum> Unsubscribe;
        @SerializedName("beta")
        private ArrayList<Forum> Beta;

        public ArrayList<Forum> getSubscribe() { return Subscribe; }
        public void setSubscribe(ArrayList<Forum> subscribe) { Subscribe = subscribe; }

        public ArrayList<Forum> getUnsubscribe() { return Unsubscribe; }
        public void setUnsubscribe(ArrayList<Forum> unsubscribe) { Unsubscribe = unsubscribe; }

        public ArrayList<Forum> getBeta() { return Beta; }
        public void setBeta(ArrayList<Forum> beta) { Beta = beta; }
    }

    public class Forum {
        private int forum_id;
        private String forum_title;
        private String forum_icon;
        private String forum_name;
        private String forum_desc;
        private String forum_url;

        public int getForum_id() {return forum_id;}
        public void setForum_id(int forum_id) {this.forum_id = forum_id;}

        public String getForum_title() {return forum_title;}
        public void setForum_title(String forum_title) {this.forum_title = forum_title;}

        public String getForum_icon() {return forum_icon;}
        public void setForum_icon(String forum_icon) {this.forum_icon = forum_icon;}

        public String getForum_name() {return forum_name;}
        public void setForum_name(String forum_name) {this.forum_name = forum_name;}

        public String getForum_desc() {return forum_desc;}
        public void setForum_desc(String forum_desc) {this.forum_desc = forum_desc;}

        public String getForum_url() {return forum_url;}
        public void setForum_url(String forum_url) {this.forum_url = forum_url;}

        //to display object as a string in spinner
        @Override
        public String toString() {return getForum_title();}

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Forum){
                Forum c = (Forum )obj;
                if(c.getForum_title().equals(forum_title) && c.getForum_id()==forum_id ) return true;
            }
            return false;
        }


    }
}

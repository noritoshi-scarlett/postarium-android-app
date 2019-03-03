package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonCharacters {

    private CharactersCircle characters;

    public JsonCharacters() {
    }

    public void parse(JsonElement jsonLibrary) {

        Gson gson = new GsonBuilder().create();
        characters = gson.fromJson(jsonLibrary, CharactersCircle.class);
    }

    public void parse(String jsonCharacters) {

        Gson gson = new GsonBuilder().create();
        characters = gson.fromJson(jsonCharacters, CharactersCircle.class);
    }

    public CharactersCircle getCharacters() {
        return characters;
    }

    public class CharactersCircle {

        @SerializedName("Characters")
        private ArrayList<CharactersForum> ListCharactersCircle;

        public ArrayList<CharactersForum> getListCharactersCircle() {
            return ListCharactersCircle;
        }
        public void setListCharactersCircle(ArrayList<CharactersForum> listCharactersCircle) {
            ListCharactersCircle = listCharactersCircle;
        }
    }

    public class CharactersForum {

        private int forum_id;
        private String forum_title;
        @SerializedName("Character")
        private ArrayList<Character> ListCharactersForForum;

        public int getForum_id() {
            return forum_id;
        }
        public void setForum_id(int forum_id) {
            this.forum_id = forum_id;
        }

        public String getForum_title() {
            return forum_title;
        }
        public void setForum_title(String forum_title) {
            this.forum_title = forum_title;
        }

        public ArrayList<Character> getListCharactersForForum() {
            return ListCharactersForForum;
        }
        public void setListCharactersForForum(ArrayList<Character> listCharactersForForum) {
            ListCharactersForForum = listCharactersForForum;
        }
    }

    public class Character {
        public int forum_id;
        public int char_id;
        public String forum_title;
        public String char_login;
        public String char_tag;
        public int char_visible;

        public int getForum_id() {
            return forum_id;
        }
        public void setForum_id(int forum_id) {
            this.forum_id = forum_id;
        }

        public int getChar_id() {
            return char_id;
        }
        public void setChar_id(int char_id) {
            this.char_id = char_id;
        }

        public String getForum_title() {
            return forum_title;
        }
        public void setForum_title(String forum_title) {
            this.forum_title = forum_title;
        }

        public String getChar_login() {
            return char_login;
        }
        public void setChar_login(String char_login) {
            this.char_login = char_login;
        }

        public String getChar_tag() {
            return char_tag;
        }
        public void setChar_tag(String char_tag) {
            this.char_tag = char_tag;
        }

        public int getChar_visible() {
            return char_visible;
        }
        public void setChar_visible(int char_visible) {
            this.char_visible = char_visible;
        }
    }
}
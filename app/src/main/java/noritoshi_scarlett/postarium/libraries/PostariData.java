package noritoshi_scarlett.postarium.libraries;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;

public class PostariData {

    private PostariData instance;
    private boolean isLogged;
    private int char_id;
    private String char_login;
    private JsonLibrary.Forum pickedForum;
    private ArrayList<JsonCharacters.Character> characters;

    public PostariData() {
        instance = this;
        isLogged = false;
        char_id = -1;
        char_login = null;
    }

    public PostariData getInstance() {
        if (instance == null) {instance = new PostariData();}
        return instance;
    }

    public boolean isLogged() {return isLogged;}
    public void setLogged(boolean logged) {isLogged = logged;}

    public int getChar_id() {return char_id;}
    public void setChar_id(int char_id) {this.char_id = char_id;}

    public String getChar_login() {return char_login;}
    public void setChar_login(String char_login) {this.char_login = char_login;}

    public JsonLibrary.Forum getPickedForum() {return pickedForum;}
    public void setPickedForum(JsonLibrary.Forum pickedForum) {this.pickedForum = pickedForum;}

    public ArrayList<JsonCharacters.Character> getCharacters() { return characters; }
    public void setCharacters(ArrayList<JsonCharacters.Character> characters) { this.characters = characters; }
}
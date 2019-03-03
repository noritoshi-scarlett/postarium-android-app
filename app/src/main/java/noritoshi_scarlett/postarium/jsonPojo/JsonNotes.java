package noritoshi_scarlett.postarium.jsonPojo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonNotes {

    private Notes notes;

    public JsonNotes() { }

    public void parse(JsonElement jsonNotes) {

        Gson gson = new GsonBuilder().create();
        notes = gson.fromJson(jsonNotes, Notes.class);
    }

    public Notes getNotes() {
        return notes;
    }

    public class Notes {

        @SerializedName("notes")
        private ArrayList<Note> ListNotes;

        public ArrayList<Note> getListNotes() {
            return ListNotes;
        }

        public void setListNotes(ArrayList<Note> listNotes) {
            ListNotes = listNotes;
        }
    }

    public class Note {
    }
}

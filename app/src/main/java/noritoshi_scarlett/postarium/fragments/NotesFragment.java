package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.NotesAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonNotes;

public class NotesFragment extends Fragment {

    NotesAdapter adapter;
    private JsonNotes.Notes notesAll;

    private ListView notesList;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getActivity().getApplicationContext();

        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        notesList = (ListView)v.findViewById(R.id.notesList);

        pickNotes(context);

        return v;
    }

    public void pickNotes(final Context context) {

        Postarium.WebRequests.get("notes/getNotes.html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    JsonParser parser = new JsonParser();
                    String responseOnline = response.body().string();
                    JsonObject jResponse = parser.parse(responseOnline).getAsJsonObject();

                    boolean resultSuccess = jResponse.get("success").getAsBoolean();
                    if (resultSuccess) {
                        JsonElement jsonNotes = jResponse.get("notes").getAsJsonArray();
                        JsonNotes notesParser = new JsonNotes();
                        JsonObject jElement = new JsonObject();
                        jElement.add("notes", jsonNotes);
                        notesParser.parse(jElement);
                        notesAll = notesParser.getNotes();

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                changeNotesList(context, notesAll);
                            }
                        });
                    }
                    else
                    {
                        final String resultDesc = jResponse.get("desc").getAsString();
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toast = Toast.makeText(context, resultDesc, Toast.LENGTH_LONG);
                                View view = toast.getView();
                                view.setBackgroundColor(ContextCompat.getColor(context, R.color.backgroundBlueTransparent));
                                view.setPaddingRelative(15, 10, 10, 15);
                                toast.show();

                            }
                        });
                    }
                }
            }
        });
    }

    private void changeNotesList(Context context, JsonNotes.Notes notes) {
        adapter = new NotesAdapter(context, notes);
        notesList.setAdapter(adapter);
    }

}

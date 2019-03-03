package noritoshi_scarlett.postarium.libraries;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.jsonPojo.JsonCharacters;
import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;

public class CharactersDialog extends AlertDialog.Builder  {

    // VIEW'S VARIABLES
    private AppCompatSpinner spinnerForumLists;
    private TextView spinnerTextDesc;
    private TextInputEditText editCharLogin;
    private TextInputEditText editCharPassword;
    private TextInputEditText editCharTag;
    private SwitchCompat switchVisible;
    private AppCompatCheckBox checkCharDelete;
    private TextView textCharDelete;
    private TextView textAttention;

    // DATA VARIABLES
    private JsonCharacters.Character character;
    private String char_tag;
    private String char_login;
    private int char_visible;
    private int char_id;
    private int forum_id;
    private Boolean char_delete = false;
    private Boolean haveForumsList = true;
    public CharactersCircleInterface charactersCircleInterface;

    public CharactersDialog(Context context, int theme) {
        super(context, theme);
        initialize(context, false);
    }

    public CharactersDialog(Context context, int theme, JsonCharacters.Character character) {
        super(context, theme);
        this.character = character;
        initialize(context, true);
    }

    public void setListener(CharactersCircleInterface callback) {
        try {
            charactersCircleInterface = callback;
        } catch (ClassCastException e){
            throw new ClassCastException(callback.toString() + " must implement CharactersCircleInterface" );
        }
    }

    private void initialize(Context context, final Boolean haveCharacter) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alert_char_settings, null);
        setView(view);

        // VIEW
        spinnerForumLists = (AppCompatSpinner) view.findViewById(R.id.spinnerForumLists);
        spinnerTextDesc = (TextView) view.findViewById(R.id.spinnerTextDesc);
        editCharLogin = (TextInputEditText) view.findViewById(R.id.editCharLogin);
        editCharPassword = (TextInputEditText) view.findViewById(R.id.editCharPassword);
        editCharTag = (TextInputEditText) view.findViewById(R.id.editCharTag);
        switchVisible = (SwitchCompat) view.findViewById(R.id.switchCharVisible);
        checkCharDelete = (AppCompatCheckBox) view.findViewById(R.id.checkCharDelete);
        textCharDelete = (TextView) view.findViewById(R.id.textCharDelete);
        textAttention = (TextView) view.findViewById(R.id.textAttention);
        // PASSWORD TEXTVIEW
        Typeface typeface = editCharPassword.getTypeface();
        editCharPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editCharPassword.setTypeface(typeface);
        editCharPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // WYPEŁNIENIE FORMULARZA
        if (haveCharacter) {
            // CHARACTER'S DATA
            char_tag = character.getChar_tag();
            char_login = character.getChar_login();
            char_visible = character.getChar_visible();
            char_id = character.getChar_id();
            forum_id = character.getForum_id();
            editCharLogin.setText(char_login);
            editCharTag.setText(char_tag);
            switchVisible.setChecked(char_visible == 1);
            textAttention.setVisibility(View.GONE);
            checkCharDelete.setChecked(false);
            checkCharDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    textCharDelete.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                    char_delete = b;
                }
            });
        // DODAWANIE NOWEJ PSOTACI
        } else {
            checkCharDelete.setChecked(false);
            checkCharDelete.setVisibility(View.GONE);
            textCharDelete.setVisibility(View.GONE);
        }

        // LISTA FORÓW
        if (Postarium.getJsonDataStorage().getLibrary() != null) {
            final ArrayList<JsonLibrary.Forum> library = Postarium.getJsonDataStorage().getLibrary().getSubscribe();
            ArrayAdapter<JsonLibrary.Forum> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, library);
            spinnerForumLists.setAdapter(adapter);
            int index = 0;
            for (JsonLibrary.Forum forum : library) {
                if (forum.getForum_id() == forum_id) {
                    spinnerForumLists.setSelection(index);
                    break;
                }
                index++;
            }
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner,
                    new String[] {context.getResources().getString(R.string.library_empty_need_refresh)});
            spinnerForumLists.setAdapter(adapter);
            spinnerForumLists.setEnabled(false);
            spinnerTextDesc.setText(R.string.library_empty_need_refresh_desc);
            spinnerTextDesc.setTextColor(ContextCompat.getColor(context, R.color.backgroundRed));
            spinnerTextDesc.setTextSize(12.0f);
            haveForumsList =  false;
        }

        setNeutralButton(R.string.circle_char_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                dialogInterface.dismiss();
            }
        });
        setPositiveButton(haveCharacter ? R.string.circle_char_dialog_apply : R.string.circle_char_dialog_add , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                // USUŃ INFORMACJE O POSTACI
                //TODO -> komunikat o bledzie
                if (haveCharacter && checkCharDelete.isChecked()) {
                    Postarium.WebRequests.get("characters/delete/" + char_id + "/" + forum_id + "/.html", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {}
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                charactersCircleInterface.refreshListOfCharacters();
                                dialogInterface.dismiss();
                            }
                        }
                    });
                    // DODAJ POSTAĆ LUB EDYTUJ USTAWIENIA POSTACI
                    // TODO -> komunikat o bledzie
                } else if (haveForumsList) {
                    //TODO -> dodaj znacnzik mobilny
                    //TODO -> w Postarium dodaj mobilna obsluge
                    RequestBody formBody = new FormBody.Builder()
                            .add(haveCharacter ? "submit_edit_char" : "submit_sign_char_up", "1")
                            .add("char_password", editCharPassword.getText().toString())
                            .add("char_passwordRetry", editCharPassword.getText().toString())
                            .add("char_tag", editCharTag.getText().toString())
                            .add("char_login", editCharLogin.getText().toString())
                            .add("char_id_forum", String.valueOf(((JsonLibrary.Forum)spinnerForumLists.getSelectedItem()).getForum_id()))
                            .add("char_visible", switchVisible.isChecked() ? "1" : "0")
                            .build();
                    String url;
                    if (haveCharacter) {
                        url = "characters/" + "profile/" + char_id + "/" + forum_id + "/.html";
                    } else {
                        url = "characters/" + "index/.html";
                    }
                    Postarium.WebRequests.post(url, formBody, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {}
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                charactersCircleInterface.refreshListOfCharacters();
                                dialogInterface.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    public interface CharactersCircleInterface {
        void refreshListOfCharacters();
    }
}

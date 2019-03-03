package noritoshi_scarlett.postarium.interfaces;

import java.util.ArrayList;

import noritoshi_scarlett.postarium.jsonPojo.JsonLibrary;

public interface RefreshingListInterface {
    void refreshingList(ArrayList<JsonLibrary.Forum> library);
    void refreshingStop();
}

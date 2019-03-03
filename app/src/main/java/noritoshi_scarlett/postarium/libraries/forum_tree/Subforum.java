package noritoshi_scarlett.postarium.libraries.forum_tree;

import noritoshi_scarlett.postarium.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Subforum implements LayoutItemType {

    public String id;
    public String title;
    public boolean isCat;

    public Subforum (String id, String title, boolean isCat) {
        this.id = id;
        this.title = title;
        this.isCat = isCat;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_forum_tree_subforum;
    }
}

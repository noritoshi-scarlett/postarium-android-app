package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonForumTree {

    private Tree forumTree;

    public JsonForumTree(String jsonForumTree) {
        parse(jsonForumTree);
    }

    public void parse(String jsonForumTree) {

        Gson gson = new GsonBuilder().create();
        forumTree = gson.fromJson(jsonForumTree, Tree.class);
    }

    public Tree getForumTree() {
        return forumTree;
    }

    public class Tree {

        @SerializedName("tree")
        private ArrayList<Subforum> root;

        public ArrayList<Subforum> getRoot() { return root; }
        public void setRoot(ArrayList<Subforum> root) { this.root = root; }
    }

    public class Subforum {

        @SerializedName("is_cat")
        private boolean isCat;
        private String id;
        private int branch;
        private String title;

        public boolean isCat() { return isCat; }
        public void setCat(boolean cat) { isCat = cat; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public int getBranch() { return branch; }
        public void setBranch(int branch) { this.branch = branch; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}

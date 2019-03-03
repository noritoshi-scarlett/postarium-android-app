package noritoshi_scarlett.postarium.jsonPojo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonForumPostsMsgs {

    private ForumPostsMsgs forumPostsMsgs;

    public JsonForumPostsMsgs() {
    }

    public void parse(JsonElement jsonForumPostsMsgs) {

        Gson gson = new GsonBuilder().create();
        forumPostsMsgs = gson.fromJson(jsonForumPostsMsgs, ForumPostsMsgs.class);
    }

    public ForumPostsMsgs getForumPostsMsgs() {
        return forumPostsMsgs;
    }

    public class ForumPostsMsgs {

        @SerializedName("LastPosts")
        private ArrayList<Post> LastPosts;
        @SerializedName("NewPosts")
        private ArrayList<Post> NewPosts;
        @SerializedName("PrivMsgs")
        private PrivMsgs PrivMsgs;
        @SerializedName("whoIs")
        private String currentOnline;

        public ArrayList<Post> getLastPosts() {
            return LastPosts;
        }

        public void setLastPosts(ArrayList<Post> lastPosts) {
            LastPosts = lastPosts;
        }

        public ArrayList<Post> getNewPosts() {
            return NewPosts;
        }

        public void setNewPosts(ArrayList<Post> newPosts) {
            NewPosts = newPosts;
        }

        public PrivMsgs getPrivMsgs() {
            return PrivMsgs;
        }

        public void setPrivMsgs(PrivMsgs privMsgs) {
            PrivMsgs = privMsgs;
        }

        public String getCurrentOnline() {
            return currentOnline;
        }

        public void setCurrentOnline(String currentOnline) {
            this.currentOnline = currentOnline;
        }
    }

    public class PrivMsgs {
        //private String char_login;
        //private int char_id;

        @SerializedName("inbox")
        private ArrayList<Msgs> inboxList;
        @SerializedName("outbox")
        private ArrayList<Msgs> outboxList;

//        public String getChar_login() {
//            return char_login;
//        }
//
//        public void setChar_login(String char_login) {
//            this.char_login = char_login;
//        }
//
//        public int getChar_id() {
//            return char_id;
//        }
//
//        public void setChar_id(int char_id) {
//            this.char_id = char_id;
//        }

        public ArrayList<Msgs> getInboxList() {
            return inboxList;
        }

        public void setInboxList(ArrayList<Msgs> inboxList) {
            this.inboxList = inboxList;
        }

        public ArrayList<Msgs> getOutboxList() {
            return outboxList;
        }

        public void setOutboxList(ArrayList<Msgs> outboxList) {
            this.outboxList = outboxList;
        }
    }

    public class Msgs {
        private String char_login;
        private int char_id;

        @SerializedName("Messages")
        private ArrayList<Msg> MsgsList;

        public String getChar_login() {
            return char_login;
        }

        public void setChar_login(String char_login) {
            this.char_login = char_login;
        }

        public int getChar_id() {
            return char_id;
        }

        public void setChar_id(int char_id) {
            this.char_id = char_id;
        }

        public ArrayList<Msg> getMsgsList() {
            return MsgsList;
        }

        public void setMsgsList(ArrayList<Msg> msgsList) {
            MsgsList = msgsList;
        }
    }

    public class Msg {
        private String msg_topic;
        private String msg_href;
        private String msg_author;
        private String msg_date;

        public String getMsg_topic() {
            return msg_topic;
        }

        public void setMsg_topic(String msg_topic) {
            this.msg_topic = msg_topic;
        }

        public String getMsg_href() {
            return msg_href;
        }

        public void setMsg_href(String msg_href) {
            this.msg_href = msg_href;
        }

        public String getMsg_author() {
            return msg_author;
        }

        public void setMsg_author(String msg_author) {
            this.msg_author = msg_author;
        }

        public String getMsg_date() {
            return msg_date;
        }

        public void setMsg_date(String msg_date) {
            this.msg_date = msg_date;
        }
    }

    public class Post {
        private String post_category;
        private String cat_href;
        private String post_topic;
        private String post_href_t;
        private String post_date;
        private String post_href_p;
        private String post_author;

        public String getPost_category() {
            return post_category;
        }

        public void setPost_category(String post_category) {
            this.post_category = post_category;
        }

        public String getCat_href() {
            return cat_href;
        }

        public void setCat_href(String cat_href) {
            this.cat_href = cat_href;
        }

        public String getPost_topic() {
            return post_topic;
        }

        public void setPost_topic(String post_topic) {
            this.post_topic = post_topic;
        }

        public String getPost_href_t() {
            return post_href_t;
        }

        public void setPost_href_t(String post_href_t) {
            this.post_href_t = post_href_t;
        }

        public String getPost_date() {
            return post_date;
        }

        public void setPost_date(String post_date) {
            this.post_date = post_date;
        }

        public String getPost_href_p() {
            return post_href_p;
        }

        public void setPost_href_p(String post_href_p) {
            this.post_href_p = post_href_p;
        }

        public String getPost_author() {
            return post_author;
        }

        public void setPost_author(String post_author) {
            this.post_author = post_author;
        }
    }
}
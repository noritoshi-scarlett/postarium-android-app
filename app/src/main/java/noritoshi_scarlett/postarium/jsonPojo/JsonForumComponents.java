package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonForumComponents {

    private Online online;
    private NewsArray news;
    private ShortcutsArray shortcuts;


    public Online getOnline() {
        return online;
    }
    public void setOnline(Online online) {
        this.online = online;
    }

    public NewsArray getNews() {
        return news;
    }
    public void setNews(NewsArray news) {
        this.news = news;
    }

    public ShortcutsArray getShortcuts() {
        return shortcuts;
    }
    public void setShortcuts(ShortcutsArray shortcuts) {
        this.shortcuts = shortcuts;
    }

    public JsonForumComponents() {
        online = new Online();
        news = new NewsArray();
        shortcuts = new ShortcutsArray();
    }

    public class Online {

        private String block;

        public String getBlock() {
            return block;
        }
        public void setBlock(String block) {
            this.block = block;
        }
    }

    public class SBMsg {

        private String m;
        private String n;

        public SBMsg(String m, String n) {
            this.m = m;
            this.n = n;
        }

        public String getM() {return m;}
        public void setM(String m) {this.m = m;}

        public String getN() {return n;}
        public void setN(String n) {this.n = n;}
    }

    public class News {

        private String title;
        private String desc;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public class Shortcuts {

        @SerializedName("topic")
        private String title;
        @SerializedName("href")
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class ShortcutsArray {
        private ArrayList<Shortcuts> quick;

        public ArrayList<Shortcuts> getQuick() {
            return quick;
        }
        public void setQuick(ArrayList<Shortcuts> quick) {
            this.quick = quick;
        }
    }

    public class NewsArray {
        private ArrayList<News> news;

        public ArrayList<News> getNews() {
            return news;
        }
        public void setNews(ArrayList<News> news) {
            this.news = news;
        }
    }
}


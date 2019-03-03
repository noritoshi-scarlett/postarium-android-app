package noritoshi_scarlett.postarium.jsonPojo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class JsonNews {

    private News news;

        public JsonNews() { }

        public void parse(JsonElement jsonNews) {

            Gson gson = new GsonBuilder().create();
            news = gson.fromJson(jsonNews, News.class);
        }

        public News getNews() {
            return news;
        }

public class News {

    @SerializedName("news")
    private ArrayList<New> ListNews;

    public ArrayList<New> getListNews() {
        return ListNews;
    }

    public void setListNews(ArrayList<New> listNews) {
        ListNews = listNews;
    }
}

public class New {
    private int news_id;
    private String news_author;
    private int news_id_forum;
    private String news_topic;
    private String news_desc;
    private String news_date;

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getNews_author() {
        return news_author;
    }

    public void setNews_author(String news_author) {
        this.news_author = news_author;
    }

    public int getNews_id_forum() {
        return news_id_forum;
    }

    public void setNews_id_forum(int news_id_forum) {
        this.news_id_forum = news_id_forum;
    }

    public String getNews_topic() {
        return news_topic;
    }

    public void setNews_topic(String news_topic) {
        this.news_topic = news_topic;
    }

    public String getNews_desc() {
        return news_desc;
    }

    public void setNews_desc(String news_desc) {
        this.news_desc = news_desc;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }
}
}

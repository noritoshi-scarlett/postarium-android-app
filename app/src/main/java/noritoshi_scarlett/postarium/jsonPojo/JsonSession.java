package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class JsonSession {

    private Session session;

    public JsonSession() { }

    public void parse(JsonElement jsonSession) {

        Gson gson = new GsonBuilder().create();
        session = gson.fromJson(jsonSession, Session.class);
    }

    public Session getSession() {
        return session;
    }

    public class Session {

        private int userId;
        private String name;
        private String email;
        private int permission;
        private String lastVisit;
        private boolean logged;

        public int getUserId() {
            return userId;
        }
        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public int getPermission() {
            return permission;
        }
        public void setPermission(int permission) {
            this.permission = permission;
        }

        public String getLastVisit() {
            return lastVisit;
        }
        public void setLastVisit(String lastVisit) {
            this.lastVisit = lastVisit;
        }

        public boolean isLogged() {
            return logged;
        }
        public void setLogged(boolean logged) {
            this.logged = logged;
        }
    }

}

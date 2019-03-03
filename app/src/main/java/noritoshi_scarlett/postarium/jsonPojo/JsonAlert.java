package noritoshi_scarlett.postarium.jsonPojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonAlert {

    private Alert alert;

    public JsonAlert(String jsonAlert) {
        parse(jsonAlert);
    }

    public void parse(String jsonAlert) {

        Gson gson = new GsonBuilder().create();
        alert = gson.fromJson(jsonAlert, Alert.class);
    }

    public Alert getAlert() {
        return alert;
    }


    public class Alert {

        private boolean success;
        private String desc;

        public boolean getSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}

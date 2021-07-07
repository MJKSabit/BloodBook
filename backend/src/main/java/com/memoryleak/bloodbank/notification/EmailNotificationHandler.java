package com.memoryleak.bloodbank.notification;

import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailNotificationHandler {

    private final Logger logger = LogManager.getLogger(EmailNotificationHandler.class);

    private final String apiURL;
    private final OkHttpClient client;


    public EmailNotificationHandler(@Value("${EMAIL_API}") String apiURL) {
        this.client = new OkHttpClient();
        this.apiURL = apiURL;
        logger.debug("EmailAPI: "+apiURL);
    }

    public boolean sendEmail(List<String> to, String subject, String body) throws IOException {
        JSONArray toArray = new JSONArray(to);
        JSONObject json = new JSONObject();
        json.put("to", toArray);
        json.put("subject", subject);
        json.put("body", body);

        logger.debug("Sending Email["+subject+"] to "+to.size()+" recipients");

        RequestBody postBody = RequestBody.create(
                MediaType.parse("application/json"), json.toString());
        Request request = new Request.Builder()
                .url(apiURL)
                .post(postBody)
                .build();

        Call call = client.newCall(request);
        Response response = call.execute();

        logger.debug("Sent Email Response Code: "+response.code());

        if (response.code() == 200) {
            String responseBody = response.body().string();
            logger.debug("Sent Email Response Body: "+responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getBoolean("success");
        }

        return false;
    }
}

package com.example.chattranslator.Services;

import com.example.chattranslator.Models.Language;
import com.example.chattranslator.Utils.CONSTS;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LanguageApiService {

    OkHttpClient client = new OkHttpClient();

    public List<Language> fetchData() {
        List<Language> languageList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(CONSTS.BASE_URL)
                .get()
                .addHeader("X-RapidAPI-Key", CONSTS.API_KEY)
                .addHeader("X-RapidAPI-Host", CONSTS.API_HOST)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                JSONArray jsonArray = new JSONArray(responseData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String code = jsonObject.getString("code");
                    Language language = new Language();
                    language.setName(name);
                    language.setCode(code);
                    languageList.add(language);
                }
            } else {
                System.out.println("Error: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

        return languageList;
    }
}

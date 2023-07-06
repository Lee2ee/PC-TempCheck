package com.temp.manager.domain.table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class TableAPI {
    public static void main(String[] args) {
        try {
            getAbnormalData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<JSONObject> getAbnormalData() throws IOException {
        String REST_API_URL = "http://localhost:8080/temps";
        URL url = new URL(REST_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(content.toString());
            List<JSONObject> abnormalDataList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);

                if (!row.getString("state").equals("normal")) {
                    JSONObject newRow = new JSONObject();
                    newRow.put("id", row.get("id"));
                    newRow.put("ip", row.get("ip"));
                    newRow.put("timestamp", row.get("timestamp"));
                    newRow.put("state", row.get("state"));
                    abnormalDataList.add(newRow);
                }
            }
            System.out.println(abnormalDataList);
            return abnormalDataList;
        } else {
            System.out.println("REST API 호출 실패");
            return null;
        }
    }
}
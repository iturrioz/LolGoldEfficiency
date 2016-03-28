package net.iturrioz.lolgoldefficiency.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LolRequest {

    public static JSONObject getItems(final String version) {
        return getFile(version, "item.json");
    }

    public static JSONObject getRunes(final String version) {
        return getFile(version, "rune.json");
    }

    public static JSONObject getMasteries(final String version) {
        return getFile(version, "mastery.json");
    }

    private static JSONObject getFile(final String version, final String fileName) {
        Object result = makeRequest("http://ddragon.leagueoflegends.com/cdn/" + version + "/data/en_US/" + fileName);
        return result != null ? (JSONObject) result : null;
    }

    public static String getVersion(final String key) {
        JSONArray versionsJson = getVersionsJson(key);
        return versionsJson != null ? (String) versionsJson.get(0) : null;
    }

    public static List<String> getVersionList(final String key) {
        JSONArray versionsJson = getVersionsJson(key);
        List<String> list = new ArrayList<>();
        if (versionsJson != null) {
            for (int i = 0; i < versionsJson.size(); i++) {
                String version = (String) versionsJson.get(i);
                if (!version.startsWith("0") && !version.startsWith("3")) {
                    list.add(version);
                }
            }
        }
        return list;
    }

    private static JSONArray getVersionsJson(final String key) {
        return (JSONArray) makeRequest("https://global.api.pvp.net/api/lol/static-data/euw/v1.2/versions?api_key=" + key);
    }

    private static Object makeRequest(final String address) {
        HttpURLConnection connection = null;
        try {
            JSONParser parser = new JSONParser();

            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is = connection.getInputStream();
            Object jsonObject = parser.parse(new InputStreamReader(is));
            is.close();
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) connection.disconnect();
        }
        return null;
    }
}

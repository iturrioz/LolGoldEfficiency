package net.iturrioz.lolgoldefficiency;

import android.test.AndroidTestCase;
import android.util.Log;

import net.iturrioz.lolgoldefficiency.data.LolRequest;
import net.iturrioz.lolgoldefficiency.data.StatValue;
import net.iturrioz.lolgoldefficiency.data.domain.Rune;

import org.json.simple.JSONObject;

import java.util.List;

public class RuneTest extends AndroidTestCase {

    public void testRune() {
        String version = "6.6.1";
        JSONObject jsonObject = LolRequest.getItems(version);

        JSONObject data = (JSONObject) jsonObject.get("data");

        StatValue stats = StatValue.createStats(data);
        for (StatValue.StatType stat : StatValue.StatType.values()) {
            double fieldValue = stats.getFieldValue(stat);
            System.out.println(stat.name() + ": " + Math.rint(fieldValue));
        }
        System.out.println();

        for (List<Rune> list : Rune.readRunes(LolRequest.getRunes(version), stats).values()) {
            for (Rune rune: list) {
                if (rune.hasValue()) {
                    System.out.println(rune.toString());
                } else {
                    System.out.println(rune.getName());
                }
            }
        }


//        JSONObject masteries = LolRequest.getMasteries(version);
//        System.out.println(((JSONObject) masteries.get("data")).get("4123"));
//        for (final Object key : masteries.keySet()) {
//            System.out.println(key);
//        }
    }

    public void testVersions() {
        //String version = "5.21.1";
        for (String version : LolRequest.getVersionList("API_KEY")) {
            try {
                Log.i("VERSION", version);
                Rune.readRunes(LolRequest.getRunes(version), StatValue.createStats((JSONObject) LolRequest.getItems(version).get("data")));
            } catch (Exception e) {
                e.printStackTrace();
                fail(version);
            }
        }
    }
}

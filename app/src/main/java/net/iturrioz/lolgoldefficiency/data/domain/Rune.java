package net.iturrioz.lolgoldefficiency.data.domain;

import net.iturrioz.lolgoldefficiency.data.StatValue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
"5016":{
    "name":"Lesser Mark of Scaling Magic Resist",
    "description":"+0.04 magic resist per level (+0.72 at champion level 18)",
    "image":{
        "full":"r_4_1.png",
        "sprite":"rune0.png",
        "group":"rune",
        "x":240,
        "y":0,
        "w":48,
        "h":48
    },
    "rune":{
        "isrune":true,
        "tier":"1",
        "type":"red"
    },
    "stats":{
        "rFlatSpellBlockModPerLevel":0.0412
    },
    "tags":["defense","perLevel","mark"],
    "colloq":null,
    "plaintext":null
},
 */
public class Rune {
    public enum RuneType {
        Mark,
        Seal,
        Glyph,
        Quintessence
    }

    private final GoldValue gold;
    private final String name;
    private final RuneType runeType;

    Rune(GoldValue gold, String name, RuneType runeType) {
        this.gold = gold;
        this.name = name;
        this.runeType = runeType;
    }

    public GoldValue getGold() {
        return gold;
    }

    public String getName() {
        return name;
    }

    public RuneType getRuneType() {
        return runeType;
    }

    public boolean hasValue(){
        return gold.getValueType() != GoldValue.ValueType.UNKNOWN;
    }

    @Override
    public String toString() {
        return String.format("%-50s%s", name, gold);
    }

    public static Rune toRune(JSONObject jsonObject, StatValue stats) {
        JSONArray tags = (JSONArray) jsonObject.get("tags");
        for (final RuneType runeType: RuneType.values()) {
            if (tags.contains(runeType.name().toLowerCase(Locale.US))) {
                JSONObject stats1 = (JSONObject) jsonObject.get("stats");
                GoldValue goldValue = stats.getValueForStat(stats1);
                return new Rune(goldValue, (String) jsonObject.get("name"), runeType);
            }
        }
        throw new IllegalStateException();
    }

    public static Map<RuneType, List<Rune>> readRunes(JSONObject jsonObject, StatValue stats) {
        List<Rune> marks = new ArrayList<>();
        List<Rune> seals = new ArrayList<>();
        List<Rune> glyphs = new ArrayList<>();
        List<Rune> quints = new ArrayList<>();

        JSONObject data = (JSONObject) jsonObject.get("data");
        for (final Object value : data.values()) {
            JSONObject json = (JSONObject) value;
            if (Integer.parseInt((String)(((JSONObject) json.get("rune")).get("tier"))) == 3) {
                try {
                    Rune rune = Rune.toRune(json, stats);
                    if (rune.getGold().getValue() > 0) {
                        switch (rune.getRuneType()) {
                            case Mark:
                                marks.add(rune);
                                break;
                            case Seal:
                                seals.add(rune);
                                break;
                            case Glyph:
                                glyphs.add(rune);
                                break;
                            case Quintessence:
                                quints.add(rune);
                                break;
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        final Map<RuneType, List<Rune>> result = new HashMap<>();
        result.put(RuneType.Mark, sortList(marks));
        result.put(RuneType.Seal, sortList(seals));
        result.put(RuneType.Glyph, sortList(glyphs));
        result.put(RuneType.Quintessence, sortList(quints));
        return result;
    }

    private static List<Rune> sortList(List<Rune> runes) {
        Collections.sort(runes, new Comparator<Rune>() {
            public int compare(Rune rune1, Rune rune2) {
                return (int) (rune2.getGold().getValue() - rune1.getGold().getValue());
            }
        });
        return runes;
    }
}

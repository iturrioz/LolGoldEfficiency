package net.iturrioz.lolgoldefficiency.data;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.iturrioz.lolgoldefficiency.data.domain.GoldValue;
import net.iturrioz.lolgoldefficiency.data.domain.ItemStat;

import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StatValue {
    public enum StatType {
        FlatPhysicalDamageMod("Long Sword"), FlatMagicDamageMod("Amplifying Tome"), FlatArmorMod("Cloth Armor"),
        FlatSpellBlockMod("Null-Magic Mantle"), FlatHPPoolMod("Ruby Crystal"), FlatMPPoolMod("Sapphire Crystal"), PercentHPRegenMod("Rejuvenation Bead"),
        PercentMPRegenMod("Faerie Charm"), FlatCritChanceMod("Brawler's Gloves"), PercentAttackSpeedMod("Dagger"), FlatMovementSpeedMod("Boots of Speed"),

        PercentLifeStealMod("Vampiric Scepter"), PercentSpellVampMod("Hextech Revolver"), rFlatArmorPenetrationMod("Serrated Dirk"),
        rFlatMagicPenetrationMod("Sorcerer's Shoes"), rPercentCooldownMod("Forbidden Idol"), PercentMovementSpeedMod("Aether Wisp"),
        FlatHPRegenMod("Doran's Shield");

        private final String item;
        StatType(final String item) { this.item = item; }
        public String getItem() { return item; }

    }

    private final Map<StatType, Double> values;

    /**
     * The current gold values for the stats types.
     * @param values Movement speed
     */
    private StatValue(Map<StatType, Double> values) {
        this.values = values;
    }

    public double getFieldValue(final StatType statType) {
        Double aDouble = values.get(statType);
        return aDouble == null ? 0 : aDouble;
    }

    public GoldValue getValueForStat(JSONObject jsonObject) {
        GoldValue result = GoldValue.UNKNOWN;
        for (final Object key: jsonObject.keySet()) {
            double stat = Math.abs(((Number) jsonObject.get(key)).doubleValue());
            if (stat < 1 && (key.toString().contains("Percent") || key.toString().contains("CritChance"))) {
                stat = stat * 100;
            }
            ItemStat itemStat = ItemStat.valueOf((String) key);
            switch (result.getValueType()) {
                case UNKNOWN:
                    result = new GoldValue(stat * getFieldValue(itemStat.statType), itemStat.valueType);
                    break;
                default:
                    if (result.getValueType() == itemStat.valueType) {
                        result = new GoldValue(stat * getFieldValue(itemStat.statType) + result.getValue(), itemStat.valueType);
                    } else {
                        throw new IllegalStateException();
                    }
            }
        }
        return result;
    }

    public static StatValue createStats(final JSONObject jsonObject) {
        final Map<StatType, JSONObject> jsons = new HashMap<StatType, JSONObject>();

        for (final Object key : jsonObject.keySet()) {
            JSONObject item = (JSONObject) jsonObject.get(key);
            final String name = (String) item.get("name");

            final Optional<StatType> statTypeOptional = Iterables.tryFind(Arrays.asList(StatType.values()), new Predicate<StatType>() {
                public boolean apply(StatType statType) {
                    return statType.getItem().equals(name);
                }
            });

            if (statTypeOptional.isPresent()) {
                jsons.put(statTypeOptional.get(), item);
            }
        }

        final Map<StatType, Double> values = new HashMap<StatType, Double>();
        for (StatType statType : StatType.values()) {
            JSONObject item = jsons.get(statType);
            if (item != null) {
                double value = getValuePerPoint(values, item, statType);
                if (value > 0) {
                    values.put(statType, value);
                }
            }

        }

        return new StatValue(values);
    }

    private static double getValuePerPoint(final Map<StatType, Double> values, JSONObject item, StatType statType) {
        String itemStat = statType.name();
        JSONObject gold = (JSONObject) item.get("gold");
        double goldValue = ((Number) gold.get("total")).doubleValue();
        JSONObject stats = (JSONObject) item.get("stats");
        double statsValue;
        if (itemStat.equals(StatType.rPercentCooldownMod.name())) {
            statsValue = parseDescription(item, "% Cooldown Reduction");
        } else if (itemStat.equals(StatType.PercentMovementSpeedMod.name())) {
            statsValue = parseDescription(item, "% Movement Speed");
        } else if (itemStat.equals(StatType.PercentMPRegenMod.name())) {
            statsValue = parseDescription(item, "% Base Mana Regen");
        } else if (itemStat.equals(StatType.PercentHPRegenMod.name())) {
            statsValue = parseDescription(item, "% Base Health Regen");
        } else if (itemStat.equals(StatType.PercentAttackSpeedMod.name())) {
            statsValue = parseDescription(item, "% Attack Speed");
        } else if (itemStat.equals(StatType.FlatCritChanceMod.name())) {
            statsValue = parseDescription(item, "% Critical Strike Chance");
        } else if (itemStat.equals(StatType.PercentLifeStealMod.name())) {
            statsValue = parseDescription(item, "% Life Steal");
        } else if (itemStat.equals(StatType.PercentSpellVampMod.name())) {
            statsValue = parseDescription(item, "% <a href='SpellVamp'>");
        } else if (itemStat.equals(StatType.rFlatArmorPenetrationMod.name())) {
            statsValue = parseDescription(item, " <a href='FlatArmorPen'>");
        } else if (itemStat.equals(StatType.rFlatMagicPenetrationMod.name())) {
            statsValue = parseDescription(item, " <a href='FlatMagicPen'>");
        } else {
            statsValue = ((Number) stats.get(itemStat)).doubleValue();
        }

        if (statsValue > 0) {
            for (Object key : stats.keySet()) {
                if (!key.equals(itemStat)) {
                    try {
                        goldValue = goldValue - values.get(StatType.valueOf((String) key)) * ((Number) stats.get(key)).doubleValue();
                    } catch (Exception e) {
                        // Nothing to do here
                    }
                }
            }
            if (!itemStat.equals(StatType.PercentMPRegenMod.name()) && values.get(StatType.PercentMPRegenMod) != null) {
                goldValue = goldValue - parseDescription(item, "% Base Mana Regen") * values.get(StatType.PercentMPRegenMod);
            }
        }

        return statsValue > 0 ? goldValue / statsValue : 0;
    }

    static double parseDescription(final JSONObject item, final String pattern) {
        try {

            final String[] strings = item.get("description").toString().split(pattern)[0].split("\\+");
            return Double.parseDouble(strings[strings.length - 1]);
        } catch (Exception e) {
            return 0;
        }
    }
}

package net.iturrioz.lolgoldefficiency.data.domain;

import net.iturrioz.lolgoldefficiency.data.StatValue;
import net.iturrioz.lolgoldefficiency.data.StatValue.StatType;
import net.iturrioz.lolgoldefficiency.data.domain.GoldValue.ValueType;

@SuppressWarnings("unused")
public enum ItemStat {
    FlatArmorMod(ValueType.FLAT, StatType.FlatArmorMod),
    FlatAttackSpeedMod(),
    FlatBlockMod(),
    FlatCritChanceMod(ValueType.FLAT, StatType.FlatCritChanceMod),
    FlatCritDamageMod(),
    FlatEXPBonus(),
    FlatEnergyPoolMod(),
    FlatEnergyRegenMod(),
    FlatHPPoolMod(ValueType.FLAT, StatType.FlatHPPoolMod),
    FlatHPRegenMod(ValueType.FLAT, StatType.FlatHPRegenMod),
    FlatMPPoolMod(ValueType.FLAT, StatType.FlatMPPoolMod),
    FlatMPRegenMod(),
    FlatMagicDamageMod(ValueType.FLAT, StatType.FlatMagicDamageMod),
    FlatMovementSpeedMod(ValueType.FLAT, StatType.FlatMovementSpeedMod),
    FlatPhysicalDamageMod(ValueType.FLAT, StatType.FlatPhysicalDamageMod),
    FlatSpellBlockMod(ValueType.FLAT, StatType.FlatSpellBlockMod),
    PercentArmorMod(),
    PercentAttackSpeedMod(ValueType.FLAT, StatType.PercentAttackSpeedMod),
    PercentBlockMod(),
    PercentCritChanceMod(),
    PercentCritDamageMod(),
    PercentDodgeMod(),
    PercentEXPBonus(),
    PercentHPPoolMod(),
    PercentHPRegenMod(ValueType.FLAT, StatType.PercentHPRegenMod),
    PercentLifeStealMod(),
    PercentMPPoolMod(),
    PercentMPRegenMod(ValueType.FLAT, StatType.PercentMPRegenMod),
    PercentMagicDamageMod(),
    PercentMovementSpeedMod(ValueType.FLAT, StatType.PercentMovementSpeedMod),
    PercentPhysicalDamageMod(),
    PercentSpellBlockMod(),
    PercentSpellVampMod(ValueType.FLAT, StatType.PercentSpellVampMod),
    rFlatArmorModPerLevel(ValueType.LEVEL, StatType.FlatArmorMod),
    rFlatArmorPenetrationMod(ValueType.FLAT, StatType.rFlatArmorPenetrationMod),
    rFlatArmorPenetrationModPerLevel(ValueType.LEVEL, StatType.rFlatArmorPenetrationMod),
    rFlatCritChanceModPerLevel(),
    rFlatCritDamageModPerLevel(),
    rFlatDodgeMod(),
    rFlatDodgeModPerLevel(),
    rFlatEnergyModPerLevel(),
    rFlatEnergyRegenModPerLevel(),
    rFlatGoldPer10Mod(),
    rFlatHPModPerLevel(ValueType.LEVEL, StatType.FlatHPPoolMod),
    rFlatHPRegenModPerLevel(ValueType.LEVEL, StatType.FlatHPRegenMod),
    rFlatMPModPerLevel(ValueType.LEVEL, StatType.FlatMPPoolMod),
    rFlatMPRegenModPerLevel(),
    rFlatMagicDamageModPerLevel(ValueType.LEVEL, StatType.FlatMagicDamageMod),
    rFlatMagicPenetrationMod(ValueType.FLAT, StatType.rFlatMagicPenetrationMod),
    rFlatMagicPenetrationModPerLevel(ValueType.LEVEL, StatType.rFlatMagicPenetrationMod),
    rFlatMovementSpeedModPerLevel(ValueType.LEVEL, StatType.FlatMovementSpeedMod),
    rFlatPhysicalDamageModPerLevel(ValueType.LEVEL, StatType.FlatPhysicalDamageMod),
    rFlatSpellBlockModPerLevel(ValueType.LEVEL, StatType.FlatSpellBlockMod),
    rFlatTimeDeadMod(),
    rFlatTimeDeadModPerLevel(),
    rPercentArmorPenetrationMod(),
    rPercentArmorPenetrationModPerLevel(),
    rPercentAttackSpeedModPerLevel(ValueType.LEVEL, StatType.PercentAttackSpeedMod),
    rPercentCooldownMod(ValueType.FLAT, StatType.rPercentCooldownMod),
    rPercentCooldownModPerLevel(ValueType.LEVEL, StatType.rPercentCooldownMod),
    rPercentMagicPenetrationMod(),
    rPercentMagicPenetrationModPerLevel(),
    rPercentMovementSpeedModPerLevel(ValueType.LEVEL, StatType.PercentMovementSpeedMod),
    rPercentTimeDeadMod(),
    rPercentTimeDeadModPerLevel();

    public final GoldValue.ValueType valueType;
    public final StatValue.StatType statType;

    ItemStat(GoldValue.ValueType valueType, StatValue.StatType statType) {
        this.valueType = valueType;
        this.statType = statType;
    }
    
    ItemStat() {
        this.valueType = ValueType.UNKNOWN;
        this.statType = null;
    }
}

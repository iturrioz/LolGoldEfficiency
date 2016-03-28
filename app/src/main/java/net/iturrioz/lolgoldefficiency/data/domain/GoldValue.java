package net.iturrioz.lolgoldefficiency.data.domain;

public class GoldValue {

    static final int LEVEL = 9;

    public enum ValueType {
        FLAT,
        LEVEL,
        UNKNOWN
    }

    final double value;
    final double estimatedValue;

    final ValueType valueType;

    public GoldValue(double value, ValueType valueType) {
        this.value = Math.abs(value);
        this.estimatedValue = valueType == ValueType.LEVEL ? this.value * LEVEL : this.value;
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public double getValue() {
        return estimatedValue;
    }

    @Override
    public String toString() {
        return String.format("%.1f%s", estimatedValue, valueType == ValueType.LEVEL ? String.format(" at level %d (%.2f per level)", LEVEL, value) : "");
    }

    public final static GoldValue UNKNOWN = new GoldValue(0, ValueType.UNKNOWN);
}

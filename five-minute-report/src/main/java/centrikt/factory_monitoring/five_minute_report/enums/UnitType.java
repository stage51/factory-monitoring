package centrikt.factory_monitoring.five_minute_report.enums;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidConstantException;

public enum UnitType {
    PACKED("Фасованная"), UNPACKED("Нефасованная");

    private final String unitType;

    UnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getUnitType() {
        return unitType;
    }

    public static UnitType fromString(String string) {
        for (UnitType unitType : values()) {
            if (unitType.getUnitType().equals(string)) {
                return unitType;
            }
        }
        throw new InvalidConstantException("Invalid unitType: " + string);
    }

    @Override
    public String toString() {
        return unitType;
    }
}

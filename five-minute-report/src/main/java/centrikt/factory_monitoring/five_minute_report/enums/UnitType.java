package centrikt.factory_monitoring.five_minute_report.enums;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidConstraintException;

public enum UnitType {
    PACKED("packed"), UNPACKED("unpacked");

    private String unitType;
    UnitType(String unitType){
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
        throw new InvalidConstraintException("Invalid unitType: " + string);
    }

    @Override
    public String toString() {
        return unitType;
    }
}
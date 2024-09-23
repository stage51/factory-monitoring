package centrikt.factory_monitoring.five_minute_report.enums;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidConstraintException;

public enum Type {
    ALCOHOL_PRODUCT("АП"), ALCOHOL_CONTAINTING_FOOD_PRODUCT("ССП"), ALCOHOL_CONTAINING_NON_FOOD_PRODUCT("ССНП"), ALCOHOL("Спирт");
    private String type;
    Type(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Type fromString(String string) {
        for (Type type : values()) {
            if (type.getType().equals(string)) {
                return type;
            }
        }
        throw new InvalidConstraintException("Invalid type: " + string);
    }

    @Override
    public String toString() {
        return type;
    }
}
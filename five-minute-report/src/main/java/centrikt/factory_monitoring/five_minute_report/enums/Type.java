package centrikt.factory_monitoring.five_minute_report.enums;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidConstantException;

public enum Type {
    ALCOHOL_PRODUCT("Алкогольная продукция"), ALCOHOL_CONTAINING_FOOD_PRODUCT("Спиртосодержащая пищевая продукция"),
    ALCOHOL_CONTAINING_NON_FOOD_PRODUCT("Спиртосодержащая непищевая продукция"), ALCOHOL("Этиловый спирт");

    private final String type;

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
        throw new InvalidConstantException("Invalid type: " + string);
    }

    @Override
    public String toString() {
        return type;
    }
}

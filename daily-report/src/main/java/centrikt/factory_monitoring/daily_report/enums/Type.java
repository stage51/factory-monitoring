package centrikt.factory_monitoring.daily_report.enums;

public enum Type {
    ALCOHOL_PRODUCT(0), ALCOHOL_CONTAINTING_FOOD_PRODUCT(1), ALCOHOL_CONTAINING_NON_FOOD_PRODUCT(2), ALCOHOL(3);
    private int type;
    private Type(int type){
        this.type = type;
    }
    @Override
    public String toString() {
        return String.valueOf(type);
    }
}

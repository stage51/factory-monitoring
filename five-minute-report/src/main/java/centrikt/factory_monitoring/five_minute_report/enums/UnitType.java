package centrikt.factory_monitoring.five_minute_report.enums;

public enum UnitType {
    PACKED(0), UNPACKED(1);

    private int unitType;
    private UnitType(int unitType){
        this.unitType = unitType;
    }
    @Override
    public String toString() {
        return String.valueOf(unitType);
    }
}
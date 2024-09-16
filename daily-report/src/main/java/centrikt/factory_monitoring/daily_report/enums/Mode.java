package centrikt.factory_monitoring.daily_report.enums;

public enum Mode {
    WASHING(1), CALIBRATION(2), TECHNOLOGICAL_RUN(3), PRODUCTION(4), SHUTDOWN(5), ACCEPTANCE_RETURN(6),
    ACCEPTANCE_PURCHASE(7), INTERNAL_MOVEMENT(8), SHIPMENT_TO_BUYER(9), SHIPMENT_RETURN(10);
    private int mode;
    private Mode(int mode){
        this.mode = mode;
    }
    @Override
    public String toString() {
        return String.valueOf(mode);
    }
}

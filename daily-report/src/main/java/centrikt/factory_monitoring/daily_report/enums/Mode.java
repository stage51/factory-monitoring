package centrikt.factory_monitoring.daily_report.enums;

import centrikt.factory_monitoring.daily_report.exceptions.InvalidConstraintException;

public enum Mode {
    WASHING("001"), CALIBRATION("002"), TECHNOLOGICAL_RUN("003"), PRODUCTION("004"), SHUTDOWN("005"),
    ACCEPTANCE_RETURN("006"), ACCEPTANCE_PURCHASE("007"), INTERNAL_MOVEMENT("008"), SHIPMENT_TO_BUYER("009"),
    SHIPMENT_RETURN("010");

    private final String code;

    Mode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Mode fromCode(String code) {
        for (Mode mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        throw new InvalidConstraintException("Invalid mode code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}



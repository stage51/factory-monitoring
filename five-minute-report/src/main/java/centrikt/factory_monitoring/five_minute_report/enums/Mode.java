package centrikt.factory_monitoring.five_minute_report.enums;

import centrikt.factory_monitoring.five_minute_report.exceptions.InvalidConstraintException;

public enum Mode {
    WASHING("Промывка АСИиУ"), CALIBRATION("Калибровка АСИиУ"), TECHNOLOGICAL_RUN("Технологический прогон"),
    PRODUCTION("Производство продукции"), SHUTDOWN("Остановка АСИиУ"),
    ACCEPTANCE_RETURN("Прием (возврат)"), ACCEPTANCE_PURCHASE("Прием (закупка)"),
    INTERNAL_MOVEMENT("Внутреннее перемещение"), SHIPMENT_TO_BUYER("Отгрузка (покупателю)"),
    SHIPMENT_RETURN("Отгрузка (возврат)");

    private final String mode;

    Mode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static Mode fromDescription(String description) {
        for (Mode mode : values()) {
            if (mode.getMode().equals(description)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Invalid mode description: " + description);
    }

    @Override
    public String toString() {
        return mode;
    }
}


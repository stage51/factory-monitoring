package centrikt.factory_monitoring.daily_report.enums;

import centrikt.factory_monitoring.daily_report.exceptions.InvalidConstantException;

public enum Mode {
    FLUSH_RUN("001", "Промывка"),
    CALIBRATION("002", "Калибровка"),
    TECHNICAL_RUN("003", "Тех. прогон"),
    PRODUCTION("004", "Производство"),
    STOPPED("005", "Остановка"),
    RECEPTION_AND_RETURN("006", "Прием (возврат)"),
    RECEPTION("007", "Прием"),
    INTRACOMPANY_TRANSPORTATION("008", "Внутреннее перемещение"),
    SHIPMENT("009", "Отгрузка"),
    SHIPMENT_AND_RETURN("010", "Отгрузка (возврат)"),
    OTHER("011", "Другие цели");

    private final String code;
    private final String description;

    Mode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static Mode fromCode(String code) {
        for (Mode mode : values()) {
            if (mode.getCode().equals(code)) {
                return mode;
            }
        }
        throw new InvalidConstantException("Invalid mode code: " + code);
    }

    public static Mode fromDescription(String description) {
        for (Mode mode : values()) {
            if (mode.getDescription().equals(description)) {
                return mode;
            }
        }
        throw new InvalidConstantException("Invalid mode description: " + description);
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }
}





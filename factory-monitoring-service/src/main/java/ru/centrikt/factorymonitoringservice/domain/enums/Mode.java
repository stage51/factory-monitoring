package ru.centrikt.factorymonitoringservice.domain.enums;

import ru.centrikt.factorymonitoringservice.domain.exceptions.InvalidConstantException;

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

    public static Mode fromCodeOrDescription(String value) {
        for (Mode mode : values()) {
            if (mode.getCode().equals(value) || mode.getDescription().equals(value)) {
                return mode;
            }
        }
        throw new InvalidConstantException("Invalid mode code or description: " + value);
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }
}






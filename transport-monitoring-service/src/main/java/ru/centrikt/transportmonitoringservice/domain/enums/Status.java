package ru.centrikt.transportmonitoringservice.domain.enums;


import ru.centrikt.transportmonitoringservice.domain.exceptions.InvalidConstantException;

public enum Status {
    UNKNOWN("Неизвестно"), ACCEPTED_IN_RAR("Принято в РАР"), NOT_ACCEPTED_IN_RAR("Не принято в РАР"),
    ACCEPTED_IN_UTM("Принято в УТМ"), NOT_ACCEPTED_IN_UTM("Не принято в УТМ");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Status fromDescription(String description) {
        for (Status status : values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new InvalidConstantException("Invalid status description: " + description);
    }

    @Override
    public String toString() {
        return description;
    }
}

package centrikt.factorymonitoring.modereport.enums;


import centrikt.factorymonitoring.modereport.exceptions.InvalidConstantException;

public enum Status {
    UNKNOWN("Неизвестно"), ACCEPTED_IN_RAR("Принято в РАР"), NOT_ACCEPTED_IN_RAR("Не принято в РАР"),
    ACCEPTED_IN_UTM("Принято в УТМ"), NOT_ACCEPTED_IN_UTM("Не принято в УТМ");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status fromDescription(String description) {
        for (Status status : values()) {
            if (status.getStatus().equals(description)) {
                return status;
            }
        }
        throw new InvalidConstantException("Invalid status description: " + description);
    }

    @Override
    public String toString() {
        return status;
    }
}

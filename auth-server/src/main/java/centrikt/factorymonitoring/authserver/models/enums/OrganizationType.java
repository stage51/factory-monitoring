package centrikt.factorymonitoring.authserver.models.enums;
import centrikt.factorymonitoring.authserver.exceptions.InvalidConstantException;

public enum OrganizationType {
    ALCOHOL_RAW_PRODUCTION("Производство алкогольного сырья"), LIQUOR_PRODUCTION("Ликеро-водочный завод"),
    PHARMA_PRODUCTION("Фармацевтическое производство"), BREW_PRODUCTION("Пивоваренная компания"),
    ALCOHOL_RAW_CARRIER("Перевозчик алкогольного сырья"), GAS_RAW_CARRIER("Перевозчик газового сырья"),
    OTHER("Другое");

    private final String description;

    OrganizationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static OrganizationType fromDescription(String description) {
        for (OrganizationType organizationType : values()) {
            if (organizationType.getDescription().equals(description)) {
                return organizationType;
            }
        }
        throw new InvalidConstantException("Invalid organization type description: " + description);
    }

    @Override
    public String toString() {
        return description;
    }
}

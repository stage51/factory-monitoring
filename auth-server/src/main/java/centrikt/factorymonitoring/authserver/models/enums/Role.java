package centrikt.factorymonitoring.authserver.models.enums;

public enum Role {
    ROLE_USER(0), ROLE_ADMIN(1), ROLE_MANAGER(2), ROLE_GUEST(3);

    private final int role;

    Role(int role) {
        this.role = role;
    }

    public int getRole() { return role; }

    public String toString() { return name(); }
}

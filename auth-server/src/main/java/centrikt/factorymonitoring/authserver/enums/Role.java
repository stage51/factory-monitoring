package centrikt.factorymonitoring.authserver.enums;

public enum Role {
    ROLE_USER(0), ROLE_ADMIN(1), ROLE_MANAGER(2);

    private final int role;

    Role(int role) {
        this.role = role;
    }

    public int getRole() { return role; }

    public String toString() { return name(); }
}

package util;


public enum Role {
    ADMIN("ADMIN", 3),
    MANAGER("MANAGER", 2),
    CASHIER("CASHIER", 1);

    private final String roleName;
    private final int level;

    Role(String roleName, int level) {
        this.roleName = roleName;
        this.level = level;
    }

    public String getRoleName() {
        return roleName;
    }

    public int getLevel() {
        return level;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        
        for (Role role : Role.values()) {
            if (role.roleName.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Invalid role: " + roleStr);
    }


    public boolean hasAuthorityOver(Role other) {
        return this.level >= other.level;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
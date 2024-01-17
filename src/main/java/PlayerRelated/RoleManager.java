package PlayerRelated;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {
    private static final Map<String, Role> userRoles = new HashMap<>();

    static {
        // Initialize user roles
        userRoles.put("adminUser", Role.ADMIN);
        userRoles.put("guestUser", Role.GUEST);
    }

    public static Role getRole(String username) {
        return userRoles.getOrDefault(username, Role.GUEST);
    }

    public static boolean canSaveGame(String username) {
        Role role = getRole(username);
        return role == Role.ADMIN;
    }

    public static void setRole(String username, Role role) {
        userRoles.put(username, role);
    }

    static {
        setRole("Red", Role.ADMIN);
        setRole("Blue", Role.GUEST);
    }
}
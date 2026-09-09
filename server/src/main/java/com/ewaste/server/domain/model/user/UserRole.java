package com.ewaste.server.domain.model.user;

import java.util.EnumSet;
import java.util.Set;

/**
 * Wraps a {@link Role} with the fine-grained {@link Permission}s it grants.
 * schema.sql stores only a single {@code role} column on {@code users} -
 * there is no {@code user_roles} table - so this class is not persisted on
 * its own; it is derived at runtime from a {@link User} via {@link #of}
 * and centralises permission checks so controllers/services don't need to
 * switch on {@link Role} themselves.
 */
public class UserRole {

    public enum Permission {
        SUBMIT_EWASTE,
        VIEW_OWN_PICKUPS,
        VIEW_OWN_REWARDS,
        MANAGE_USERS,
        MANAGE_CATEGORIES,
        COLLECT_EWASTE,
        UPDATE_PICKUP_STATUS,
        INSPECT_AND_PROCESS_ITEMS,
        VIEW_REPORTS,
        SEND_NOTIFICATIONS
    }

    private final Role role;

    public UserRole(Role role) {
        this.role = role == null ? Role.CUSTOMER : role;
    }

    public static UserRole of(User user) {
        return new UserRole(user == null ? Role.CUSTOMER : user.getRole());
    }

    public Role getRole() {
        return role;
    }

    public Set<Permission> getPermissions() {
        switch (role) {
            case ADMIN:
                return EnumSet.of(Permission.MANAGE_USERS, Permission.MANAGE_CATEGORIES,
                        Permission.INSPECT_AND_PROCESS_ITEMS, Permission.VIEW_REPORTS,
                        Permission.SEND_NOTIFICATIONS, Permission.UPDATE_PICKUP_STATUS);
            case COLLECTOR:
                return EnumSet.of(Permission.COLLECT_EWASTE, Permission.UPDATE_PICKUP_STATUS);
            case CUSTOMER:
            default:
                return EnumSet.of(Permission.SUBMIT_EWASTE, Permission.VIEW_OWN_PICKUPS,
                        Permission.VIEW_OWN_REWARDS);
        }
    }

    public boolean hasPermission(Permission permission) {
        return getPermissions().contains(permission);
    }

    @Override
    public String toString() {
        return "UserRole{role=" + role + '}';
    }
}

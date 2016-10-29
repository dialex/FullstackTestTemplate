package com.diogonunes.fullstack.helpers;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class User {

    private enum Permission {
        CHANGE_PASSWORD,
        CREATE_OPERATIONS,
        UPDATE_USER_PROFILE,
        USER_MANAGEMENT,
        VIEW_AUDIT,
        VIEW_OPERATIONS,
        VIEW_PROVISION,
        VIEW_SETTLEMENT
    }

    private static final String SUPERUSER_NAME = "superUser";
    private static final String SUPERUSER_PASS = "superPass";
    private static final String TESTUSER_PASS = "testPass";

    private Set<Permission> permissions;
    private String username;
    private String password;

    public User() {
        this("", "", getAnonymousPerms());
    }

    private User(String username, String password, Set<Permission> permissions) {
        this.username = username;
        this.password = password;
        this.permissions = permissions;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Builders

    public static User asAnonymous() {
        return new User("", "", getAnonymousPerms());
    }

    public static User asOperatorSystemAdmin() {
        return new User("testOperatorAdmin", TESTUSER_PASS, getOperatorSystemAdminPerms());
    }

    public static User asOperatorUser() {
        return new User("testOperatorUser", TESTUSER_PASS, getOperatorUserPerms());
    }

    public static User asOperatorAllRoles() {
        return new User(SUPERUSER_NAME, SUPERUSER_PASS, getOperatorAllRolesPerms());
    }

    // Mapping Roles >> Permissions

    private static Set<Permission> getAnonymousPerms() {
        return EnumSet.noneOf(Permission.class);
    }

    private static Set<Permission> getOperatorSystemAdminPerms() {
        return EnumSet.of(
                Permission.VIEW_AUDIT,
                Permission.USER_MANAGEMENT,
                Permission.CHANGE_PASSWORD,
                Permission.UPDATE_USER_PROFILE
        );
    }

    private static Set<Permission> getOperatorUserPerms() {
        return EnumSet.of(
                Permission.VIEW_PROVISION,
                Permission.VIEW_SETTLEMENT,
                Permission.CHANGE_PASSWORD,
                Permission.VIEW_OPERATIONS,
                Permission.UPDATE_USER_PROFILE
        );
    }

    private static Set<Permission> getOperatorAllRolesPerms() {
        Set<Permission> perms = getAnonymousPerms();

        perms.addAll(getOperatorSystemAdminPerms());
        perms.addAll(getOperatorUserPerms());

        return perms;
    }

    // Mapping Screen << Permissions

    public boolean canViewDashboardScreen() {
        Set<Permission> required = EnumSet.of(
                Permission.CREATE_OPERATIONS,
                Permission.VIEW_OPERATIONS
        );
        return hasAtLeastOnePermission(required);
    }

    // Helpers

    public boolean hasPermission(Permission requiredPerm) {
        return permissions.contains(requiredPerm);
    }

    private boolean hasAtLeastOnePermission(Set<Permission> requiredPerms) {
        return !Collections.disjoint(permissions, requiredPerms);
    }
}
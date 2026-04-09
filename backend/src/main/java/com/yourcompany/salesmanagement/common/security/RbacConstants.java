package com.yourcompany.salesmanagement.common.security;

/**
 * Centralized RBAC constants for consistent {@code @PreAuthorize} usage.
 *
 * Note: Roles are represented as authorities with {@code ROLE_} prefix by Spring Security.
 * Permissions are represented as plain authorities, e.g. {@code USER_READ}.
 */
public final class RbacConstants {
    private RbacConstants() {}

    // ---- Role codes (DB role.code / JWT roleCodes) ----
    // Keep existing code compatibility: STORE_MANAGER is currently used as default owner role.
    public static final String ROLE_STORE_MANAGER = "STORE_MANAGER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";

    // Optional future role codes aligned with functional doc (can be added later without breaking):
    public static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";
    public static final String ROLE_STORE_OWNER = "STORE_OWNER";
    public static final String ROLE_BRANCH_MANAGER = "BRANCH_MANAGER";
    public static final String ROLE_CASHIER = "CASHIER";
    public static final String ROLE_WAREHOUSE = "WAREHOUSE";
    public static final String ROLE_ACCOUNTANT = "ACCOUNTANT";
    public static final String ROLE_CSKH_ONLINE = "CSKH_ONLINE";

    // ---- Permission codes (DB permissions.code / plain authorities) ----
    public static final String PERM_USER_READ = "USER_READ";
    public static final String PERM_USER_WRITE = "USER_WRITE";
    public static final String PERM_ROLE_READ = "ROLE_READ";
    public static final String PERM_ROLE_WRITE = "ROLE_WRITE";
    public static final String PERM_PERMISSION_READ = "PERMISSION_READ";

    public static final String PERM_CUSTOMER_READ = "CUSTOMER_READ";
    public static final String PERM_CUSTOMER_WRITE = "CUSTOMER_WRITE";

    public static final String PERM_LOYALTY_READ = "LOYALTY_READ";
    public static final String PERM_LOYALTY_REDEEM = "LOYALTY_REDEEM";

    public static final String PERM_PRODUCT_IMPORT = "PRODUCT_IMPORT";
    public static final String PERM_PRODUCT_EXPORT = "PRODUCT_EXPORT";
    public static final String PERM_IMPORT_JOB_READ = "IMPORT_JOB_READ";

    public static final String PERM_EINVOICE_ISSUE = "EINVOICE_ISSUE";
    public static final String PERM_EINVOICE_READ = "EINVOICE_READ";

    public static final String PERM_INTEGRATION_READ = "INTEGRATION_READ";
    public static final String PERM_INTEGRATION_WRITE = "INTEGRATION_WRITE";
    public static final String PERM_INTEGRATION_SYNC_ORDERS = "INTEGRATION_SYNC_ORDERS";
    public static final String PERM_ONLINE_ORDER_READ = "ONLINE_ORDER_READ";

    public static final String PERM_SHIFT_READ = "SHIFT_READ";
    public static final String PERM_SHIFT_OPEN = "SHIFT_OPEN";
    public static final String PERM_SHIFT_CLOSE = "SHIFT_CLOSE";
}


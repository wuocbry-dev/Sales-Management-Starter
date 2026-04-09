package com.yourcompany.salesmanagement.module.permission.seed;

import com.yourcompany.salesmanagement.module.permission.entity.Permission;
import com.yourcompany.salesmanagement.module.permission.repository.PermissionRepository;
import com.yourcompany.salesmanagement.module.user.entity.Role;
import com.yourcompany.salesmanagement.module.user.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Idempotent RBAC seed, aligned with functional roles (FR-01..FR-49) and Phase 1 roadmap.
 *
 * Enable via:
 * - app.seed.rbac.enabled=true
 *
 * This runner is intentionally OFF by default to avoid unexpected changes in existing DBs.
 */
@Component
@ConditionalOnProperty(prefix = "app.seed.rbac", name = "enabled", havingValue = "true")
public class RbacSeedRunner implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RbacSeedRunner(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 1) Seed permissions
        Map<String, PermDef> perms = permissionMatrix();
        Map<String, Permission> persistedPerms = new LinkedHashMap<>();
        for (PermDef def : perms.values()) {
            Permission p = permissionRepository.findByCode(def.code).orElseGet(() -> {
                Permission np = new Permission();
                np.setCode(def.code);
                np.setName(def.name);
                np.setModuleName(def.moduleName);
                np.setDescription(def.description);
                return permissionRepository.save(np);
            });
            // Keep existing record stable; only fill missing descriptive fields.
            if (isBlank(p.getName())) p.setName(def.name);
            if (isBlank(p.getModuleName())) p.setModuleName(def.moduleName);
            if (isBlank(p.getDescription())) p.setDescription(def.description);
            p = permissionRepository.save(p);
            persistedPerms.put(p.getCode(), p);
        }

        // 2) Seed roles and mapping
        Map<String, RoleDef> roles = roleMatrix();
        for (RoleDef rdef : roles.values()) {
            Role role = roleRepository.findByCode(rdef.code).orElseGet(() -> {
                Role nr = new Role();
                nr.setCode(rdef.code);
                nr.setName(rdef.name);
                nr.setDescription(rdef.description);
                nr.setIsSystem(true);
                nr.setStatus("ACTIVE");
                return roleRepository.save(nr);
            });

            if (isBlank(role.getName())) role.setName(rdef.name);
            if (isBlank(role.getDescription())) role.setDescription(rdef.description);
            if (role.getIsSystem() == null) role.setIsSystem(true);
            if (isBlank(role.getStatus())) role.setStatus("ACTIVE");

            // Merge permissions (do not remove existing ones)
            Set<Permission> toAdd = new LinkedHashSet<>();
            for (String code : rdef.permissionCodes) {
                Permission p = persistedPerms.get(code);
                if (p != null) toAdd.add(p);
            }
            role.getPermissions().addAll(toAdd);
            roleRepository.save(role);
        }
    }

    /**
     * Permission codes are designed per module, stable for FE to build UI.
     * Keep these as the single source-of-truth.
     */
    private Map<String, PermDef> permissionMatrix() {
        Map<String, PermDef> m = new LinkedHashMap<>();

        // Platform: user/role/permission
        put(m, "USER_READ", "User - Read", "user", "View users");
        put(m, "USER_WRITE", "User - Write", "user", "Create/update users");
        put(m, "USER_RESET_PASSWORD", "User - Reset Password", "user", "Reset user passwords");

        put(m, "ROLE_READ", "Role - Read", "role", "View roles");
        put(m, "ROLE_WRITE", "Role - Write", "role", "Create/update roles and mappings");

        put(m, "PERMISSION_READ", "Permission - Read", "permission", "View permissions catalog");

        // Store/Branch/Employee
        put(m, "STORE_READ", "Store - Read", "store", "View store settings");
        put(m, "STORE_WRITE", "Store - Write", "store", "Update store settings");
        put(m, "BRANCH_READ", "Branch - Read", "branch", "View branches");
        put(m, "BRANCH_WRITE", "Branch - Write", "branch", "Create/update branches");
        put(m, "EMPLOYEE_READ", "Employee - Read", "employee", "View employees");
        put(m, "EMPLOYEE_WRITE", "Employee - Write", "employee", "Create/update employees");

        // Catalog
        put(m, "CATEGORY_READ", "Category - Read", "category", "View categories");
        put(m, "CATEGORY_WRITE", "Category - Write", "category", "Create/update categories");
        put(m, "PRODUCT_READ", "Product - Read", "product", "View products");
        put(m, "PRODUCT_WRITE", "Product - Write", "product", "Create/update/disable products");
        put(m, "PRODUCT_IMPORT", "Product - Import", "product", "Import products from file");
        put(m, "PRODUCT_EXPORT", "Product - Export", "product", "Export products to file");

        // Inventory / purchasing
        put(m, "SUPPLIER_READ", "Supplier - Read", "supplier", "View suppliers");
        put(m, "SUPPLIER_WRITE", "Supplier - Write", "supplier", "Create/update suppliers");

        put(m, "INVENTORY_READ", "Inventory - Read", "inventory", "View inventory");
        put(m, "INVENTORY_ADJUST", "Inventory - Adjust", "inventory", "Adjust inventory quantities");

        put(m, "PURCHASE_READ", "Purchase Order - Read", "purchaseorder", "View purchase orders");
        put(m, "PURCHASE_CREATE", "Purchase Order - Create", "purchaseorder", "Create purchase orders");
        put(m, "PURCHASE_RECEIVE", "Purchase Order - Receive", "purchaseorder", "Receive goods and post inventory");
        put(m, "PURCHASE_CANCEL", "Purchase Order - Cancel", "purchaseorder", "Cancel purchase orders");
        put(m, "PURCHASE_RETURN", "Purchase Order - Return", "purchaseorder", "Return goods to supplier");

        // POS
        put(m, "POS_ORDER_READ", "POS Order - Read", "salesorder", "View sales orders");
        put(m, "POS_ORDER_CREATE", "POS Order - Create", "salesorder", "Create sales orders");
        put(m, "POS_ORDER_COMPLETE", "POS Order - Complete", "salesorder", "Complete sales orders");
        put(m, "POS_ORDER_RETURN", "POS Return - Create", "returnorder", "Create return orders");
        put(m, "POS_ORDER_HOLD", "POS Order - Hold", "salesorder", "Hold/reserve orders");
        put(m, "POS_ORDER_DISCOUNT", "POS Order - Discount", "salesorder", "Apply voucher/promotion to orders");

        put(m, "PAYMENT_CREATE", "Payment - Create", "payment", "Create payments for orders");
        put(m, "PAYMENT_READ", "Payment - Read", "payment", "View payments");

        // Customer / loyalty
        put(m, "CUSTOMER_READ", "Customer - Read", "customer", "View customers");
        put(m, "CUSTOMER_WRITE", "Customer - Write", "customer", "Create/update customers");
        put(m, "LOYALTY_READ", "Loyalty - Read", "loyalty", "View loyalty accounts/transactions");
        put(m, "LOYALTY_REDEEM", "Loyalty - Redeem", "loyalty", "Redeem loyalty points");

        // Finance / report
        put(m, "CASHBOOK_READ", "Cashbook - Read", "cashbook", "View cashbook entries");
        put(m, "CASHBOOK_WRITE", "Cashbook - Write", "cashbook", "Create manual cashbook entries");
        put(m, "DEBT_READ", "Debt - Read", "finance", "View customer/supplier debts");
        put(m, "REPORT_READ", "Report - Read", "report", "View reports");
        put(m, "DASHBOARD_READ", "Dashboard - Read", "dashboard", "View dashboard widgets");

        // Import jobs
        put(m, "IMPORT_JOB_READ", "Import Job - Read", "importjob", "View import job status/result");

        // E-invoice
        put(m, "EINVOICE_ISSUE", "E-Invoice - Issue", "einvoice", "Issue e-invoice for sales orders");
        put(m, "EINVOICE_READ", "E-Invoice - Read", "einvoice", "View issued e-invoices");

        // Omnichannel / integrations
        put(m, "INTEGRATION_READ", "Integration - Read", "integration", "View integration channels and mappings");
        put(m, "INTEGRATION_WRITE", "Integration - Write", "integration", "Create/update integration channels and mappings");
        put(m, "INTEGRATION_SYNC_ORDERS", "Integration - Sync Orders", "integration", "Trigger sync orders from channel");
        put(m, "ONLINE_ORDER_READ", "Online Order - Read", "integration", "View online orders");

        // Shipment
        put(m, "SHIPMENT_READ", "Shipment - Read", "shipment", "View shipments");
        put(m, "SHIPMENT_WRITE", "Shipment - Write", "shipment", "Create/update shipments and statuses");

        // Shift (POS cash register)
        put(m, "SHIFT_READ", "Shift - Read", "shift", "View current/previous shifts");
        put(m, "SHIFT_OPEN", "Shift - Open", "shift", "Open a shift at a branch");
        put(m, "SHIFT_CLOSE", "Shift - Close", "shift", "Close a shift at a branch");

        return m;
    }

    /**
     * Role codes aligned with functional doc.
     * Compatibility strategy:
     * - Keep existing STORE_MANAGER role as an alias of STORE_OWNER (same permission set).
     * - Keep SUPER_ADMIN/ADMIN if already used; SUPER_ADMIN gets full permissions.
     */
    private Map<String, RoleDef> roleMatrix() {
        Map<String, PermDef> perms = permissionMatrix();
        Set<String> allPerms = perms.keySet();

        Map<String, RoleDef> m = new LinkedHashMap<>();

        // System Admin: full platform control
        m.put("SYSTEM_ADMIN", new RoleDef(
                "SYSTEM_ADMIN",
                "System Admin",
                "Platform/system administrator",
                new LinkedHashSet<>(List.of(
                        "USER_READ", "USER_WRITE", "USER_RESET_PASSWORD",
                        "ROLE_READ", "ROLE_WRITE",
                        "PERMISSION_READ"
                ))
        ));

        // Store owner: full business ops within their store
        m.put("STORE_OWNER", new RoleDef(
                "STORE_OWNER",
                "Store Owner",
                "Store owner with full access to store data",
                new LinkedHashSet<>(allPerms)
        ));

        // Alias for backward compatibility (existing default role)
        m.put("STORE_MANAGER", new RoleDef(
                "STORE_MANAGER",
                "Store Manager",
                "Compatibility role (alias of STORE_OWNER)",
                new LinkedHashSet<>(allPerms)
        ));

        // Branch manager: most ops except platform user/role mgmt
        m.put("BRANCH_MANAGER", new RoleDef(
                "BRANCH_MANAGER",
                "Branch Manager",
                "Manage operations within a branch",
                new LinkedHashSet<>(List.of(
                        "STORE_READ",
                        "BRANCH_READ",
                        "EMPLOYEE_READ",
                        "CATEGORY_READ", "CATEGORY_WRITE",
                        "PRODUCT_READ", "PRODUCT_WRITE", "PRODUCT_IMPORT", "PRODUCT_EXPORT",
                        "SUPPLIER_READ", "SUPPLIER_WRITE",
                        "INVENTORY_READ", "INVENTORY_ADJUST",
                        "PURCHASE_READ", "PURCHASE_CREATE", "PURCHASE_RECEIVE",
                        "POS_ORDER_READ", "POS_ORDER_CREATE", "POS_ORDER_COMPLETE", "POS_ORDER_RETURN", "POS_ORDER_HOLD",
                        "POS_ORDER_DISCOUNT",
                        "PAYMENT_CREATE", "PAYMENT_READ",
                        "CUSTOMER_READ", "CUSTOMER_WRITE",
                        "LOYALTY_READ", "LOYALTY_REDEEM",
                        "INTEGRATION_READ", "INTEGRATION_WRITE", "INTEGRATION_SYNC_ORDERS", "ONLINE_ORDER_READ",
                        "CASHBOOK_READ", "CASHBOOK_WRITE", "DEBT_READ",
                        "DASHBOARD_READ", "REPORT_READ",
                        "SHIFT_READ", "SHIFT_OPEN", "SHIFT_CLOSE",
                        "IMPORT_JOB_READ"
                        , "EINVOICE_ISSUE", "EINVOICE_READ"
                        , "SHIPMENT_READ", "SHIPMENT_WRITE"
                ))
        ));

        // Cashier: POS + payments + customer basic
        m.put("CASHIER", new RoleDef(
                "CASHIER",
                "Cashier",
                "POS cashier / sales staff",
                new LinkedHashSet<>(List.of(
                        "CATEGORY_READ",
                        "PRODUCT_READ",
                        "INVENTORY_READ",
                        "POS_ORDER_READ", "POS_ORDER_CREATE", "POS_ORDER_COMPLETE", "POS_ORDER_RETURN",
                        "POS_ORDER_DISCOUNT",
                        "PAYMENT_CREATE", "PAYMENT_READ",
                        "CUSTOMER_READ", "CUSTOMER_WRITE",
                        "LOYALTY_READ", "LOYALTY_REDEEM",
                        "DASHBOARD_READ",
                        "SHIFT_READ", "SHIFT_OPEN", "SHIFT_CLOSE",
                        "SHIPMENT_READ", "SHIPMENT_WRITE"
                ))
        ));

        // Warehouse: purchase/inventory/supplier
        m.put("WAREHOUSE", new RoleDef(
                "WAREHOUSE",
                "Warehouse Staff",
                "Warehouse operations (purchasing & inventory)",
                new LinkedHashSet<>(List.of(
                        "SUPPLIER_READ", "SUPPLIER_WRITE",
                        "PRODUCT_READ", "PRODUCT_IMPORT", "PRODUCT_EXPORT",
                        "INVENTORY_READ", "INVENTORY_ADJUST",
                        "PURCHASE_READ", "PURCHASE_CREATE", "PURCHASE_RECEIVE",
                        "SHIFT_READ",
                        "ONLINE_ORDER_READ",
                        "REPORT_READ", "DASHBOARD_READ",
                        "SHIPMENT_READ"
                ))
        ));

        // Accountant: cashbook + reports + payments read
        m.put("ACCOUNTANT", new RoleDef(
                "ACCOUNTANT",
                "Accountant / Cashier (Finance)",
                "Finance operations",
                new LinkedHashSet<>(List.of(
                        "PAYMENT_READ",
                        "CASHBOOK_READ", "CASHBOOK_WRITE", "DEBT_READ",
                        "SHIFT_READ",
                        "REPORT_READ", "DASHBOARD_READ",
                        "IMPORT_JOB_READ",
                        "PRODUCT_EXPORT",
                        "EINVOICE_ISSUE", "EINVOICE_READ",
                        "INTEGRATION_READ", "ONLINE_ORDER_READ"
                ))
        ));

        // CSKH/Online sales: customer & loyalty & reporting
        m.put("CSKH_ONLINE", new RoleDef(
                "CSKH_ONLINE",
                "CSKH / Online Sales",
                "Customer care / online sales",
                new LinkedHashSet<>(List.of(
                        "CUSTOMER_READ", "CUSTOMER_WRITE",
                        "LOYALTY_READ", "LOYALTY_REDEEM",
                        "POS_ORDER_READ",
                        "POS_ORDER_DISCOUNT",
                        "REPORT_READ", "DASHBOARD_READ",
                        "SHIPMENT_READ"
                ))
        ));

        // Legacy/optional roles used in code (keep with broad access if they exist)
        m.put("ADMIN", new RoleDef(
                "ADMIN",
                "Admin",
                "Compatibility admin role",
                new LinkedHashSet<>(allPerms)
        ));
        m.put("SUPER_ADMIN", new RoleDef(
                "SUPER_ADMIN",
                "Super Admin",
                "Compatibility super admin role",
                new LinkedHashSet<>(allPerms)
        ));

        return m;
    }

    private void put(Map<String, PermDef> m, String code, String name, String moduleName, String description) {
        m.put(code, new PermDef(code, name, moduleName, description));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private record PermDef(String code, String name, String moduleName, String description) {}

    private record RoleDef(String code, String name, String description, Set<String> permissionCodes) {}
}


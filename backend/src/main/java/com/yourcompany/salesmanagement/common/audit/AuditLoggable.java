package com.yourcompany.salesmanagement.common.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark service methods to be audited. The audit log is written only when the method succeeds.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditLoggable {
    String module();
    String action();
    String entityType() default "";
}


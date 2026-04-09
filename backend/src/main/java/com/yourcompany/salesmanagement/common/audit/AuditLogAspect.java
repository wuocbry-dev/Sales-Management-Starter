package com.yourcompany.salesmanagement.common.audit;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.auditlog.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class AuditLogAspect {
    private final AuditLogService auditLogService;

    public AuditLogAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Around("@annotation(a)")
    public Object around(ProceedingJoinPoint pjp, AuditLoggable a) throws Throwable {
        Object result = pjp.proceed();

        var principal = SecurityUtils.requirePrincipal();
        Long storeId = principal.storeId();

        Long branchId = extractBranchId(pjp.getArgs(), principal.branchId());
        Long actorUserId = principal.userId();
        String actorUsername = principal.username();

        Long entityId = extractEntityId(result);
        String entityType = a.entityType();
        String message = buildMessage(pjp.getTarget().getClass().getSimpleName(), pjp.getSignature().getName(), pjp.getArgs());

        HttpServletRequest req = currentRequest();
        String ip = req == null ? null : req.getRemoteAddr();
        String ua = req == null ? null : req.getHeader("User-Agent");

        try {
            auditLogService.write(
                    storeId,
                    branchId,
                    actorUserId,
                    actorUsername,
                    a.module(),
                    a.action(),
                    entityType,
                    entityId,
                    message,
                    ip,
                    ua
            );
        } catch (Exception ignored) {
            // Audit must never break business flow
        }

        return result;
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }

    private Long extractEntityId(Object result) {
        if (result == null) return null;
        // Try record accessor id()
        try {
            Method m = result.getClass().getMethod("id");
            Object v = m.invoke(result);
            if (v instanceof Long l) return l;
        } catch (Exception ignored) {}
        return null;
    }

    private Long extractBranchId(Object[] args, Long fallback) {
        if (args == null) return fallback;
        for (Object arg : args) {
            if (arg == null) continue;
            // Try record accessor branchId()
            try {
                Method m = arg.getClass().getMethod("branchId");
                Object v = m.invoke(arg);
                if (v instanceof Long l) return l;
            } catch (Exception ignored) {}
        }
        return fallback;
    }

    private String buildMessage(String clazz, String method, Object[] args) {
        String argTypes = args == null ? "" : Arrays.stream(args)
                .map(a -> a == null ? "null" : a.getClass().getSimpleName())
                .reduce((x, y) -> x + "," + y)
                .orElse("");
        return clazz + "#" + method + "(" + argTypes + ")";
    }
}


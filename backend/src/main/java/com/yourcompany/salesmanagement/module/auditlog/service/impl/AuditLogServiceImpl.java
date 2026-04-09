package com.yourcompany.salesmanagement.module.auditlog.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.auditlog.dto.response.AuditLogResponse;
import com.yourcompany.salesmanagement.module.auditlog.entity.AuditLog;
import com.yourcompany.salesmanagement.module.auditlog.repository.AuditLogRepository;
import com.yourcompany.salesmanagement.module.auditlog.service.AuditLogService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final NamedParameterJdbcTemplate jdbc;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, NamedParameterJdbcTemplate jdbc) {
        this.auditLogRepository = auditLogRepository;
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void write(Long storeId,
                      Long branchId,
                      Long actorUserId,
                      String actorUsername,
                      String module,
                      String action,
                      String entityType,
                      Long entityId,
                      String message,
                      String ip,
                      String userAgent) {
        AuditLog l = new AuditLog();
        l.setStoreId(storeId);
        l.setBranchId(branchId);
        l.setActorUserId(actorUserId);
        l.setActorUsername(actorUsername);
        l.setModule(module);
        l.setAction(action);
        l.setEntityType(entityType);
        l.setEntityId(entityId);
        l.setMessage(message);
        l.setIp(ip);
        l.setUserAgent(userAgent);
        auditLogRepository.save(l);
    }

    @Override
    public List<AuditLogResponse> search(LocalDateTime from,
                                        LocalDateTime to,
                                        Long actorUserId,
                                        String module,
                                        String action,
                                        Long branchId,
                                        Integer limit,
                                        Integer offset) {
        Long storeId = SecurityUtils.requireStoreId();
        int lim = limit == null ? 50 : Math.min(Math.max(limit, 1), 200);
        int off = offset == null ? 0 : Math.max(offset, 0);

        String sql = """
                select
                  id,
                  store_id,
                  branch_id,
                  actor_user_id,
                  actor_username,
                  module,
                  action,
                  entity_type,
                  entity_id,
                  message,
                  ip,
                  user_agent,
                  created_at
                from audit_logs
                where store_id = :storeId
                  and (:fromTs is null or created_at >= :fromTs)
                  and (:toTs is null or created_at <= :toTs)
                  and (:actorUserId is null or actor_user_id = :actorUserId)
                  and (:module is null or module = :module)
                  and (:action is null or action = :action)
                  and (:branchId is null or branch_id = :branchId)
                order by id desc
                limit :lim offset :off
                """;

        var params = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("fromTs", from)
                .addValue("toTs", to)
                .addValue("actorUserId", actorUserId)
                .addValue("module", module)
                .addValue("action", action)
                .addValue("branchId", branchId)
                .addValue("lim", lim)
                .addValue("off", off);

        return jdbc.query(sql, params, (rs, rowNum) -> new AuditLogResponse(
                rs.getLong("id"),
                rs.getLong("store_id"),
                (Long) rs.getObject("branch_id"),
                (Long) rs.getObject("actor_user_id"),
                rs.getString("actor_username"),
                rs.getString("module"),
                rs.getString("action"),
                rs.getString("entity_type"),
                (Long) rs.getObject("entity_id"),
                rs.getString("message"),
                rs.getString("ip"),
                rs.getString("user_agent"),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }
}


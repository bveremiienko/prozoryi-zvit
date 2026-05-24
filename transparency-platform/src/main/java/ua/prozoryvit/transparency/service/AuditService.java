package ua.prozoryvit.transparency.service;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.prozoryvit.transparency.domain.AuditEvent;
import ua.prozoryvit.transparency.repository.AuditEventRepository;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void log(String entityType, Long entityId, String action, String field, String oldValue, String newValue) {
        AuditEvent event = new AuditEvent();
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setFieldName(field);
        event.setOldValue(truncate(oldValue));
        event.setNewValue(truncate(newValue));
        event.setActor(currentActor());
        auditEventRepository.save(event);
    }

    @Transactional
    public void logCreate(String entityType, Long entityId) {
        log(entityType, entityId, "CREATE", null, null, null);
    }

    @Transactional(readOnly = true)
    public List<AuditEvent> findForEntity(String entityType, Long entityId) {
        return auditEventRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    private String currentActor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "system";
        }
        return auth.getName();
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() > 2000 ? value.substring(0, 2000) + "…" : value;
    }
}

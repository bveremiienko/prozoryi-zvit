package ua.prozoryvit.transparency.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.prozoryvit.transparency.domain.AuditEvent;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
}

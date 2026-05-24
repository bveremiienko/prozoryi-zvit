package ua.prozoryvit.transparency.web.dto;

import java.util.List;
import ua.prozoryvit.transparency.domain.AuditEvent;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignReport;
import ua.prozoryvit.transparency.domain.ExpensePlanLine;
import ua.prozoryvit.transparency.domain.TrustStatus;

public record CampaignDetailView(
        Campaign campaign,
        CampaignSummary summary,
        TrustStatus trustStatus,
        List<ExpensePlanLine> planLines,
        List<CampaignReport> reports,
        List<AuditEvent> auditEvents,
        List<CategoryBudgetRow> planVsFact,
        TransparencyChecklist checklist,
        String publicUrl
) {
}

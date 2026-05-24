package ua.prozoryvit.transparency.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignReport;
import ua.prozoryvit.transparency.domain.ExpenseCategory;
import ua.prozoryvit.transparency.domain.ExpensePlanLine;
import ua.prozoryvit.transparency.domain.TrustStatus;
import ua.prozoryvit.transparency.web.dto.CategoryBudgetRow;
import ua.prozoryvit.transparency.web.dto.TransparencyChecklist;

@Service
public class CampaignAnalyticsService {

    public List<CategoryBudgetRow> planVsFact(List<ExpensePlanLine> planLines, List<CampaignReport> reports) {
        Map<ExpenseCategory, BigDecimal> planned = new EnumMap<>(ExpenseCategory.class);
        Map<ExpenseCategory, BigDecimal> spent = new EnumMap<>(ExpenseCategory.class);

        for (ExpensePlanLine line : planLines) {
            planned.merge(line.getCategory(), line.getPlannedAmount(), BigDecimal::add);
        }
        for (CampaignReport report : reports) {
            report.getLines().forEach(line ->
                    spent.merge(line.getCategory(), line.getAmount(), BigDecimal::add));
        }

        List<CategoryBudgetRow> rows = new ArrayList<>();
        for (ExpenseCategory category : ExpenseCategory.values()) {
            BigDecimal p = planned.getOrDefault(category, BigDecimal.ZERO);
            BigDecimal s = spent.getOrDefault(category, BigDecimal.ZERO);
            if (p.signum() > 0 || s.signum() > 0) {
                rows.add(new CategoryBudgetRow(category, p, s));
            }
        }
        rows.sort((a, b) -> b.planned().compareTo(a.planned()));
        return rows;
    }

    public TransparencyChecklist transparencyChecklist(
            Campaign campaign,
            List<ExpensePlanLine> planLines,
            List<CampaignReport> reports,
            TrustStatus trustStatus) {
        boolean hasPlan = !planLines.isEmpty();
        boolean hasReport = !reports.isEmpty();
        boolean onSchedule = trustStatus != TrustStatus.OVERDUE;
        boolean hasAttachments = reports.stream().anyMatch(r -> !r.getAttachments().isEmpty());

        int score = 0;
        if (hasPlan) {
            score++;
        }
        if (hasReport) {
            score++;
        }
        if (onSchedule) {
            score++;
        }
        if (hasAttachments) {
            score++;
        }
        if (campaign.getExternalDonationUrl() != null && !campaign.getExternalDonationUrl().isBlank()) {
            score++;
        }

        int maxScore = 5;
        return new TransparencyChecklist(hasPlan, hasReport, onSchedule, hasAttachments, score, maxScore);
    }
}

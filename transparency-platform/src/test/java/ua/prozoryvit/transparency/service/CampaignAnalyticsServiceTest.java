package ua.prozoryvit.transparency.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignReport;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.ExpenseCategory;
import ua.prozoryvit.transparency.domain.ExpensePlanLine;
import ua.prozoryvit.transparency.domain.ReportLine;
import ua.prozoryvit.transparency.domain.TrustStatus;

class CampaignAnalyticsServiceTest {

    private final CampaignAnalyticsService service = new CampaignAnalyticsService();

    @Test
    void planVsFactAggregatesByCategory() {
        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setStatus(CampaignStatus.ACTIVE);

        ExpensePlanLine plan = new ExpensePlanLine();
        plan.setCategory(ExpenseCategory.PROCUREMENT);
        plan.setPlannedAmount(new BigDecimal("100000"));

        CampaignReport report = new CampaignReport();
        report.setCampaign(campaign);
        ReportLine spent = new ReportLine();
        spent.setCategory(ExpenseCategory.PROCUREMENT);
        spent.setAmount(new BigDecimal("80000"));
        report.setLines(List.of(spent));

        var rows = service.planVsFact(List.of(plan), List.of(report));
        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().usagePercent()).isEqualTo(80);
    }

    @Test
    void checklistScoresTransparencyItems() {
        Campaign campaign = new Campaign();
        campaign.setExternalDonationUrl("https://example.com/jar");

        var checklist = service.transparencyChecklist(
                campaign,
                List.of(new ExpensePlanLine()),
                List.of(new CampaignReport()),
                TrustStatus.ON_TRACK);

        assertThat(checklist.score()).isEqualTo(4);
        assertThat(checklist.hasPlan()).isTrue();
    }
}

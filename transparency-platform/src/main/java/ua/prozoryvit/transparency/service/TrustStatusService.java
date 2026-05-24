package ua.prozoryvit.transparency.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import ua.prozoryvit.transparency.config.AppProperties;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignReport;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.TrustStatus;
import ua.prozoryvit.transparency.repository.CampaignReportRepository;
import ua.prozoryvit.transparency.repository.ExpensePlanLineRepository;

@Service
public class TrustStatusService {

    private final ExpensePlanLineRepository planLineRepository;
    private final CampaignReportRepository reportRepository;

    public TrustStatusService(
            ExpensePlanLineRepository planLineRepository,
            CampaignReportRepository reportRepository,
            AppProperties appProperties) {
        this.planLineRepository = planLineRepository;
        this.reportRepository = reportRepository;
    }

    public TrustStatus compute(Campaign campaign) {
        if (campaign.getStatus() == CampaignStatus.DRAFT) {
            return TrustStatus.REGISTERED;
        }

        long planCount = planLineRepository.countByCampaignId(campaign.getId());
        long reportCount = reportRepository.countByCampaignId(campaign.getId());

        if (planCount == 0) {
            return TrustStatus.REGISTERED;
        }

        if (reportCount == 0) {
            return isOverdue(campaign) ? TrustStatus.OVERDUE : TrustStatus.HAS_PLAN;
        }

        if (isOverdue(campaign)) {
            return TrustStatus.OVERDUE;
        }

        if (campaign.getStatus() == CampaignStatus.CLOSED) {
            return TrustStatus.REPORTED;
        }

        return TrustStatus.ON_TRACK;
    }

    private boolean isOverdue(Campaign campaign) {
        LocalDate due = campaign.getNextReportDue();
        if (due == null || !due.isBefore(LocalDate.now())) {
            return false;
        }
        List<CampaignReport> reports = reportRepository.findByCampaignIdOrderBySubmittedAtDesc(campaign.getId());
        if (reports.isEmpty()) {
            return true;
        }
        return reports.getFirst().getPeriodTo().isBefore(due);
    }
}

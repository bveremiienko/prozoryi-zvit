package ua.prozoryvit.transparency.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.TrustStatus;

public record CampaignSummary(
        Long id,
        String slug,
        String title,
        String organizerName,
        String organizerSlug,
        CampaignStatus status,
        TrustStatus trustStatus,
        BigDecimal targetAmount,
        BigDecimal collected,
        BigDecimal spent,
        BigDecimal plannedTotal,
        int reportCount,
        LocalDate lastReportDate
) {
    public static CampaignSummary of(
            Campaign campaign,
            TrustStatus trustStatus,
            BigDecimal spent,
            BigDecimal plannedTotal,
            int reportCount,
            LocalDate lastReportDate) {
        return new CampaignSummary(
                campaign.getId(),
                campaign.getSlug(),
                campaign.getTitle(),
                campaign.getOrganizerName(),
                campaign.getOrganizerSlug(),
                campaign.getStatus(),
                trustStatus,
                campaign.getTargetAmount(),
                campaign.getDeclaredCollectedAmount(),
                spent,
                plannedTotal,
                reportCount,
                lastReportDate);
    }

    public int progressPercent() {
        if (targetAmount == null || targetAmount.signum() == 0) {
            return 0;
        }
        return collected.multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 0, java.math.RoundingMode.HALF_UP)
                .intValue();
    }
}

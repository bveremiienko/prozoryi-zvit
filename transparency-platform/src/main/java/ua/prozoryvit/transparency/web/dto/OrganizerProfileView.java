package ua.prozoryvit.transparency.web.dto;

import java.util.List;
import ua.prozoryvit.transparency.domain.FundraiserOrganizer;

public record OrganizerProfileView(
        FundraiserOrganizer organizer,
        List<CampaignSummary> campaigns,
        long totalCampaigns,
        long activeCampaigns,
        long completedCampaigns,
        int transparencyScorePercent,
        String publicProfileUrl
) {
}

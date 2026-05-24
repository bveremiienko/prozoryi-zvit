package ua.prozoryvit.transparency.web.dto;

import ua.prozoryvit.transparency.domain.FundraiserOrganizer;

public record OrganizerListItem(
        Long id,
        String slug,
        String name,
        String edrpou,
        boolean verified,
        long totalCampaigns,
        long activeCampaigns,
        int transparencyScorePercent
) {
    public static OrganizerListItem of(
            FundraiserOrganizer organizer,
            long totalCampaigns,
            long activeCampaigns,
            int transparencyScorePercent) {
        return new OrganizerListItem(
                organizer.getId(),
                organizer.getSlug(),
                organizer.getName(),
                organizer.getEdrpou(),
                organizer.isVerified(),
                totalCampaigns,
                activeCampaigns,
                transparencyScorePercent);
    }
}

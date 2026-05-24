package ua.prozoryvit.transparency.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.FundraiserOrganizer;
import ua.prozoryvit.transparency.repository.CampaignRepository;
import ua.prozoryvit.transparency.repository.FundraiserOrganizerRepository;
import ua.prozoryvit.transparency.web.dto.CampaignSummary;
import ua.prozoryvit.transparency.web.dto.OrganizerListItem;
import ua.prozoryvit.transparency.web.dto.OrganizerProfileView;

@Service
public class FundraiserOrganizerService {

    private final FundraiserOrganizerRepository organizerRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;
    private final PublicUrlService publicUrlService;

    public FundraiserOrganizerService(
            FundraiserOrganizerRepository organizerRepository,
            CampaignRepository campaignRepository,
            CampaignService campaignService,
            PublicUrlService publicUrlService) {
        this.organizerRepository = organizerRepository;
        this.campaignRepository = campaignRepository;
        this.campaignService = campaignService;
        this.publicUrlService = publicUrlService;
    }

    @Transactional(readOnly = true)
    public List<OrganizerListItem> listPublic() {
        return organizerRepository.findAllByOrderByNameAsc().stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizerProfileView getPublicProfile(String slug) {
        FundraiserOrganizer organizer = organizerRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Організатора не знайдено"));

        List<CampaignSummary> campaigns = campaignRepository.findByOrganizerIdWithOrganizer(organizer.getId()).stream()
                .filter(c -> c.getStatus() != CampaignStatus.DRAFT)
                .map(campaignService::toSummary)
                .toList();

        long active = campaignRepository.countByFundraiserOrganizerIdAndStatus(
                organizer.getId(), CampaignStatus.ACTIVE);
        long total = campaignRepository.countByFundraiserOrganizerId(organizer.getId());
        long completed = campaignRepository.countByFundraiserOrganizerIdAndStatus(
                organizer.getId(), CampaignStatus.CLOSED);

        int scorePercent = campaigns.isEmpty()
                ? 0
                : (int) campaigns.stream()
                        .mapToInt(c -> transparencyPoints(c.trustStatus()))
                        .average()
                        .orElse(0) * 100 / 5;

        return new OrganizerProfileView(
                organizer,
                campaigns,
                total,
                active,
                completed,
                scorePercent,
                publicUrlService.organizerUrl(organizer.getSlug()));
    }

    @Transactional(readOnly = true)
    public FundraiserOrganizer findById(Long id) {
        return organizerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Організатора не знайдено"));
    }

    private OrganizerListItem toListItem(FundraiserOrganizer organizer) {
        long total = campaignRepository.countByFundraiserOrganizerId(organizer.getId());
        long active = campaignRepository.countByFundraiserOrganizerIdAndStatus(
                organizer.getId(), CampaignStatus.ACTIVE);

        List<CampaignSummary> summaries = campaignRepository.findByOrganizerIdWithOrganizer(organizer.getId()).stream()
                .filter(c -> c.getStatus() != CampaignStatus.DRAFT)
                .map(campaignService::toSummary)
                .toList();

        int scorePercent = summaries.isEmpty()
                ? 0
                : (int) summaries.stream()
                        .mapToInt(c -> transparencyPoints(c.trustStatus()))
                        .average()
                        .orElse(0) * 100 / 5;

        return OrganizerListItem.of(organizer, total, active, scorePercent);
    }

    private int transparencyPoints(ua.prozoryvit.transparency.domain.TrustStatus status) {
        return switch (status) {
            case ON_TRACK, REPORTED -> 5;
            case HAS_PLAN -> 3;
            case REGISTERED -> 2;
            case OVERDUE -> 1;
        };
    }
}

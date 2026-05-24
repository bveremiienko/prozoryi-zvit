package ua.prozoryvit.transparency.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.FundraiserOrganizer;
import ua.prozoryvit.transparency.repository.CampaignRepository;
import ua.prozoryvit.transparency.repository.CampaignReportRepository;
import ua.prozoryvit.transparency.repository.ExpensePlanLineRepository;
import ua.prozoryvit.transparency.repository.ReportLineRepository;
import ua.prozoryvit.transparency.util.SlugUtils;
import ua.prozoryvit.transparency.web.dto.CampaignDetailView;
import ua.prozoryvit.transparency.web.dto.CampaignSummary;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ExpensePlanLineRepository planLineRepository;
    private final CampaignReportRepository reportRepository;
    private final ReportLineRepository reportLineRepository;
    private final TrustStatusService trustStatusService;
    private final AuditService auditService;
    private final CampaignAnalyticsService analyticsService;
    private final PublicUrlService publicUrlService;
    private final OrganizerUserService organizerUserService;

    public CampaignService(
            CampaignRepository campaignRepository,
            ExpensePlanLineRepository planLineRepository,
            CampaignReportRepository reportRepository,
            ReportLineRepository reportLineRepository,
            TrustStatusService trustStatusService,
            AuditService auditService,
            CampaignAnalyticsService analyticsService,
            PublicUrlService publicUrlService,
            OrganizerUserService organizerUserService) {
        this.campaignRepository = campaignRepository;
        this.planLineRepository = planLineRepository;
        this.reportRepository = reportRepository;
        this.reportLineRepository = reportLineRepository;
        this.trustStatusService = trustStatusService;
        this.auditService = auditService;
        this.analyticsService = analyticsService;
        this.publicUrlService = publicUrlService;
        this.organizerUserService = organizerUserService;
    }

    @Transactional(readOnly = true)
    public List<CampaignSummary> listPublicSummaries() {
        return campaignRepository.findAllWithOrganizerOrderByUpdatedAtDesc().stream()
                .filter(c -> c.getStatus() != CampaignStatus.DRAFT)
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CampaignSummary toSummary(Campaign campaign) {
        return buildSummary(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignDetailView getPublicDetail(String slug) {
        Campaign campaign = campaignRepository.findBySlugWithOrganizer(slug)
                .orElseThrow(() -> new NotFoundException("Кампанію не знайдено"));
        if (campaign.getStatus() == CampaignStatus.DRAFT) {
            throw new NotFoundException("Кампанію не знайдено");
        }
        return buildDetail(campaign);
    }

    @Transactional(readOnly = true)
    public Campaign findById(Long id) {
        return campaignRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new NotFoundException("Кампанію не знайдено"));
    }

    @Transactional
    public Campaign create(CampaignForm form) {
        FundraiserOrganizer organizer = organizerUserService.requireCurrentOrganizer();
        Campaign campaign = new Campaign();
        applyForm(campaign, form);
        attachOrganizer(campaign, organizer);
        campaign.setSlug(uniqueSlug(form.title()));
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setDeclaredCollectedAmount(BigDecimal.ZERO);
        Campaign saved = campaignRepository.save(campaign);
        auditService.logCreate("CAMPAIGN", saved.getId());
        return saved;
    }

    @Transactional
    public Campaign update(Long id, CampaignForm form) {
        Campaign campaign = findById(id);
        trackField(campaign.getId(), "title", campaign.getTitle(), form.title());
        trackField(campaign.getId(), "organizerName", campaign.getOrganizerName(), form.organizerName());
        trackField(campaign.getId(), "declaredCollectedAmount",
                str(campaign.getDeclaredCollectedAmount()), str(form.declaredCollectedAmount()));
        applyForm(campaign, form);
        campaign.setUpdatedAt(Instant.now());
        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignDetailView getDetailForOrganizer(Long id) {
        return buildDetail(findById(id));
    }

    @Transactional(readOnly = true)
    public List<Campaign> findAllForOrganizer() {
        return campaignRepository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly = true)
    public long countActiveCampaigns() {
        return campaignRepository.findAll().stream()
                .filter(c -> c.getStatus() == CampaignStatus.ACTIVE)
                .count();
    }

    @Transactional(readOnly = true)
    public long countReports() {
        return reportRepository.count();
    }

    private CampaignDetailView buildDetail(Campaign campaign) {
        var summary = buildSummary(campaign);
        var planLines = planLineRepository.findByCampaignIdOrderBySortOrderAsc(campaign.getId());
        var reports = reportRepository.findByCampaignIdWithLines(campaign.getId());
        reports.forEach(r -> r.getAttachments().size());
        var audit = auditService.findForEntity("CAMPAIGN", campaign.getId());
        var trust = summary.trustStatus();
        return new CampaignDetailView(
                campaign,
                summary,
                trust,
                planLines,
                reports,
                audit,
                analyticsService.planVsFact(planLines, reports),
                analyticsService.transparencyChecklist(campaign, planLines, reports, trust),
                publicUrlService.campaignUrl(campaign.getSlug()));
    }

    private CampaignSummary buildSummary(Campaign campaign) {
        BigDecimal spent = reportLineRepository.sumSpentByCampaignId(campaign.getId());
        BigDecimal planned = planLineRepository.findByCampaignIdOrderBySortOrderAsc(campaign.getId()).stream()
                .map(l -> l.getPlannedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int reportCount = (int) reportRepository.countByCampaignId(campaign.getId());
        LocalDate lastReport = reportRepository.findByCampaignIdOrderBySubmittedAtDesc(campaign.getId()).stream()
                .findFirst()
                .map(r -> r.getPeriodTo())
                .orElse(null);
        var trust = trustStatusService.compute(campaign);
        return CampaignSummary.of(campaign, trust, spent, planned, reportCount, lastReport);
    }

    private void attachOrganizer(Campaign campaign, FundraiserOrganizer organizer) {
        campaign.setFundraiserOrganizer(organizer);
        campaign.setOrganizerName(organizer.getName());
        campaign.setEdrpou(organizer.getEdrpou());
    }

    private void applyForm(Campaign campaign, CampaignForm form) {
        campaign.setTitle(form.title());
        campaign.setDescription(form.description());
        if (form.organizerName() != null && !form.organizerName().isBlank()) {
            campaign.setOrganizerName(form.organizerName());
        }
        if (form.edrpou() != null) {
            campaign.setEdrpou(form.edrpou());
        }
        campaign.setTargetAmount(form.targetAmount());
        if (form.declaredCollectedAmount() != null) {
            campaign.setDeclaredCollectedAmount(form.declaredCollectedAmount());
        }
        campaign.setExternalDonationUrl(form.externalDonationUrl());
        campaign.setExternalWebsiteUrl(form.externalWebsiteUrl());
        campaign.setStartedAt(form.startedAt());
        campaign.setNextReportDue(form.nextReportDue());
        if (form.status() != null) {
            campaign.setStatus(form.status());
        }
    }

    private String uniqueSlug(String title) {
        String base = SlugUtils.toSlug(title);
        String slug = base;
        int i = 1;
        while (campaignRepository.existsBySlug(slug)) {
            slug = base + "-" + i++;
        }
        return slug;
    }

    private void trackField(Long id, String field, String oldVal, String newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            auditService.log("CAMPAIGN", id, "UPDATE", field, oldVal, newVal);
        }
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    public record CampaignForm(
            String title,
            String description,
            String organizerName,
            String edrpou,
            BigDecimal targetAmount,
            BigDecimal declaredCollectedAmount,
            String externalDonationUrl,
            String externalWebsiteUrl,
            LocalDate startedAt,
            LocalDate nextReportDue,
            CampaignStatus status
    ) {
    }
}

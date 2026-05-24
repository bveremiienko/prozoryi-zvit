package ua.prozoryvit.transparency.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private FundraiserOrganizer fundraiserOrganizer;

    @Column(name = "organizer_name", nullable = false)
    private String organizerName;

    private String edrpou;

    @Column(name = "target_amount", precision = 14, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "declared_collected_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal declaredCollectedAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "UAH";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.ACTIVE;

    @Column(name = "external_donation_url", length = 1000)
    private String externalDonationUrl;

    @Column(name = "external_website_url", length = 1000)
    private String externalWebsiteUrl;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "next_report_due")
    private LocalDate nextReportDue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FundraiserOrganizer getFundraiserOrganizer() {
        return fundraiserOrganizer;
    }

    public void setFundraiserOrganizer(FundraiserOrganizer fundraiserOrganizer) {
        this.fundraiserOrganizer = fundraiserOrganizer;
    }

    public String getOrganizerName() {
        if (fundraiserOrganizer != null) {
            return fundraiserOrganizer.getName();
        }
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerSlug() {
        return fundraiserOrganizer != null ? fundraiserOrganizer.getSlug() : null;
    }

    public String getEdrpou() {
        if (edrpou != null) {
            return edrpou;
        }
        return fundraiserOrganizer != null ? fundraiserOrganizer.getEdrpou() : null;
    }

    public void setEdrpou(String edrpou) {
        this.edrpou = edrpou;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getDeclaredCollectedAmount() {
        return declaredCollectedAmount;
    }

    public void setDeclaredCollectedAmount(BigDecimal declaredCollectedAmount) {
        this.declaredCollectedAmount = declaredCollectedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public String getExternalDonationUrl() {
        return externalDonationUrl;
    }

    public void setExternalDonationUrl(String externalDonationUrl) {
        this.externalDonationUrl = externalDonationUrl;
    }

    public String getExternalWebsiteUrl() {
        return externalWebsiteUrl;
    }

    public void setExternalWebsiteUrl(String externalWebsiteUrl) {
        this.externalWebsiteUrl = externalWebsiteUrl;
    }

    public LocalDate getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDate startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDate getNextReportDue() {
        return nextReportDue;
    }

    public void setNextReportDue(LocalDate nextReportDue) {
        this.nextReportDue = nextReportDue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

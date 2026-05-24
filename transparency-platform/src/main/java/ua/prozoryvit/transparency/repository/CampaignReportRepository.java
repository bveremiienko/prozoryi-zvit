package ua.prozoryvit.transparency.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.prozoryvit.transparency.domain.CampaignReport;

public interface CampaignReportRepository extends JpaRepository<CampaignReport, Long> {

    List<CampaignReport> findByCampaignIdOrderBySubmittedAtDesc(Long campaignId);

    long countByCampaignId(Long campaignId);

    @Query("SELECT r FROM CampaignReport r LEFT JOIN FETCH r.lines LEFT JOIN FETCH r.attachments WHERE r.id = :id")
    Optional<CampaignReport> findByIdWithDetails(Long id);

    @Query("SELECT DISTINCT r FROM CampaignReport r LEFT JOIN FETCH r.lines WHERE r.campaign.id = :campaignId ORDER BY r.submittedAt DESC")
    List<CampaignReport> findByCampaignIdWithLines(Long campaignId);
}

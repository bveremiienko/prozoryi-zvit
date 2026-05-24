package ua.prozoryvit.transparency.repository;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.prozoryvit.transparency.domain.ReportLine;

public interface ReportLineRepository extends JpaRepository<ReportLine, Long> {

    @Query("SELECT COALESCE(SUM(rl.amount), 0) FROM ReportLine rl WHERE rl.report.campaign.id = :campaignId")
    BigDecimal sumSpentByCampaignId(Long campaignId);
}

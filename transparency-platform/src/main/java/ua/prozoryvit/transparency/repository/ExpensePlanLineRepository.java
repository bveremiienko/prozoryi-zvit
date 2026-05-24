package ua.prozoryvit.transparency.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.prozoryvit.transparency.domain.ExpensePlanLine;

public interface ExpensePlanLineRepository extends JpaRepository<ExpensePlanLine, Long> {

    List<ExpensePlanLine> findByCampaignIdOrderBySortOrderAsc(Long campaignId);

    long countByCampaignId(Long campaignId);
}

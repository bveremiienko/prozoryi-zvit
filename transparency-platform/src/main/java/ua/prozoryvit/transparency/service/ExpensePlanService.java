package ua.prozoryvit.transparency.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.ExpenseCategory;
import ua.prozoryvit.transparency.domain.ExpensePlanLine;
import ua.prozoryvit.transparency.repository.ExpensePlanLineRepository;

@Service
public class ExpensePlanService {

    private final ExpensePlanLineRepository planLineRepository;
    private final CampaignService campaignService;
    private final AuditService auditService;

    public ExpensePlanService(
            ExpensePlanLineRepository planLineRepository,
            CampaignService campaignService,
            AuditService auditService) {
        this.planLineRepository = planLineRepository;
        this.campaignService = campaignService;
        this.auditService = auditService;
    }

    @Transactional
    public ExpensePlanLine addLine(Long campaignId, PlanLineForm form) {
        Campaign campaign = campaignService.findById(campaignId);
        ExpensePlanLine line = new ExpensePlanLine();
        line.setCampaign(campaign);
        line.setCategory(form.category());
        line.setPlannedAmount(form.plannedAmount());
        line.setNote(form.note());
        line.setSortOrder((int) planLineRepository.countByCampaignId(campaignId));
        ExpensePlanLine saved = planLineRepository.save(line);
        auditService.log("CAMPAIGN", campaignId, "PLAN_ADD", "category", null, form.category().name());
        return saved;
    }

    @Transactional
    public void deleteLine(Long campaignId, Long lineId) {
        ExpensePlanLine line = planLineRepository.findById(lineId)
                .orElseThrow(() -> new NotFoundException("Рядок плану не знайдено"));
        if (!line.getCampaign().getId().equals(campaignId)) {
            throw new NotFoundException("Рядок плану не знайдено");
        }
        planLineRepository.delete(line);
        auditService.log("CAMPAIGN", campaignId, "PLAN_DELETE", "lineId", lineId.toString(), null);
    }

    public record PlanLineForm(ExpenseCategory category, BigDecimal plannedAmount, String note) {
    }
}

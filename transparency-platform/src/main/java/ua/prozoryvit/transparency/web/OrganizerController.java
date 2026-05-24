package ua.prozoryvit.transparency.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.prozoryvit.transparency.domain.CampaignStatus;
import ua.prozoryvit.transparency.domain.ExpenseCategory;
import ua.prozoryvit.transparency.service.CampaignService;
import ua.prozoryvit.transparency.service.ExpensePlanService;
import ua.prozoryvit.transparency.service.OrganizerUserService;
import ua.prozoryvit.transparency.service.ReportService;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {

    private final CampaignService campaignService;
    private final ExpensePlanService expensePlanService;
    private final ReportService reportService;
    private final OrganizerUserService organizerUserService;

    public OrganizerController(
            CampaignService campaignService,
            ExpensePlanService expensePlanService,
            ReportService reportService,
            OrganizerUserService organizerUserService) {
        this.campaignService = campaignService;
        this.expensePlanService = expensePlanService;
        this.reportService = reportService;
        this.organizerUserService = organizerUserService;
    }

    @ModelAttribute("categories")
    public ExpenseCategory[] categories() {
        return ExpenseCategory.values();
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("campaigns", campaignService.findAllForOrganizer());
        model.addAttribute("organization", organizerUserService.requireCurrentOrganizer());
        return "organizer/dashboard";
    }

    @GetMapping("/campaigns/new")
    public String newCampaignForm(Model model) {
        model.addAttribute("organization", organizerUserService.requireCurrentOrganizer());
        return "organizer/campaign-form";
    }

    @PostMapping("/campaigns")
    public String createCampaign(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal targetAmount,
            @RequestParam(required = false) String externalDonationUrl,
            @RequestParam(required = false) String externalWebsiteUrl,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextReportDue,
            RedirectAttributes redirectAttributes) {
        var campaign = campaignService.create(new CampaignService.CampaignForm(
                title, description, null, null, targetAmount, null,
                externalDonationUrl, externalWebsiteUrl, startedAt, nextReportDue, CampaignStatus.ACTIVE));
        redirectAttributes.addFlashAttribute("success", "Кампанію створено");
        return "redirect:/organizer/campaigns/" + campaign.getId() + "/edit";
    }

    @GetMapping("/campaigns/{id}/edit")
    public String editCampaign(@PathVariable Long id, Model model) {
        model.addAttribute("detail", campaignService.getDetailForOrganizer(id));
        model.addAttribute("statuses", CampaignStatus.values());
        return "organizer/campaign-edit";
    }

    @PostMapping("/campaigns/{id}/edit")
    public String updateCampaign(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal targetAmount,
            @RequestParam(required = false) BigDecimal declaredCollectedAmount,
            @RequestParam(required = false) String externalDonationUrl,
            @RequestParam(required = false) String externalWebsiteUrl,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nextReportDue,
            @RequestParam CampaignStatus status,
            RedirectAttributes redirectAttributes) {
        campaignService.update(id, new CampaignService.CampaignForm(
                title, description, null, null, targetAmount, declaredCollectedAmount,
                externalDonationUrl, externalWebsiteUrl, startedAt, nextReportDue, status));
        redirectAttributes.addFlashAttribute("success", "Збережено");
        return "redirect:/organizer/campaigns/" + id + "/edit";
    }

    @GetMapping("/campaigns/{id}/plan")
    public String planPage(@PathVariable Long id, Model model) {
        model.addAttribute("detail", campaignService.getDetailForOrganizer(id));
        return "organizer/plan";
    }

    @PostMapping("/campaigns/{id}/plan")
    public String addPlanLine(
            @PathVariable Long id,
            @RequestParam ExpenseCategory category,
            @RequestParam BigDecimal plannedAmount,
            @RequestParam(required = false) String note,
            RedirectAttributes redirectAttributes) {
        expensePlanService.addLine(id, new ExpensePlanService.PlanLineForm(category, plannedAmount, note));
        redirectAttributes.addFlashAttribute("success", "Рядок плану додано");
        return "redirect:/organizer/campaigns/" + id + "/plan";
    }

    @PostMapping("/campaigns/{id}/plan/{lineId}/delete")
    public String deletePlanLine(
            @PathVariable Long id, @PathVariable Long lineId, RedirectAttributes redirectAttributes) {
        expensePlanService.deleteLine(id, lineId);
        redirectAttributes.addFlashAttribute("success", "Рядок видалено");
        return "redirect:/organizer/campaigns/" + id + "/plan";
    }

    @GetMapping("/campaigns/{id}/reports/new")
    public String newReportForm(@PathVariable Long id, Model model) {
        model.addAttribute("campaign", campaignService.findById(id));
        return "organizer/report-form";
    }

    @PostMapping("/campaigns/{id}/reports")
    public String createReport(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam String summary,
            @RequestParam(required = false) List<ExpenseCategory> lineCategory,
            @RequestParam(required = false) List<BigDecimal> lineAmount,
            @RequestParam(required = false) List<String> lineDescription,
            @RequestParam(required = false) MultipartFile attachment,
            RedirectAttributes redirectAttributes) {
        try {
            List<ReportService.LineForm> lines = buildLineForms(lineCategory, lineAmount, lineDescription);
            List<MultipartFile> files = attachment != null && !attachment.isEmpty()
                    ? List.of(attachment) : List.of();
            var report = reportService.createReport(
                    id, new ReportService.ReportForm(periodFrom, periodTo, summary, lines), files);
            redirectAttributes.addFlashAttribute("success", "Звіт опубліковано");
            return "redirect:/campaigns/" + campaignService.findById(id).getSlug();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/organizer/campaigns/" + id + "/reports/new";
        }
    }

    private List<ReportService.LineForm> buildLineForms(
            List<ExpenseCategory> categories,
            List<BigDecimal> amounts,
            List<String> descriptions) {
        List<ReportService.LineForm> lines = new ArrayList<>();
        if (categories == null) {
            return lines;
        }
        for (int i = 0; i < categories.size(); i++) {
            BigDecimal amount = amounts != null && i < amounts.size() ? amounts.get(i) : null;
            String desc = descriptions != null && i < descriptions.size() ? descriptions.get(i) : null;
            lines.add(new ReportService.LineForm(categories.get(i), amount, desc));
        }
        return lines;
    }
}

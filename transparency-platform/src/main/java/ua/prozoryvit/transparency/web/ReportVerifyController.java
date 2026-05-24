package ua.prozoryvit.transparency.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ua.prozoryvit.transparency.repository.CampaignReportRepository;
import ua.prozoryvit.transparency.service.NotFoundException;
import ua.prozoryvit.transparency.util.ReportHashUtils;

@Controller
public class ReportVerifyController {

    private final CampaignReportRepository reportRepository;

    public ReportVerifyController(CampaignReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/reports/{id}/verify")
    public String verifyPage(@PathVariable Long id, @RequestParam(required = false) String hash, Model model) {
        var report = reportRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("Звіт не знайдено"));
        model.addAttribute("report", report);
        model.addAttribute("campaign", report.getCampaign());
        model.addAttribute("storedHash", report.getReportHash());
        if (hash != null && !hash.isBlank()) {
            String normalized = hash.trim().toLowerCase();
            String computed = ReportHashUtils.computeHash(report);
            model.addAttribute("submittedHash", normalized);
            model.addAttribute("matches", computed.equalsIgnoreCase(normalized));
            model.addAttribute("computedHash", computed);
        }
        return "reports/verify";
    }
}

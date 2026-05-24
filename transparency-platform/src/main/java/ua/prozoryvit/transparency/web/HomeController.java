package ua.prozoryvit.transparency.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.prozoryvit.transparency.service.CampaignService;

@Controller
public class HomeController {

    private final CampaignService campaignService;

    public HomeController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activeCampaigns", campaignService.countActiveCampaigns());
        model.addAttribute("totalReports", campaignService.countReports());
        model.addAttribute("registryCount", campaignService.listPublicSummaries().size());
        return "home";
    }
}

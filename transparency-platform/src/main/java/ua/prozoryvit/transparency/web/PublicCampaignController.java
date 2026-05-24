package ua.prozoryvit.transparency.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.prozoryvit.transparency.service.CampaignService;

@Controller
public class PublicCampaignController {

    private final CampaignService campaignService;

    public PublicCampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping("/campaigns")
    public String registry(Model model) {
        model.addAttribute("campaigns", campaignService.listPublicSummaries());
        return "campaigns/registry";
    }

    @GetMapping("/campaigns/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        model.addAttribute("detail", campaignService.getPublicDetail(slug));
        return "campaigns/detail";
    }
}

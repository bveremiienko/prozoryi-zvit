package ua.prozoryvit.transparency.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.prozoryvit.transparency.service.FundraiserOrganizerService;

@Controller
public class PublicOrganizerController {

    private final FundraiserOrganizerService organizerService;

    public PublicOrganizerController(FundraiserOrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @GetMapping("/organizers")
    public String list(Model model) {
        model.addAttribute("organizers", organizerService.listPublic());
        return "organizers/list";
    }

    @GetMapping("/organizers/{slug}")
    public String profile(@PathVariable String slug, Model model) {
        model.addAttribute("profile", organizerService.getPublicProfile(slug));
        return "organizers/profile";
    }
}

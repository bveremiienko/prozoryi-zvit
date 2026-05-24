package ua.prozoryvit.transparency.web;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.prozoryvit.transparency.repository.CampaignRepository;
import ua.prozoryvit.transparency.service.PublicUrlService;
import ua.prozoryvit.transparency.service.QrCodeService;

@Controller
public class CampaignQrController {

    private final CampaignRepository campaignRepository;
    private final PublicUrlService publicUrlService;
    private final QrCodeService qrCodeService;

    public CampaignQrController(
            CampaignRepository campaignRepository,
            PublicUrlService publicUrlService,
            QrCodeService qrCodeService) {
        this.campaignRepository = campaignRepository;
        this.publicUrlService = publicUrlService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(value = "/campaigns/{slug}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qrCode(@PathVariable String slug) {
        if (campaignRepository.findBySlug(slug).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        byte[] png = qrCodeService.generatePng(publicUrlService.campaignUrl(slug));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }
}

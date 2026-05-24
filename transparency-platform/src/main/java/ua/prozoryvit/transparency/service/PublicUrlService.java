package ua.prozoryvit.transparency.service;

import org.springframework.stereotype.Service;
import ua.prozoryvit.transparency.config.AppProperties;

@Service
public class PublicUrlService {

    private final AppProperties appProperties;

    public PublicUrlService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String campaignUrl(String slug) {
        return baseUrl() + "/campaigns/" + slug;
    }

    public String organizerUrl(String slug) {
        return baseUrl() + "/organizers/" + slug;
    }

    private String baseUrl() {
        String base = appProperties.publicBaseUrl();
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }
}

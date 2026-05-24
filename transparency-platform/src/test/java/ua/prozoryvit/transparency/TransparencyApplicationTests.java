package ua.prozoryvit.transparency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ua.prozoryvit.transparency.domain.TrustStatus;
import ua.prozoryvit.transparency.service.CampaignService;
import ua.prozoryvit.transparency.service.TrustStatusService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "demo"})
class TransparencyApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private TrustStatusService trustStatusService;

    @Test
    void contextLoads() {
    }

    @Test
    void publicRegistryReturnsOk() throws Exception {
        mockMvc.perform(get("/campaigns")).andExpect(status().isOk());
    }

    @Test
    void seededCampaignIsOnTrack() {
        var detail = campaignService.getPublicDetail("dopomoga-medychne-obladnannya");
        assertThat(trustStatusService.compute(detail.campaign())).isEqualTo(TrustStatus.ON_TRACK);
    }

    @Test
    void seededOverdueCampaign() {
        var detail = campaignService.getPublicDetail("generator-osbb-kyiv");
        assertThat(trustStatusService.compute(detail.campaign())).isEqualTo(TrustStatus.OVERDUE);
    }
}

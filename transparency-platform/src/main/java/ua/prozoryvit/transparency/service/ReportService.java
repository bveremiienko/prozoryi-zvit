package ua.prozoryvit.transparency.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.prozoryvit.transparency.domain.Campaign;
import ua.prozoryvit.transparency.domain.CampaignReport;
import ua.prozoryvit.transparency.domain.ExpenseCategory;
import ua.prozoryvit.transparency.domain.ReportAttachment;
import ua.prozoryvit.transparency.domain.ReportLine;
import ua.prozoryvit.transparency.repository.CampaignReportRepository;
import ua.prozoryvit.transparency.repository.CampaignRepository;
import ua.prozoryvit.transparency.util.ReportHashUtils;

@Service
public class ReportService {

    private final CampaignReportRepository reportRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;
    private final AuditService auditService;
    private final FileStorageService fileStorageService;

    public ReportService(
            CampaignReportRepository reportRepository,
            CampaignRepository campaignRepository,
            CampaignService campaignService,
            AuditService auditService,
            FileStorageService fileStorageService) {
        this.reportRepository = reportRepository;
        this.campaignRepository = campaignRepository;
        this.campaignService = campaignService;
        this.auditService = auditService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public CampaignReport createReport(Long campaignId, ReportForm form, List<MultipartFile> files) throws IOException {
        Campaign campaign = campaignService.findById(campaignId);
        CampaignReport report = new CampaignReport();
        report.setCampaign(campaign);
        report.setPeriodFrom(form.periodFrom());
        report.setPeriodTo(form.periodTo());
        report.setSummary(form.summary());
        report.setSubmittedAt(Instant.now());

        List<ReportLine> lines = new ArrayList<>();
        for (LineForm lineForm : form.lines()) {
            if (lineForm.amount() == null || lineForm.amount().signum() <= 0) {
                continue;
            }
            ReportLine line = new ReportLine();
            line.setReport(report);
            line.setCategory(lineForm.category());
            line.setAmount(lineForm.amount());
            line.setDescription(lineForm.description());
            lines.add(line);
        }
        report.setLines(lines);
        report.setReportHash(ReportHashUtils.computeHash(report));

        CampaignReport saved = reportRepository.save(report);
        auditService.logCreate("REPORT", saved.getId());
        auditService.log("CAMPAIGN", campaignId, "REPORT_ADD", "reportId", null, saved.getId().toString());

        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    attachFile(saved, file);
                }
            }
            if (!saved.getAttachments().isEmpty()) {
                saved = reportRepository.save(saved);
            }
        }

        campaign.setNextReportDue(form.periodTo().plusDays(14));
        campaign.setUpdatedAt(Instant.now());
        campaignRepository.save(campaign);
        return reportRepository.findByIdWithDetails(saved.getId()).orElse(saved);
    }

    private void attachFile(CampaignReport report, MultipartFile file) throws IOException {
        FileStorageService.StoredFile stored = fileStorageService.store(
                file, report.getCampaign().getId(), report.getId());
        ReportAttachment attachment = new ReportAttachment();
        attachment.setReport(report);
        attachment.setOriginalFilename(stored.originalFilename());
        attachment.setStoragePath(stored.storagePath());
        attachment.setMimeType(stored.mimeType());
        attachment.setFileSize(stored.size());
        report.getAttachments().add(attachment);
    }

    public record ReportForm(LocalDate periodFrom, LocalDate periodTo, String summary, List<LineForm> lines) {
    }

    public record LineForm(ExpenseCategory category, BigDecimal amount, String description) {
    }
}

package ua.prozoryvit.transparency.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.prozoryvit.transparency.domain.ReportAttachment;
import ua.prozoryvit.transparency.repository.CampaignReportRepository;
import ua.prozoryvit.transparency.service.FileStorageService;
import ua.prozoryvit.transparency.service.NotFoundException;

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    private final CampaignReportRepository reportRepository;
    private final FileStorageService fileStorageService;

    public AttachmentController(CampaignReportRepository reportRepository, FileStorageService fileStorageService) {
        this.reportRepository = reportRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{reportId}/{attachmentId}")
    public ResponseEntity<Resource> download(
            @PathVariable Long reportId, @PathVariable Long attachmentId) throws IOException {
        var report = reportRepository.findByIdWithDetails(reportId)
                .orElseThrow(() -> new NotFoundException("Звіт не знайдено"));
        ReportAttachment attachment = report.getAttachments().stream()
                .filter(a -> a.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вкладення не знайдено"));

        Path path = fileStorageService.resolve(attachment.getStoragePath());
        if (!Files.exists(path)) {
            throw new NotFoundException("Файл не знайдено");
        }
        Resource resource = new UrlResource(path.toUri());
        String contentType = attachment.getMimeType() != null
                ? attachment.getMimeType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + attachment.getOriginalFilename() + "\"")
                .body(resource);
    }
}

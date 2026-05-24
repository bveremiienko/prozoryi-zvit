package ua.prozoryvit.transparency.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import ua.prozoryvit.transparency.domain.CampaignReport;

public final class ReportHashUtils {

    private ReportHashUtils() {
    }

    public static String computeHash(CampaignReport report) {
        StringBuilder canonical = new StringBuilder();
        canonical.append(report.getCampaign().getId()).append('|');
        canonical.append(report.getPeriodFrom()).append('|');
        canonical.append(report.getPeriodTo()).append('|');
        canonical.append(report.getSummary()).append('|');
        report.getLines().forEach(line ->
                canonical.append(line.getCategory()).append(':')
                        .append(line.getAmount()).append(';'));
        return sha256(canonical.toString());
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

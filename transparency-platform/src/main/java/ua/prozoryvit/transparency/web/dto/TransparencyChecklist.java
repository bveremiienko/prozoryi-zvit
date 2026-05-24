package ua.prozoryvit.transparency.web.dto;

public record TransparencyChecklist(
        boolean hasPlan,
        boolean hasReport,
        boolean onSchedule,
        boolean hasAttachments,
        int score,
        int maxScore
) {
    public int scorePercent() {
        if (maxScore == 0) {
            return 0;
        }
        return score * 100 / maxScore;
    }
}

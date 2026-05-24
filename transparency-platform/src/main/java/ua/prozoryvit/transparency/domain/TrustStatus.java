package ua.prozoryvit.transparency.domain;

public enum TrustStatus {
    REGISTERED,
    HAS_PLAN,
    REPORTED,
    ON_TRACK,
    OVERDUE;

    public String getLabelUk() {
        return switch (this) {
            case REGISTERED -> "Зареєстровано";
            case HAS_PLAN -> "Є план витрат";
            case REPORTED -> "Є звіт";
            case ON_TRACK -> "Вчасно звітує";
            case OVERDUE -> "Прострочений звіт";
        };
    }

    public String getCssClass() {
        return switch (this) {
            case REGISTERED -> "trust-neutral";
            case HAS_PLAN -> "trust-neutral";
            case REPORTED -> "trust-ok";
            case ON_TRACK -> "trust-ok";
            case OVERDUE -> "trust-warn";
        };
    }
}

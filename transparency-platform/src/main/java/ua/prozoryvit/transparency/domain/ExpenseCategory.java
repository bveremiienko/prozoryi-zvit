package ua.prozoryvit.transparency.domain;

public enum ExpenseCategory {
    PROCUREMENT,
    LOGISTICS,
    SERVICES,
    OTHER;

    public String getLabelUk() {
        return switch (this) {
            case PROCUREMENT -> "Закупівля";
            case LOGISTICS -> "Логістика";
            case SERVICES -> "Послуги";
            case OTHER -> "Інше";
        };
    }
}

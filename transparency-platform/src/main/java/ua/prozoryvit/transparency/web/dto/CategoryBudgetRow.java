package ua.prozoryvit.transparency.web.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import ua.prozoryvit.transparency.domain.ExpenseCategory;

public record CategoryBudgetRow(
        ExpenseCategory category,
        BigDecimal planned,
        BigDecimal spent
) {
    public String categoryLabel() {
        return category.getLabelUk();
    }

    public BigDecimal remaining() {
        if (planned == null) {
            return BigDecimal.ZERO;
        }
        return planned.subtract(spent != null ? spent : BigDecimal.ZERO);
    }

    public int usagePercent() {
        if (planned == null || planned.signum() == 0) {
            return spent != null && spent.signum() > 0 ? 100 : 0;
        }
        return spent.multiply(BigDecimal.valueOf(100))
                .divide(planned, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    public String statusClass() {
        int pct = usagePercent();
        if (pct > 100) {
            return "budget-over";
        }
        if (pct >= 80) {
            return "budget-warn";
        }
        return "budget-ok";
    }

    public int barWidthPercent() {
        return Math.min(usagePercent(), 100);
    }
}

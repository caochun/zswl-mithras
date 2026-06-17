package cn.zswltech.mithras.budget.enums;

import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BudgetFtpIndustryCategory implements PullDown {
    FTP_PUBLIC_UTILITIES("公用事业类"),
    FTP_CIVIL_CONSUMPTION("民生消费类"),
    FTP_STATE_OWNED_INDUSTRY("国有产业类"),
    FTP_OTHER_INDUSTRY("其他产业类");

    private final String display;

    @Override
    public String display() {
        return this.display;
    }

    public static BudgetFtpIndustryCategory getByName(String name) {
        for (BudgetFtpIndustryCategory item : values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static BudgetFtpIndustryCategory getByDisplay(String display) {
        for (BudgetFtpIndustryCategory item : values()) {
            if (item.getDisplay().equals(display)) {
                return item;
            }
        }
        return null;
    }
}

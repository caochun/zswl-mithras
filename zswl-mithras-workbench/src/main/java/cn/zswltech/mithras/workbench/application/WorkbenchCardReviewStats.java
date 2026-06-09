package cn.zswltech.mithras.workbench.application;

public class WorkbenchCardReviewStats {
    private final String reviewCount;
    private final String passRate;

    public WorkbenchCardReviewStats(String reviewCount, String passRate) {
        this.reviewCount = reviewCount;
        this.passRate = passRate;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public String getPassRate() {
        return passRate;
    }
}

package cn.zswltech.mithras.service.excel;

public class CashFlowExportUtil {
    private CashFlowExportUtil() {
    }

    public static Integer getRepayTimesInYear(String repayRateName) {
        if (repayRateName == null) {
            return null;
        }
        switch (repayRateName) {
            case "DOUBLE_MONTH":
                return 6;
            case "QUARTER":
                return 4;
            case "HALF_YEAR":
                return 2;
            case "YEAR":
                return 1;
            case "MONTH":
                return 12;
            default:
                return null;
        }
    }
}

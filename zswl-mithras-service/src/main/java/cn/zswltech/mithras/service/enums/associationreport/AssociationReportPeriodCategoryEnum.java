package cn.zswltech.mithras.service.enums.associationreport;

import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@AllArgsConstructor
@Getter
public enum AssociationReportPeriodCategoryEnum implements PullDown {
    REALTIME("实时"),
    MONTH("月报"),
    QUARTER("季报");

    private final String display;

    @Override
    public String display() {
        return display;
    }

    public static AssociationReportPeriodCategoryEnum findByName(String name) {
        for (AssociationReportPeriodCategoryEnum item : values()) {
            if (Objects.equals(item.name(), name)) {
                return item;
            }
        }
        return null;
    }

    //获取报送日期
    public static String getReportCategoryDate(AssociationReportPeriodCategoryEnum categoryEnum, Integer reportYear, Integer reportPeriod,long realtimeMinusMonths) {
        switch (categoryEnum) {
            case REALTIME:
                if(realtimeMinusMonths>0){//测试环境，减一个月
                    // 获取当前日期
                    LocalDate currentDate = LocalDate.now();
                    // 获取前一个月的日期
                    LocalDate minusMonthDate = currentDate.minusMonths(realtimeMinusMonths);
                    // 格式化日期输出
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    return minusMonthDate.format(formatter);
                }else{
                    return String.format("%s%04d", reportYear, reportPeriod);
                }
            case MONTH:
                return String.format("%s%02d", reportYear, reportPeriod);
            case QUARTER:
                return String.format("%sQ%d", reportYear, reportPeriod);
            default:
                return null;
        }
    }
    //获取报送日期
    public static String getReportCategoryName(AssociationReportPeriodCategoryEnum categoryEnum, Integer reportPeriod) {
        if (categoryEnum == null) {
            return null;
        }
        switch (categoryEnum) {
            case REALTIME:
                String currentDate = new SimpleDateFormat("MMdd").format(new Date());
                return currentDate;
            case MONTH:
                return String.format("%d月", reportPeriod);
            case QUARTER:
                return String.format("%d季度",reportPeriod);
            default:
                return null;
        }
    }

    //获取报表增量/全量标识
    public static String getReportCategoryIncrementSuffix(AssociationReportCategoryEnum categoryEnum) {
        if (categoryEnum == null) {
            return null;
        }
        switch (categoryEnum) {
            case J0001: {
                return "TQ";
            }
            case J0002: {
                return "TQ";
            }
            case J0003: {
                return "Z";
            }
            case J0004: {
                return "TQ";
            }
            case J0005: {
                return "Q";
            }
            case J0006: {
                return "Q";
            }
            case J0007: {
                return "Q";
            }
            case J0008: {
                return "Q";
            }
            case J0009: {
                return "Q";
            }
            case J0010: {
                return "Q";
            }
            case J0011: {
                return "Q";
            }
            case J0012: {
                return "Q";
            }
            case J0013: {
                return "Z";
            }
            case J0014: {
                return "Z";
            }
            case J0015: {
                return "Z";
            }
            default: {
                return null;
            }
        }
    }

}

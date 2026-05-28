package cn.zswltech.mithras.dto.dashboard;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.PageReq;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * @author bigbear
 * @version 1.0
 * @description 这个类
 * @since 2025/9/1 09:29
 **/
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DashboardFundFinanceBaseREQ extends PageReq {

    @ApiModelProperty("查询时间节点，默认当前日期")
    protected LocalDate queryDate = LocalDate.now();

    @ApiModelProperty(value = "起息日是否在本年")
    private Integer isThisYear;

    @ApiModelProperty(value = "起息日是否在本月")
    private Integer isThisMonth;

    /**
     * 生成该月的最后一天
     *
     * @param queryDate format: yyyy-MM
     */
    @JsonSetter("queryDate")
    public void setQueryDate(String queryDate) {
        LocalDate now = LocalDate.now();
        if (CharSequenceUtil.isBlank(queryDate)) {
            this.queryDate = now;
            return;
        }
        try {
            if (CharSequenceUtil.equals(queryDate, now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))) {
                this.queryDate = now;
            } else {
                this.queryDate = LocalDateTimeUtil.parseDate(queryDate, DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN))
                        .with(TemporalAdjusters.lastDayOfMonth());
            }
        } catch (Exception e) {
            // 解析失败时返回当前日期
            log.error("工作台融资视图查询时解析日期异常，返回当前日期", e);
            this.queryDate = now;
        }
    }

    public void setQueryDate(LocalDate queryDate) {
        this.queryDate = queryDate;
    }
}

package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricCurrency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorType;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorFile;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorMerge;
import cn.zswltech.mithras.metric.service.RiskMetricFactorFileService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.service.third.jk.JinKongMonthlyReportService;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.YunHuReportIndicatorDataHandler;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.req.ReportIndicatorDataReq;
import cn.zswltech.mithras.third.yunhu.infrastructure.client.res.ReportIndicatorDataRes;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
public class JinKongSyncJob {
    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;
    @Resource
    private RiskMetricFactorFileService riskMetricFactorFileService;
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;
    @Resource
    private YunHuReportIndicatorDataHandler yunHuReportIndicatorDataHandler;

    @XxlJob("syncGZKB")
    public void syncGZKB () {
        int year;
        int month;
        try {
            String s = XxlJobHelper.getJobParam();
//            String s = "{\"year\":2025,\"month\":8}";
            if (StrUtil.isNotBlank(s)) {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                year = jsonObject.getInt("year");
                month = jsonObject.getInt("month");
            } else {
                LocalDate now = LocalDate.now();
                LocalDate lastMonthDate = now.minusMonths(1);
                year = lastMonthDate.getYear();
                month = lastMonthDate.getMonthValue();
            }
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = LocalDate.of(year, month, startOfMonth.lengthOfMonth());
            // 判断是否存在对应月份的数据
            LambdaQueryWrapper<RiskMetricFactorMerge> query = Wrappers.lambdaQuery();
            query.eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.GZKB.display);
            query.eq(RiskMetricFactor::getFactorDate, endOfMonth);
            if (riskMetricFactorMergeService.count(query) > 0) {
                log.info("{}年{}月的<国资快报>已经存在，无需重复同步", year, month);
                return;
            }
            // 查询云湖接口
            ReportIndicatorDataReq req = new ReportIndicatorDataReq();
            req.setDt(LocalDateTimeUtil.format(startOfMonth, DatePattern.SIMPLE_MONTH_PATTERN));
            req.setOrg_number(GlobalConstants.ZSZL_MERGE_ORG_CODE);
            req.setPage_num(1);
            // 3000足够了（单个orgNumber的情况下，且3000也是云湖的最大限制）
            req.setPage_size(3000);
            ReportIndicatorDataRes res = yunHuReportIndicatorDataHandler.execute(req);
            if (Objects.isNull(res) || Objects.isNull(res.getData()) || CollectionUtil.isEmpty(res.getData().getData())) {
                log.warn("未查询到{}年{}月的<国资快报>数据，等待下一次同步数据", year, month);
                return;
            }
            List<RiskMetricFactorMerge> toInsertList = new LinkedList<>();
            for (ReportIndicatorDataRes.Data data : res.getData().getData()) {
                RiskMetricFactorMerge factor1 = this.buildFromReportIndicatorData(data, endOfMonth);
                factor1.setFactorName(data.getIndicator_name() + "@本期发生");
                factor1.setFactorValue(Optional.ofNullable(data.getMonth_amount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(10000)).longValue());
                toInsertList.add(factor1);
                RiskMetricFactorMerge factor2 = this.buildFromReportIndicatorData(data, endOfMonth);
                factor2.setFactorName(data.getIndicator_name() + "@本年累计");
                factor2.setFactorValue(Optional.ofNullable(data.getCurrent_amount()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(10000)).longValue());
                toInsertList.add(factor2);
                RiskMetricFactorMerge factor3 = this.buildFromReportIndicatorData(data, endOfMonth);
                factor3.setFactorName(data.getIndicator_name() + "@上年同期累计");
                factor3.setFactorValue(Optional.ofNullable(data.getLytd_amt()).orElse(BigDecimal.ZERO).multiply(BigDecimal.valueOf(10000)).longValue());
                toInsertList.add(factor3);
            }
            riskMetricFactorMergeService.saveBatch(toInsertList);
        } catch (Exception e) {
            log.error("同步<国资快报>发生异常", e);
        }
    }

    private RiskMetricFactorMerge buildFromReportIndicatorData(ReportIndicatorDataRes.Data data, LocalDate factorDate) {
        RiskMetricFactorMerge riskMetricFactorMerge = new RiskMetricFactorMerge();
        riskMetricFactorMerge.setFactorTable(RiskMetricFactorTable.GZKB.display);
        riskMetricFactorMerge.setFactorDate(factorDate);
        riskMetricFactorMerge.setOrgCode(data.getOrg_number());
        riskMetricFactorMerge.setFactorCurrency(RiskMetricCurrency.CNY.name());
        riskMetricFactorMerge.setFactorSource(RiskMetricFactorType.AUTOMATIC.name());
        riskMetricFactorMerge.setFactorCode(data.getIndicator_code());
        return riskMetricFactorMerge;
    }

    @XxlJob("syncAccountBalance")
    public void syncAccountBalance() {
        int year;
        int month;
        try {
            String s = XxlJobHelper.getJobParam();
//            String s = "{\"year\":2025,\"month\":4}";
            if (StrUtil.isNotBlank(s)) {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                year = jsonObject.getInt("year");
                month = jsonObject.getInt("month");
            } else {
                LocalDate now = LocalDate.now();
                LocalDate lastMonthDate = now.minusMonths(1);
                year = lastMonthDate.getYear();
                month = lastMonthDate.getMonthValue();
            }
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = LocalDate.of(year, month, startOfMonth.lengthOfMonth());
            if (this.isExist(endOfMonth, RiskMetricFactorTable.SUBJECT_BALANCE.name())) {
                log.info("{}年{}月的<科目余额表>已经存在，无需重复同步", year, month);
                return;
            }
            jinKongMonthlyReportService.syncAccountBalanceData(year, month);
            try {
                //同步保存科目余额辅助表
                jinKongMonthlyReportService.syncBcmFflexfiledAssistMfByDept(year, month);
            } catch (Exception e) {
                log.error("{}年{}月的<辅助核算维度表>保存异常", year, month, e);
            }
        } catch (Exception e) {
            log.error("同步<科目余额表>发生异常", e);
        }
    }

    @XxlJob("jinKongSyncAssetJob")
    public void jinKongSyncAssetJob() {
        try {
            LocalDate localDate = null;
            String jobParam = XxlJobHelper.getJobParam();
            if (ObjectUtil.isNotEmpty(jobParam)) {
                localDate = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .with(TemporalAdjusters.firstDayOfMonth());
            }
            if (ObjectUtil.isNull(localDate)) {
                localDate = LocalDate.now();
            }
            LocalDate targetDate = localDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            if (this.isExist(targetDate, RiskMetricFactorTable.CAPITAL_BALANCE.name())) {
                log.info("{}年{}月的<资产负债表>已经存在，无需重复同步", targetDate.getYear(), targetDate.getMonthValue());
                return;
            }
            jinKongMonthlyReportService.jinKongSyncAsset(targetDate);
        } catch (Exception e) {
            log.error("同步<资产负债表>发生异常", e);
        }
    }

    @XxlJob("jinKongSyncProfitJob")
    public void jinKongSyncProfitJob() {
        try {
            LocalDate localDate = null;
            String jobParam = XxlJobHelper.getJobParam();
            if (ObjectUtil.isNotEmpty(jobParam)) {
                localDate = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .with(TemporalAdjusters.firstDayOfMonth());
            }
            if (ObjectUtil.isNull(localDate)) {
                localDate = LocalDate.now();
            }
            LocalDate targetDate = localDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            if (this.isExist(targetDate, RiskMetricFactorTable.PROFIT.name())) {
                log.info("{}年{}月的<利润表>已经存在，无需重复同步", targetDate.getYear(), targetDate.getMonthValue());
                return;
            }
            jinKongMonthlyReportService.jinKongSyncProfit(targetDate);
        } catch (Exception e) {
            log.error("同步<利润表>发生异常", e);
        }
    }

    @XxlJob("jinKongSyncCashflowJob")
    public void jinKongSyncCashflowJob() {
        try {
            LocalDate localDate = null;
            String jobParam = XxlJobHelper.getJobParam();
            if (ObjectUtil.isNotEmpty(jobParam)) {
                localDate = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .with(TemporalAdjusters.firstDayOfMonth());
            }
            if (ObjectUtil.isNull(localDate)) {
                localDate = LocalDate.now();
            }
            LocalDate targetDate = localDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            if (this.isExist(targetDate, RiskMetricFactorTable.CASH_FLOW.name())) {
                log.info("{}年{}月的<现金流量表>已经存在，无需重复同步", targetDate.getYear(), targetDate.getMonthValue());
                return;
            }
            jinKongMonthlyReportService.jinKongSyncCashFlow(targetDate);
        } catch (Exception e) {
            log.error("同步<现金流量表>发生异常", e);
        }
    }

    private boolean isExist(LocalDate targetDate, String type) {
        LambdaQueryWrapper<RiskMetricFactorFile> query = Wrappers.lambdaQuery();
        query.eq(RiskMetricFactorFile::getSheetDate, targetDate);
        query.eq(RiskMetricFactorFile::getSheetName, type);
        query.last(StringUtil.mysqlLimitOne());
        RiskMetricFactorFile exist = riskMetricFactorFileService.getOne(query);
        return Objects.nonNull(exist);
    }
}

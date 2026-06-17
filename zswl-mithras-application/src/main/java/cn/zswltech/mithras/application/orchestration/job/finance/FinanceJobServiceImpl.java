package cn.zswltech.mithras.application.orchestration.job.finance;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.enums.CommonProcessPrepareStatus;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.kpi.application.config.KpiParameterConfigService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.finance.enums.financeoverdue.OverduePlanStatueEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.finance.application.job.FinanceJobService;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.model.KpiProvisionBaseInfo;
import cn.zswltech.mithras.kpi.model.KpiProvisionDetail;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import cn.zswltech.mithras.finance.service.FinanceOverdueReportBaseService;
import cn.zswltech.mithras.application.orchestration.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.finance.service.FinanceProjectProfitService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProvisionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.finance.service.third.jk.JinKongMonthlyReportService;
import cn.zswltech.mithras.third.jinkong.client.res.ReportBcmBalanceMfRes;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/8/10
 * @description
 */
@Slf4j
@Component
public class FinanceJobServiceImpl implements FinanceJobService {
    private static final String INCOME_ACCOUNT_NO = "6001";

    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;
    @Resource
    private FinanceProjectProfitService financeProjectProfitService;
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;
    @Resource
    private FtpInterestDetailRecordService ftpInterestDetailRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private KpiProvisionBaseInfoService kpiProvisionBaseInfoService;
    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;

    /**
     * @deprecated 无需定时任务跑数据，用户点击按钮进行
     */
    @Deprecated
    @Override
    public void calculateProjectProfit(String jobParam) {
        // 通过科目余额表的辅助表先获取合同信息，然后通过辅助id去科目余额表取对应的数据得到合同维度的主营收入和风险金余额
        // 主营收入 - 6001 主营业务收入 本年累计  贷方金额
        // 风险金余额 - 6702 信用减值损失 本年累计  借方金额
        // since 2023-10-13 需求变更 风险金从融租易系统取 不再使用苍穹数据
        try {
            LocalDate targetYearMonth;
            if (StrUtil.isBlank(jobParam)) {
                targetYearMonth = LocalDate.now().minusMonths(1);
            } else {
                targetYearMonth = LocalDateTimeUtil.parseDate(jobParam, DatePattern.NORM_DATE_PATTERN);
            }
            int targetYear = targetYearMonth.getYear();
            int targetMonth = targetYearMonth.getMonthValue();
            // 获取拨备计提数据
            KpiProvisionBaseInfo kpiProvisionBaseInfo = kpiProvisionBaseInfoService.getEffectOneByYearMonth(targetYear, targetMonth);
            if (Objects.isNull(kpiProvisionBaseInfo)) {
                log.error("{}年{}月的拨备计提数据不存在，无法计算", targetYear, targetMonth);
                throw new MithrasException("数据缺失");
            }
            List<KpiProvisionDetail> kpiProvisionDetailList = kpiProvisionDetailService.listByProvisionId(kpiProvisionBaseInfo.getId());
            Map<String, List<KpiProvisionDetail>> kpiProvisionDetailMap = kpiProvisionDetailList.stream().collect(Collectors.groupingBy(KpiProvisionDetail::getContractCode));
            // 调用金控接口获取科目余额表数据
            List<ReportBcmBalanceMfRes.Data> bcmBalanceDataList = jinKongMonthlyReportService.listBcmBalanceData(GlobalConstants.ZSZL_LOCAL_ORG_CODE, targetYear, targetMonth);
            if (CollectionUtil.isEmpty(bcmBalanceDataList)) {
                throw new MithrasException(String.format("没有获取到%s年%s月的科目余额表数据", targetYear, targetMonth));
            }
            // 过滤出科目余额表中指定科目编码的数据
            bcmBalanceDataList.removeIf(item -> (StrUtil.isBlank(item.getAcct_no())) || (!item.getAcct_no().startsWith(INCOME_ACCOUNT_NO)));
            if (CollectionUtil.isEmpty(bcmBalanceDataList)) {
                throw new MithrasException("科目余额表中没有指定科目编码的数据");
            }
            log.info("科目余额表过滤后的数据:{}", JSONUtil.toJsonStr(bcmBalanceDataList));
            // 调用金控接口获取辅助核算表数据
            Map<Long, String> helpMap = jinKongMonthlyReportService.listBcmFflexAssistData();
            if (CollectionUtil.isEmpty(helpMap)) {
                throw new MithrasException("科目余额辅助核算表中没有合同类型的数据");
            }
            log.info("辅助核算表过滤后的数据:{}", JSONUtil.toJsonStr(helpMap));
            // 计算每一个合同的项目利润数据
            Map<String, List<ReportBcmBalanceMfRes.Data>> bcmBalanceMap = bcmBalanceDataList.stream().collect(Collectors.groupingBy(ReportBcmBalanceMfRes.Data::getFassgrpid));
            List<FinanceProjectProfitDetail> detailList = new LinkedList<>();
            // 查询费用计提比例
            BigDecimal expenseRadio = kpiParameterConfigService.ensureExpenseRadio(1L);
            // 计算项目利润相关金额
            for (Map.Entry<Long, String> entry : helpMap.entrySet()) {
                Long fid = entry.getKey();
                String contractCode = entry.getValue();
                if (Objects.isNull(fid) || StrUtil.isBlank(contractCode)) {
                    log.error("辅助核算必要数据不存在[{}]", JSONUtil.toJsonStr(entry));
                    continue;
                }
                List<ReportBcmBalanceMfRes.Data> balanceList = bcmBalanceMap.get(String.valueOf(fid));
                if (CollectionUtil.isEmpty(balanceList)) {
                    log.error("通过辅助核算id没有找到对应的科目余额表数据[fid:{}]", fid);
                    continue;
                } else {
                    log.info("通过辅助核算id找到的科目余额表数据[fid:{}, contractCode:{}, data:{}]", fid, contractCode, JSONUtil.toJsonStr(balanceList));
                }
                FinanceProjectProfitDetail detail = this.doCalculate(targetYearMonth, contractCode, balanceList, kpiProvisionDetailMap.get(contractCode), expenseRadio);
                if (Objects.nonNull(detail)) {
                    detailList.add(detail);
                }
            }
            // 执行DB操作
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    // 查询是否已有数据
                    FinanceProjectProfit exist = financeProjectProfitService.getOneByYearMonth(targetYear, targetMonth);
                    if (Objects.nonNull(exist)) {
                        // 删除已有数据
                        financeProjectProfitService.removeById(exist.getId());
                        financeProjectProfitDetailService.removeByProjectProfitId(exist.getId());
                    }
                    // 保存数据
                    this.save(targetYear, targetMonth, detailList);
                } catch (Exception e) {
                    log.error("保存项目利润数据异常", e);
                }
            });
        } catch (Exception e) {
            log.error("计算项目利润发生异常", e);
        }
    }

    /**
     * 维护所有未反核销的收付款
     **/
    @Override
    public void cancelWriteRecordAll() {
        try {
            //付款
            List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getCancelWriteOffFlag, YesOrNoNumberEnum.NO.getCode())
                    .lt(PaymentActualDetail::getPaidInAmount, 0));
            if(CollectionUtil.isNotEmpty(paymentActualDetails)){
                paymentActualDetails.forEach(paymentActualDetailService::cancelWriteRecord);
            }
            List<CollectionRecordInfo> collectionRecordInfos = collectionRecordInfoService.list(Wrappers.<CollectionRecordInfo>lambdaQuery()
                    .eq(CollectionRecordInfo::getCancelWriteOffFlag, YesOrNoNumberEnum.NO.getCode())
                    .lt(CollectionRecordInfo::getCollectionAmount, 0));
            if(CollectionUtil.isNotEmpty(collectionRecordInfos)){
                collectionRecordInfos.forEach(collectionRecordInfoService::cancelWriteRecord);
            }
        } catch (Exception e) {
            log.error("维护所有未反核销的收付款数据发生异常", e);
        }
    }

    private FinanceProjectProfitDetail doCalculate(LocalDate targetMonth, String contractCode, List<ReportBcmBalanceMfRes.Data> bcmBalanceList, List<KpiProvisionDetail> kpiProvisionDetailList, BigDecimal expenseRadio) {
        // 计算目标月份的数据
        try {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getValidOneByContractCode(contractCode);
            if (Objects.isNull(contractBaseInfo)) {
                throw new MithrasException("没有找到对应的合同信息");
            }
            // 收入
            long totalIncomeThisYear = 0L;
            for (ReportBcmBalanceMfRes.Data balanceData : bcmBalanceList) {
                if (balanceData.getAcct_no().startsWith(INCOME_ACCOUNT_NO) && Objects.nonNull(balanceData.getCredit_ytd())) {
                    totalIncomeThisYear += Util.mithrasLongDecimalTwo(balanceData.getCredit_ytd().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
                }
            }
            // 风险金
            long totalRiskThisYear = 0L;
            if (CollectionUtil.isNotEmpty(kpiProvisionDetailList)) {
                totalRiskThisYear = kpiProvisionDetailList.stream().filter(e -> Objects.nonNull(e.getProfitCurrent())).mapToLong(KpiProvisionDetail::getProfitCurrent).sum();
            }
            // 资金成本
            long totalCostThisYear = this.calculateTotalCostThisYear(targetMonth, contractBaseInfo.getId());
            // 附加税
            long totalAdditionalTaxThisYear = this.calculateAdditionalTax(contractBaseInfo, totalIncomeThisYear, totalCostThisYear);
            // 印花税
            long totalStampTaxThisYear = this.calculateStampTax(contractBaseInfo, targetMonth);
            log.info("<{}>计算项目利润相关数据[收入:{}, 风险金:{}, 资金成本:{}, 附加税:{}, 印花税:{}, 费用计提比例:{}]", contractCode, totalIncomeThisYear, totalRiskThisYear, totalCostThisYear, totalAdditionalTaxThisYear, totalStampTaxThisYear, expenseRadio.toPlainString());
            // 项目利润 = (收入 - 资金成本 - 附加税 - 印花税 - 拨备(风险金)) * (1 - 费用计提比例)
            long totalProfitThisYear = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(totalIncomeThisYear - totalCostThisYear - totalAdditionalTaxThisYear - totalStampTaxThisYear - totalRiskThisYear).multiply(BigDecimal.ONE.subtract(expenseRadio)).longValue());
            // 封装对象
            FinanceProjectProfitDetail detail = new FinanceProjectProfitDetail();
            detail.setYear(targetMonth.getYear());
            detail.setMonth(targetMonth.getMonthValue());
            detail.setExpenseRadio(expenseRadio.multiply(BigDecimal.valueOf(1000000)).intValue());
            detail.setContractId(contractBaseInfo.getId());
            detail.setContractStartDate(paymentBaseInfoService.getEarliestPayDate(contractBaseInfo.getId()));
            detail.setTotalIncomeThisYear(totalIncomeThisYear);
            detail.setTotalCostThisYear(totalCostThisYear);
            detail.setTotalRiskThisYear(totalRiskThisYear);
            detail.setTotalAdditionalTaxThisYear(totalAdditionalTaxThisYear);
            detail.setTotalStampTaxThisYear(totalStampTaxThisYear);
            detail.setTotalProfitThisYear(totalProfitThisYear);
            this.fillDiffThisMonth(detail, targetMonth);
            return detail;
        } catch (Exception e) {
            log.error("计算<{}>的项目利润发生异常", contractCode, e);
        }
        return null;
    }

    private long calculateTotalCostThisYear(LocalDate targetDate, Long contractId) {
        // 资金成本(FTP计息是借据维度的，合同维度需加总)
        List<FtpInterestDetailRecord> ftpInterestDetailRecordList = ftpInterestDetailRecordService.listEndOfMonthByContractYearMonth(targetDate.getYear(), targetDate.getMonthValue(), contractId);
        return ftpInterestDetailRecordList.stream().mapToLong(FtpInterestDetailRecord::getTotalInterestThisYear).sum();
    }

    private long calculateAdditionalTax(ContractBaseInfo contractBaseInfo, long totalIncomeThisYear, long totalCostThisYear) {
        // 附加税 = max(0, (当年累计收入 - 当年累计资金成本) * 增值税税率 * 0.12)
        BigDecimal additionalTaxRate = kpiParameterConfigService.ensureZZSRate(contractBaseInfo.getBizType(), contractBaseInfo.getLeaseType());
        if (Objects.isNull(additionalTaxRate)) {
            throw new MithrasException(String.format("<%s>没有获取到对应的增值税税率，放弃计算项目利润", contractBaseInfo.getContractCode()));
        }
        BigDecimal result = BigDecimal.valueOf(totalIncomeThisYear).subtract(BigDecimal.valueOf(totalCostThisYear)).multiply(additionalTaxRate).multiply(BigDecimal.valueOf(0.12));
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return 0L;
        } else {
            return Util.mithrasLongDecimalTwo(result.longValue());
        }
    }

    private long calculateStampTax(ContractBaseInfo contractBaseInfo, LocalDate targetMonth) {
        if (!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
            // 印花税(非直租) = (首次核销在当年的付款申请对应的借据的租金总和 + 实际核销的咨询费总和) * 万分之5
            List<PaymentBaseInfo> todoPaymentList = paymentBaseInfoService.listWriteOffTargetYear(contractBaseInfo.getId(), targetMonth.getYear());
            if (CollectionUtil.isEmpty(todoPaymentList)) {
                return 0L;
            }
            // 计算租金总和
            Set<Long> receiptIds = todoPaymentList.stream().filter(item -> Objects.nonNull(item.getReceiptIdFinal())).map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet());
            List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipts(receiptIds);
            long totalRent = contractRentActualList.stream().filter(e -> StrUtil.isNotBlank(e.getCashFlowCode())).mapToLong(ContractRentActual::getRent).sum();
            // 计算咨询费总和
            LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
            query.in(CollectionBaseInfo::getPaymentId, todoPaymentList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet()));
            query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.OTHERAMOUNT.name());
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(query);
            long totalConsultingFee = 0L;
            if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
                List<CollectionRecordInfo> collectionRecordInfoList = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toSet()));
                totalConsultingFee = collectionRecordInfoList.stream().mapToLong(CollectionRecordInfo::getCollectionAmount).sum();
            }
            BigDecimal b = BigDecimal.valueOf(totalRent + totalConsultingFee).multiply(BigDecimal.valueOf(0.0005));
            return Util.mithrasLongDecimalTwo(b.longValue());
        } else {
            // 印花税（直租） = (租金表的总租金 + 付款单上的咨询费) * 万分之0.5 + 付款单上的全额付款 * 万分之3
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByContractId(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                return 0L;
            }
            // 去掉没有关联借据的
            paymentBaseInfoList.removeIf(e -> Objects.isNull(e.getReceiptIdFinal()));
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                return 0L;
            }
            Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            Set<Long> receiptIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getReceiptIdFinal).collect(Collectors.toSet());
            // 查询收款
            LambdaQueryWrapper<CollectionBaseInfo> collectionQuery = Wrappers.lambdaQuery();
            collectionQuery.eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId());
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectionQuery);
            // 租金总额
            long totalRent = collectionBaseInfoList.stream()
                    .filter(e -> receiptIds.contains(e.getReceiptId()))
                    .filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                    .filter(e -> Objects.nonNull(e.getPlanCollectionAmount()))
                    .mapToLong(CollectionBaseInfo::getPlanCollectionAmount)
                    .sum();
            // 咨询费总额
            long totalConsulting = collectionBaseInfoList.stream()
                    .filter(e -> paymentIds.contains(e.getPaymentId()))
                    .filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.OTHERAMOUNT.name()))
                    .filter(e -> Objects.nonNull(e.getCollectionAmount()))
                    .mapToLong(CollectionBaseInfo::getCollectionAmount)
                    .sum();
            // 查询付款
            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentIds(paymentIds);
            // 付款总额
            long totalPay = paymentActualDetailList.stream()
                    .filter(e -> Objects.nonNull(e.getPaidInAmount()))
                    .mapToLong(PaymentActualDetail::getPaidInAmount)
                    .sum();
            BigDecimal result = BigDecimal.valueOf(totalRent + totalConsulting).multiply(BigDecimal.valueOf(0.00005)).add(BigDecimal.valueOf(totalPay).multiply(BigDecimal.valueOf(0.0003)));
            return Util.mithrasLongDecimalTwo(result.longValue());
        }
    }

    private void fillDiffThisMonth(FinanceProjectProfitDetail thisMonthDetail, LocalDate currentCalculateDate) {
        // 获取上个月的项目利润数据
        LocalDate lastMonthDate = currentCalculateDate.minusMonths(1);
        FinanceProjectProfitDetail lastMonthDetail = financeProjectProfitDetailService.getSpecificOne(thisMonthDetail.getContractId(), lastMonthDate.getYear(), lastMonthDate.getMonthValue());
        if (Objects.isNull(lastMonthDetail)) {
            // 理论上说明该合同进入投放后第一个月，所以没有上个月的数据，当月相关金额等同于当年金额
            thisMonthDetail.setIncomeThisMonth(thisMonthDetail.getTotalIncomeThisYear());
            thisMonthDetail.setCostThisMonth(thisMonthDetail.getTotalCostThisYear());
            thisMonthDetail.setRiskThisMonth(thisMonthDetail.getRiskThisMonth());
            thisMonthDetail.setProfitThisMonth(thisMonthDetail.getTotalProfitThisYear());
        } else {
            // 存在上月数据则当月减上月得出当月金额
            thisMonthDetail.setIncomeThisMonth(thisMonthDetail.getTotalIncomeThisYear() - Optional.ofNullable(lastMonthDetail.getTotalIncomeThisYear()).orElse(0L));
            thisMonthDetail.setCostThisMonth(thisMonthDetail.getTotalCostThisYear() - Optional.ofNullable(lastMonthDetail.getTotalCostThisYear()).orElse(0L));
            thisMonthDetail.setRiskThisMonth(thisMonthDetail.getTotalRiskThisYear() - Optional.ofNullable(lastMonthDetail.getTotalRiskThisYear()).orElse(0L));
            thisMonthDetail.setProfitThisMonth(thisMonthDetail.getTotalProfitThisYear() - Optional.ofNullable(lastMonthDetail.getTotalProfitThisYear()).orElse(0L));
        }
    }

    private void save(int year, int month, List<FinanceProjectProfitDetail> detailList) {
        // 保存主表
        FinanceProjectProfit financeProjectProfit = new FinanceProjectProfit();
        financeProjectProfit.setYear(year);
        financeProjectProfit.setMonth(month);
        // 计算合计值
        long incomeThisMonth = 0L;
        long costThisMonth = 0L;
        long riskThisMonth = 0L;
        long profitThisMonth = 0L;
        long totalIncomeThisYear = 0L;
        long totalCostThisYear = 0L;
        long totalRiskThisYear = 0L;
        long totalProfitThisYear = 0L;
        long totalAdditionalTaxThisYear = 0L;
        long totalStampTaxThisYear = 0L;
        if (CollectionUtil.isNotEmpty(detailList)) {
            for (FinanceProjectProfitDetail detail : detailList) {
                incomeThisMonth += Optional.ofNullable(detail.getIncomeThisMonth()).orElse(0L);
                costThisMonth += Optional.ofNullable(detail.getCostThisMonth()).orElse(0L);
                riskThisMonth += Optional.ofNullable(detail.getRiskThisMonth()).orElse(0L);
                profitThisMonth += Optional.ofNullable(detail.getProfitThisMonth()).orElse(0L);
                totalIncomeThisYear += Optional.ofNullable(detail.getTotalIncomeThisYear()).orElse(0L);
                totalCostThisYear += Optional.ofNullable(detail.getTotalCostThisYear()).orElse(0L);
                totalRiskThisYear += Optional.ofNullable(detail.getTotalRiskThisYear()).orElse(0L);
                totalProfitThisYear += Optional.ofNullable(detail.getTotalProfitThisYear()).orElse(0L);
                totalAdditionalTaxThisYear += Optional.ofNullable(detail.getTotalAdditionalTaxThisYear()).orElse(0L);
                totalStampTaxThisYear += Optional.ofNullable(detail.getTotalStampTaxThisYear()).orElse(0L);
            }
        }
        financeProjectProfit.setIncomeThisMonth(incomeThisMonth);
        financeProjectProfit.setCostThisMonth(costThisMonth);
        financeProjectProfit.setRiskThisMonth(riskThisMonth);
        financeProjectProfit.setProfitThisMonth(profitThisMonth);
        financeProjectProfit.setTotalIncomeThisYear(totalIncomeThisYear);
        financeProjectProfit.setTotalCostThisYear(totalCostThisYear);
        financeProjectProfit.setTotalRiskThisYear(totalRiskThisYear);
        financeProjectProfit.setTotalProfitThisYear(totalProfitThisYear);
        financeProjectProfit.setTotalAdditionalTaxThisYear(totalAdditionalTaxThisYear);
        financeProjectProfit.setTotalStampTaxThisYear(totalStampTaxThisYear);
        financeProjectProfitService.save(financeProjectProfit);
        // 保存子表
        if (CollectionUtil.isNotEmpty(detailList)) {
            for (FinanceProjectProfitDetail detail : detailList) {
                detail.setProjectProfitId(financeProjectProfit.getId());
            }
            financeProjectProfitDetailService.saveBatch(detailList);
        }
    }

    /**
     * 检查是否有上月逾期情况，无则发起
     **/
    @Override
    public void fianceOverdueMaintenance() {
        try {
            //付款
            LocalDate date = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
            String monthFormat = date.format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
            String format = date.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
            List<CommonProcessPrepare> commonProcessPrepares = commonProcessPrepareService.list(Wrappers.<CommonProcessPrepare>lambdaQuery()
                    .eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.FinanceOverdue.name())
                    .eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name())
                    .eq(CommonProcessPrepare::getBusinessData, format));

            if(financeOverdueReportBaseService.count(Wrappers.<FinanceOverdueReportBase>lambdaQuery()
            .eq(FinanceOverdueReportBase::getPlanDate, date)
            .in(FinanceOverdueReportBase::getReportStatus, OverduePlanStatueEnum.NEW.name(), OverduePlanStatueEnum.PART.name(), OverduePlanStatueEnum.FINISHED.name())) > 0) {
                if (CollectionUtil.isNotEmpty(commonProcessPrepares)) {
                    commonProcessPrepares.forEach(e -> {
                        e.setStatus(CommonProcessPrepareStatus.CLOSED.name());
                    });
                    commonProcessPrepareService.updateBatchById(commonProcessPrepares);
                }
                return;
            }
            //判断是否有合规的
            if(CollectionUtil.isNotEmpty(commonProcessPrepares)) {
                return;
            }
            //获取财务经理岗位人员
            List<Long> userIdList = SpringContextHolder.getBean(SysUserService.class).queryJobUserIds(JobEnum.financialmanager.name());
            CommonProcessPrepare commonProcessPrepare = CommonProcessPrepare.builder().processType(ProcessModelTypeEnum.FinanceOverdue.name()).status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                    .formName("请尽快创建" +monthFormat + "月应收逾期报送计划，并于5日前完成报送").currentNode("财务经理岗").currentAssignee(JSON.toJSONString(userIdList)).businessData(format).build();
            commonProcessPrepareService.save(commonProcessPrepare);
        } catch (Exception e) {
            log.error("维护每月逾期报送发生异常", e);
        }
    }


}

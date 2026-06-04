package cn.zswltech.mithras.others;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.BudgetExaminePayPlanExecuteAddREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoAddREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlan;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPayDetail;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanProfit;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanProfitDetail;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.budget.*;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/5/16
 * @description
 */
public class BudgetPlanTest extends ApplicationTest {
    @Test
    public void eclCallbackTest() {
        SpringUtil.getBean(EclExecutePredictBaseInfoService.class).calculation(28L, null);
    }

    @Test
    public void eclTest() throws Exception {
        BudgetPlan budgetPlan = SpringUtil.getBean(BudgetPlanService.class).getById(19L);
        EclExecutePredictBaseInfoAddREQ req = new EclExecutePredictBaseInfoAddREQ();
        req.setBudgetPlanId(budgetPlan.getId());
        req.setBudgetPlanName(budgetPlan.getPlanName());
        req.setPredictDataFrom(budgetPlan.getBudgetDateFrom());
        req.setPredictDataTo(budgetPlan.getBudgetDateTo());
        req.setSource(YesOrNoNumberEnum.NO.getCode());
        SpringUtil.getBean(EclExecutePredictBaseInfoService.class).create(req);
        Thread.sleep(10000000000000000L);
    }

    @Test
    public void calculationTest() {
        SpringUtil.getBean(EclExecutePredictBaseInfoService.class).calculation(24L, null);
    }

    @Test
    public void fixProfitWithoutExpense() {
        List<BudgetPlanProfitDetail> detailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(
                Wrappers.<BudgetPlanProfitDetail>lambdaQuery().eq(BudgetPlanProfitDetail::getBudgetPlanProfitId, 7L)
        );
        for (BudgetPlanProfitDetail detail : detailList) {
            Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(detail.getBelongDeptId());
            BigDecimal profitOriginalBD = BigDecimal.valueOf(detail.getAssessmentProfitOriginal());
            BigDecimal profitWithoutExpenseOriginalBD = FinancialUtil.calculateProfitWithoutExpense(profitOriginalBD, expenseRate);
            BigDecimal expenseBD = profitOriginalBD.subtract(profitWithoutExpenseOriginalBD);
            detail.setAssessmentProfitWithoutExpenseOriginal(Util.mithrasLongDecimalTwo(profitWithoutExpenseOriginalBD.longValue()));
            detail.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
            detail.setAssessmentProfitWithoutExpense(detail.getAssessmentProfitWithoutExpenseOriginal());
            if (Objects.equals(detail.getDataCategory(), BudgetPlanDataCategoryEnum.HISTORY.name()) && detail.getAssessmentProfitWithoutExpenseOriginal() < 0) {
                detail.setAssessmentProfitWithoutExpense(0L);
            }
        }
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).updateBatchById(detailList);
    }

    @Test
    public void importData() {
        Map<String, Long> orgMap = SpringUtil.getBean(SysUserService.class).listAllDept().stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId));
        ExcelReader excelReader = ExcelUtil.getReader("/Users/dingqi/Downloads/预算管理初始化/利润预算表（合并）.xlsx");
        Long budgetPlanProfitId = 7L;
        BudgetPlanProfit budgetPlanProfit = SpringUtil.getBean(BudgetPlanProfitService.class).getById(budgetPlanProfitId);
        List<BudgetPlanProfitDetail> toInsertList = new LinkedList<>();
        // 存量项目
        excelReader.setSheet("存量项目");
        List<List<Object>> historyDataList = excelReader.read(1);
        if (CollectionUtil.isNotEmpty(historyDataList)) {
            for (List<Object> row : historyDataList) {
                Long belongDeptId = orgMap.get(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
                String contractCode = Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null);
                String receiptCode = Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null);
                BudgetPlanProfitDetail detail = this.initDetail(budgetPlanProfit, contractCode, receiptCode);
                detail.setBelongDeptId(belongDeptId);
                detail.setDataCategory(BudgetPlanDataCategoryEnum.HISTORY.name());
                detail.setEndOfLastPeriodBalance(Optional.ofNullable(row.get(4)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setEndOfThisPeriodBalance(Optional.ofNullable(row.get(5)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setIncome(Optional.ofNullable(row.get(6)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setIncomeWithoutTax(Optional.ofNullable(row.get(7)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setCost(Optional.ofNullable(row.get(8)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setCostWithoutTax(detail.getCost());
                detail.setEndOfThisPeriodRiskFund(Optional.ofNullable(row.get(9)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setEndOfLastPeriodRiskFund(Optional.ofNullable(row.get(10)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setRiskFundDiff(Optional.ofNullable(row.get(11)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAdditionalTax(Optional.ofNullable(row.get(13)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setStampTax(Optional.ofNullable(row.get(14)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAssessmentProfitOriginal(Optional.ofNullable(row.get(15)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAssessmentProfit(detail.getAssessmentProfitOriginal() < 0 ? 0 : detail.getAssessmentProfitOriginal());
                detail.setGrossProfit(detail.getIncomeWithoutTax() - detail.getCost() - detail.getAdditionalTax() - detail.getStampTax());
                Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(belongDeptId);
                BigDecimal expenseBD = BigDecimal.valueOf(detail.getAssessmentProfit()).multiply(BigDecimal.valueOf(expenseRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                detail.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
                detail.setAssessmentProfitWithoutExpense(detail.getAssessmentProfit() - detail.getExpense());
                detail.setAssessmentProfitWithoutExpenseOriginal(detail.getAssessmentProfitOriginal() - detail.getExpense());
                toInsertList.add(detail);
            }
        }
        // 新增项目
        excelReader.setSheet("公司新增业务");
        List<List<Object>> futureDataList = excelReader.read(1);
        if (CollectionUtil.isNotEmpty(futureDataList)) {
            for (List<Object> row : futureDataList) {
                Long belongDeptId = orgMap.get(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
                String contractCode = Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null);
                BudgetPlanProfitDetail detail = this.initDetail(budgetPlanProfit, contractCode, null);
                detail.setBelongDeptId(belongDeptId);
                detail.setDataCategory(BudgetPlanDataCategoryEnum.FUTURE.name());
                detail.setEndOfLastPeriodBalance(0L);
                detail.setEndOfThisPeriodBalance(Optional.ofNullable(row.get(12)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setIncome(Optional.ofNullable(row.get(3)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setIncomeWithoutTax(Optional.ofNullable(row.get(4)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setCost(Optional.ofNullable(row.get(5)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setCostWithoutTax(detail.getCost());
                detail.setEndOfThisPeriodRiskFund(Optional.ofNullable(row.get(8)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setEndOfLastPeriodRiskFund(Optional.ofNullable(row.get(9)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setRiskFundDiff(Optional.ofNullable(row.get(10)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAdditionalTax(Optional.ofNullable(row.get(6)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setStampTax(Optional.ofNullable(row.get(7)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAssessmentProfitOriginal(Optional.ofNullable(row.get(11)).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L));
                detail.setAssessmentProfit(detail.getAssessmentProfitOriginal());
                detail.setGrossProfit(detail.getIncomeWithoutTax() - detail.getCost() - detail.getAdditionalTax() - detail.getStampTax());
                Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(belongDeptId);
                BigDecimal expenseBD = BigDecimal.valueOf(detail.getAssessmentProfit()).multiply(BigDecimal.valueOf(expenseRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                detail.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
                detail.setAssessmentProfitWithoutExpense(detail.getAssessmentProfit() - detail.getExpense());
                detail.setAssessmentProfitWithoutExpenseOriginal(detail.getAssessmentProfitOriginal() - detail.getExpense());
                toInsertList.add(detail);
            }
        }
        System.out.println("1111111");
        // 更新数据
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).remove(Wrappers.<BudgetPlanProfitDetail>lambdaQuery().eq(BudgetPlanProfitDetail::getBudgetPlanProfitId, budgetPlanProfitId));
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).saveBatch(toInsertList);
    }

    private BudgetPlanProfitDetail initDetail(BudgetPlanProfit budgetPlanProfit, String contractCode, String receiptCode) {
        BudgetPlanProfitDetail budgetPlanProfitDetail = new BudgetPlanProfitDetail();
        budgetPlanProfitDetail.setBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        budgetPlanProfitDetail.setBudgetPlanProfitId(budgetPlanProfit.getId());
        budgetPlanProfitDetail.setPeriodType("YEAR");
        budgetPlanProfitDetail.setPeriodValue("2025");
        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getValidOneByContractCode(contractCode);
        budgetPlanProfitDetail.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
        if (StrUtil.isBlank(receiptCode)) {
            List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
            contractReceiptList.removeIf(e -> StrUtil.isBlank(e.getReceiptCode()));
            contractReceiptList.sort(Comparator.comparing(ContractReceipt::getId).reversed());
            receiptCode = contractReceiptList.get(0).getReceiptCode();
        }
        ContractReceipt contractReceipt = SpringUtil.getBean(ContractReceiptService.class).getOne(
                Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getReceiptCode, receiptCode).orderByDesc(ContractReceipt::getId).last(StringUtil.mysqlLimitOne())
        );
        budgetPlanProfitDetail.setClientId(contractBaseInfo.getClientId());
        Client client = SpringUtil.getBean(ClientService.class).getById(contractBaseInfo.getClientId());
        budgetPlanProfitDetail.setClientName(client.getClientName());
        budgetPlanProfitDetail.setProjReviewId(contractBaseInfo.getProjReviewId());
        budgetPlanProfitDetail.setContractId(contractBaseInfo.getId());
        budgetPlanProfitDetail.setContractCode(contractBaseInfo.getContractCode());
        budgetPlanProfitDetail.setReceiptId(contractReceipt.getId());
        budgetPlanProfitDetail.setReceiptCode(contractReceipt.getReceiptCode());
        ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(contractBaseInfo.getProjReviewId());
        budgetPlanProfitDetail.setFtpIndustryCategory(projReviewBaseInfo.getFtpIndustryCategory());
        budgetPlanProfitDetail.setRiskControlIndustryClassify(projReviewBaseInfo.getRiskControlIndustryClassify());
        budgetPlanProfitDetail.setLeaseType(contractBaseInfo.getLeaseType());
        ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).getByContractId(contractBaseInfo.getId());
        budgetPlanProfitDetail.setTermMonth(contractLeasePrice.getLeaseMonthCount());
        budgetPlanProfitDetail.setRepayFrequency(contractLeasePrice.getRepayRate());
        budgetPlanProfitDetail.setProjName(contractBaseInfo.getProjName());
        long deposit = Optional.ofNullable(contractLeasePrice.getEarnestMoney()).orElse(0L);
        long consultingFee = Optional.ofNullable(contractLeasePrice.getConsultingFee()).orElse(0L);
        BigDecimal depositRateBD = BigDecimal.valueOf(deposit).divide(BigDecimal.valueOf(contractLeasePrice.getApplyCreditAmount()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000));
        budgetPlanProfitDetail.setDepositRate(Util.mithrasIntegerDecimalTwo(depositRateBD.intValue()));
        budgetPlanProfitDetail.setContractInterestRate(Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0));
        budgetPlanProfitDetail.setIrr(contractReceipt.getActualIrr());
        budgetPlanProfitDetail.setXirr(contractReceipt.getXirr());
        BigDecimal consultingFeeRateBD = BigDecimal.valueOf(consultingFee).divide(BigDecimal.valueOf(contractLeasePrice.getApplyCreditAmount()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000));
        budgetPlanProfitDetail.setConsultingFeeRate(Util.mithrasIntegerDecimalTwo(consultingFeeRateBD.intValue()));
        BigDecimal consultingFeeRateYearBD = consultingFeeRateBD.divide(BigDecimal.valueOf(contractLeasePrice.getLeaseMonthCount()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP), 20, RoundingMode.HALF_UP);
        budgetPlanProfitDetail.setConsultingFeeRateYear(Util.mithrasIntegerDecimalTwo(consultingFeeRateYearBD.intValue()));
        FtpAssessmentInfo ftpAssessmentInfo = SpringUtil.getBean(FtpAssessmentInfoService.class).findLatestEffect(LocalDate.now(), contractReceipt.getId());
        if (Objects.nonNull(ftpAssessmentInfo) && Objects.nonNull(ftpAssessmentInfo.getAssessmentPrice())) {
            budgetPlanProfitDetail.setFtp(ftpAssessmentInfo.getAssessmentPrice().intValue());
        }
        budgetPlanProfitDetail.setPayDate(contractReceipt.getReceiptStartDate());
        budgetPlanProfitDetail.setPayDateYear(contractReceipt.getReceiptStartDate().getYear());
        budgetPlanProfitDetail.setPayDateMonth(contractReceipt.getReceiptStartDate().getMonthValue());
        budgetPlanProfitDetail.setActualPay(contractLeasePrice.getApplyCreditAmount());
        return budgetPlanProfitDetail;
    }

    private String buildMapKey(Long belongDeptId, String receiptCode) {
        return belongDeptId + "-" + receiptCode;
    }

    @Test
    public void budgetPlanPayExecuteAddTest() {
        BudgetExaminePayPlanExecuteAddREQ req = new BudgetExaminePayPlanExecuteAddREQ();
        req.setBudgetExamineId(39L);
        SpringUtil.getBean(BudgetExaminePayPlanExecuteService.class).add(req);
    }

    @Test
    public void initCostDetailTest() {
        SpringUtil.getBean(BudgetPlanCostService.class).initCostDetail(3L);
//        SpringUtil.getBean(BudgetPlanCostService.class).initCostDetail(18L);
//        SpringUtil.getBean(BudgetPlanCostService.class).initCostDetail(20L);
    }

    @Test
    public void initProfitDetailTest() {
        SpringUtil.getBean(BudgetPlanProfitService.class).initProfitDetail(40L);
//        BudgetPlanProfit budgetPlanProfit = SpringUtil.getBean(BudgetPlanProfitService.class).getOneByBudgetPlanId(17L);
//        ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getById(1157L);
//        SpringUtil.getBean(BudgetPlanProfitService.class).calculateByContract(budgetPlanProfit, contractBaseInfo);
    }

    @Test
    public void budgetPlanPayCalculateProfitTest() {
        BudgetPlanPayDetail budgetPlanPayDetail = SpringUtil.getBean(BudgetPlanPayDetailService.class).getById(1L);
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
        budgetPlanPayDetail = SpringUtil.getBean(BudgetPlanPayDetailService.class).getById(2L);
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
        budgetPlanPayDetail = SpringUtil.getBean(BudgetPlanPayDetailService.class).getById(3L);
        SpringUtil.getBean(BudgetPlanProfitService.class).recalculateByBudgetPlanPayDetail(budgetPlanPayDetail);
    }

    @Test
    public void findHistoryExcelDiff() {
        String fileName = "/Users/dingqi/Downloads/预算数据核对/利润预算表_修复FTP初始化数据后.xlsx";
        ExcelReader excelReader = ExcelUtil.getReader(FileUtil.getInputStream(fileName));
        // 存量
        List<Map<String, Object>> offlineDataList = excelReader.setSheet("存量项目-台账").read(0, 1, 422);
        Map<String, Map<String, Object>> offlineDataMap = new LinkedHashMap<>();
        for (Map<String, Object> map : offlineDataList) {
            offlineDataMap.put(map.get("业务部门") + "@" + map.get("借据编号"), map);
        }
        List<Map<String, Object>> onlineDataList = excelReader.setSheet("存量项目-系统").read(0, 1, 428);
        Map<String, Map<String, Object>> onlineDataMap = new LinkedHashMap<>();
        for (Map<String, Object> map : onlineDataList) {
            onlineDataMap.put(map.get("业务部门") + "@" + map.get("借据编号"), map);
        }
        // 比对
        List<List<Object>> resultDataList = new LinkedList<>();
        for (Map.Entry<String, Map<String, Object>> entry : offlineDataMap.entrySet()) {
            Map<String, Object> online = onlineDataMap.get(entry.getKey());
            if (CollectionUtil.isEmpty(online)) {
                resultDataList.add(ListUtil.of(entry.getKey(), entry.getValue().get("合同编号")));
                continue;
            }
            resultDataList.add(ListUtil.of(
                    entry.getKey(),
                    online.get("合同编号"),
                    this.calculateDiff(entry.getValue().get("上年末业务余额（元）"), online.get("上年末业务余额（元）")),
                    this.calculateDiff(entry.getValue().get("本年末业务余额（元）"), online.get("本年末业务余额（元）")),
                    this.calculateDiff(entry.getValue().get("本年累计收入（含税）（元）"), online.get("本年累计收入（含税）（元）")),
                    this.calculateDiff(entry.getValue().get("本年资金成本/经营租赁成本（元）"), online.get("本年资金成本/经营租赁成本（元）")),
                    this.calculateDiff(entry.getValue().get("年末风险金余额（元）"), online.get("年末风险金余额（元）")),
                    this.calculateDiff(entry.getValue().get("年初风险金余额（元）"), online.get("年初风险金余额（元）")),
                    this.calculateDiff(entry.getValue().get("累计风险金（元）"), online.get("累计风险金（元）"))
            ));
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of("业务部门@借据编号", "合同编号", "上年末业务余额（元）", "本年末业务余额（元）", "本年累计收入（含税）（元）", "本年资金成本/经营租赁成本（元）", "年末风险金余额（元）", "年初风险金余额（元）", "累计风险金（元）"));
        for (List<Object> row : resultDataList) {
            excelWriter.writeRow(row);
        }
        excelWriter.flush(FileUtil.getOutputStream("/Users/dingqi/存量项目-差异.xlsx"), true);
    }

    @Test
    public void findFutureExcelDiff() {
        String fileName = "/Users/dingqi/Downloads/预算数据核对/利润预算表_修复FTP初始化数据后.xlsx";
        ExcelReader excelReader = ExcelUtil.getReader(FileUtil.getInputStream(fileName));
        // 存量
        List<Map<String, Object>> offlineDataList = excelReader.setSheet("公司新增业务-台账").read(0, 1, 164);
        Map<String, Map<String, Object>> offlineDataMap = new LinkedHashMap<>();
        for (Map<String, Object> map : offlineDataList) {
            // 查询借据编号
            ContractBaseInfo contractBaseInfo = SpringUtil.getBean(ContractBaseInfoService.class).getValidOneByContractCode(map.get("合同编号").toString());
            ContractReceipt contractReceipt = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId()).get(0);
            offlineDataMap.put(map.get("业务部门") + "@" + contractReceipt.getReceiptCode(), map);
        }
        List<Map<String, Object>> onlineDataList = excelReader.setSheet("公司新增业务-系统").read(0, 1, 190);
        Map<String, Map<String, Object>> onlineDataMap = new LinkedHashMap<>();
        for (Map<String, Object> map : onlineDataList) {
            onlineDataMap.put(map.get("业务部门") + "@" + map.get("借据编号"), map);
        }
        // 比对
        List<List<Object>> resultDataList = new LinkedList<>();
        for (Map.Entry<String, Map<String, Object>> entry : offlineDataMap.entrySet()) {
            Map<String, Object> online = onlineDataMap.get(entry.getKey());
            if (CollectionUtil.isEmpty(online)) {
                resultDataList.add(ListUtil.of(entry.getKey(), entry.getValue().get("合同编号")));
                continue;
            }
            resultDataList.add(ListUtil.of(
                    entry.getKey(),
                    online.get("合同编号"),
                    this.calculateDiff(entry.getValue().get("营业收入（含税）（元）"), online.get("营业收入（含税）（元）")),
                    this.calculateDiff(entry.getValue().get("营业收入（不含税）（元）"), online.get("营业收入（不含税）（元）")),
                    this.calculateDiff(entry.getValue().get("营业成本（不含税）（元）"), online.get("营业成本（不含税）（元）")),
                    this.calculateDiff(entry.getValue().get("附加税"), online.get("附加税")),
                    this.calculateDiff(entry.getValue().get("印花税"), online.get("印花税")),
                    this.calculateDiff(entry.getValue().get("本年风险金余额"), online.get("本年风险金余额")),
                    this.calculateDiff(entry.getValue().get("年初风险金余额"), online.get("年初风险金余额")),
                    this.calculateDiff(entry.getValue().get("累计风险金"), online.get("累计风险金")),
                    this.calculateDiff(entry.getValue().get("考核利润"), online.get("考核利润")),
                    this.calculateDiff(entry.getValue().get("本年末资产余额（元）"), online.get("本年末资产余额（元）"))
            ));
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        excelWriter.writeHeadRow(ListUtil.of("业务部门@借据编号", "合同编号", "营业收入（含税）（元）", "营业收入（不含税）（元）", "营业成本（不含税）（元）", "附加税", "印花税", "本年风险金余额", "年初风险金余额", "累计风险金", "考核利润", "本年末资产余额（元）"));
        for (List<Object> row : resultDataList) {
            excelWriter.writeRow(row);
        }
        excelWriter.flush(FileUtil.getOutputStream("/Users/dingqi/公司新增业务-差异.xlsx"), true);
    }

    private BigDecimal calculateDiff(Object offline, Object online) {
        BigDecimal offlineBD = Optional.ofNullable(offline).map(e -> new BigDecimal(offline.toString()).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO);
        BigDecimal onlineBD = Optional.ofNullable(online).map(e -> new BigDecimal(online.toString()).setScale(2, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO);
        return offlineBD.subtract(onlineBD);
    }
}

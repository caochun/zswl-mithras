package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.BudgetPlanProfitDetailApplicationService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.budget.enums.BudgetFtpIndustryCategory;
import cn.zswltech.mithras.budget.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.budget.mapper.BudgetPlanProfitDetailMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanProfit;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanProfitDetail;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@Slf4j
@Service
public class BudgetPlanProfitDetailService extends ServiceImpl<BudgetPlanProfitDetailMapper, BudgetPlanProfitDetail> implements BudgetPlanProfitDetailApplicationService {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BudgetPlanProfitService budgetPlanProfitService;

    public List<BudgetPlanProfitDetail> listHistoryByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfitDetail::getDataCategory, BudgetPlanDataCategoryEnum.HISTORY.name());
        query.eq(BudgetPlanProfitDetail::getBudgetPlanId, budgetPlanId);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanProfitDetail::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public void modifyDetail(BudgetPlanProfitDetailModifyREQ req) {
        if (Objects.nonNull(req.getProfitAdjust()) && req.getProfitAdjust() != 0) {
            // 项目调整项如果不为0则需要填备注
            if (StrUtil.isBlank(req.getRemark())) {
                throw new MithrasException("利润调整项不为0时，备注不能为空");
            }
        }
        BudgetPlanProfitDetail exist = this.getById(req.getId());
        if (Objects.isNull(exist)) {
            throw new MithrasException("行数据不存在");
        }
        // 保存
        BudgetPlanProfitDetail update = BeanUtil.copyProperties(req, BudgetPlanProfitDetail.class);
//        update.setId(req.getId());
//        update.setProfitAdjust(req.getProfitAdjust());
//        update.setRemark(req.getRemark());
//        long newAssessmentProfit = Optional.ofNullable(exist.getAssessmentProfitOriginal()).orElse(0L) + req.getProfitAdjust();
//        // 扣费后利润
//        BigDecimal newProfitBD = BigDecimal.valueOf(newAssessmentProfit);
//        Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(exist.getBelongDeptId());
//        BigDecimal newProfitWithoutExpenseBD = FinancialUtil.calculateProfitWithoutExpense(newProfitBD, expenseRate);
//        // 费用 = 利润 - 扣费后利润
//        BigDecimal newExpenseBD = newProfitBD.subtract(newProfitWithoutExpenseBD);
//        update.setExpense(Util.mithrasLongDecimalTwo(newExpenseBD.longValue()));
//        update.setAssessmentProfit(Util.mithrasLongDecimalTwo(newProfitBD.longValue() < 0 ? 0 : newProfitBD.longValue()));
//        update.setAssessmentProfitWithoutExpense(Util.mithrasLongDecimalTwo(newProfitWithoutExpenseBD.longValue() < 0 ? 0 : newProfitWithoutExpenseBD.longValue()));
        this.updateById(update);
    }

    public List<BudgetPlanProfitDetailRSP> deptDetail(BudgetPlanProfitDetailREQ req) {
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = this.initDefaultQuery(req.getBudgetPlanProfitId());
        query.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
        query.eq(BudgetPlanProfitDetail::getDataCategory, BudgetPlanDataCategoryEnum.HISTORY.name());
        if (StrUtil.isNotBlank(req.getClientName())) {
            query.like(BudgetPlanProfitDetail::getClientName, req.getClientName());
        }
        List<BudgetPlanProfitDetail> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(dbList.stream().map(BudgetPlanProfitDetail::getBelongDeptId).collect(Collectors.toSet()));
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(dbList.stream().map(BudgetPlanProfitDetail::getSponsorUserId).collect(Collectors.toSet()));
        List<BudgetPlanProfitDetailRSP> result = dbList.stream().map(e -> {
            BudgetPlanProfitDetailRSP rsp = BeanUtil.copyProperties(e, BudgetPlanProfitDetailRSP.class);
            // 补充信息
            long diff = Optional.ofNullable(e.getIncomeWithoutTax()).orElse(0L) - Optional.ofNullable(e.getCost()).orElse(0L);
            rsp.setDiff(diff);
            rsp.setBelongDeptName(deptNameMap.get(e.getBelongDeptId()));
            rsp.setSponsorUserName(userNameMap.get(e.getSponsorUserId()));
            rsp.setLeaseTypeDisplay(Optional.ofNullable(LeaseType.of(e.getLeaseType())).map(LeaseType::display).orElse(null));
            rsp.setFtpIndustryCategoryDisplay(Optional.ofNullable(BudgetFtpIndustryCategory.getByName(e.getFtpIndustryCategory())).map(BudgetFtpIndustryCategory::display).orElse(null));
            return rsp;
        }).collect(Collectors.toList());
        // 添加合计
        result.add(this.deptDetailSum(dbList));
        return result;
    }

    private BudgetPlanProfitDetailRSP deptDetailSum(List<BudgetPlanProfitDetail> dbList) {
        BudgetPlanProfitDetailRSP rsp = new BudgetPlanProfitDetailRSP();
        rsp.setBelongDeptName("合计");
        for (BudgetPlanProfitDetail detail : dbList) {
            rsp.setEndOfLastPeriodBalance(rsp.getEndOfLastPeriodBalance() + Optional.ofNullable(detail.getEndOfLastPeriodBalance()).orElse(0L));
            rsp.setEndOfThisPeriodBalance(rsp.getEndOfThisPeriodBalance() + Optional.ofNullable(detail.getEndOfThisPeriodBalance()).orElse(0L));
            rsp.setIncome(rsp.getIncome() + Optional.ofNullable(detail.getIncome()).orElse(0L));
            rsp.setIncomeWithoutTax(rsp.getIncomeWithoutTax() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
            rsp.setCost(rsp.getCost() + Optional.ofNullable(detail.getCost()).orElse(0L));
            rsp.setCostWithoutTax(rsp.getCostWithoutTax() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
            rsp.setGrossProfit(rsp.getGrossProfit() + Optional.ofNullable(detail.getGrossProfit()).orElse(0L));
            rsp.setEndOfLastPeriodRiskFund(rsp.getEndOfLastPeriodRiskFund() + Optional.ofNullable(detail.getEndOfLastPeriodRiskFund()).orElse(0L));
            rsp.setEndOfThisPeriodRiskFund(rsp.getEndOfThisPeriodRiskFund() + Optional.ofNullable(detail.getEndOfThisPeriodRiskFund()).orElse(0L));
            rsp.setRiskFundDiff(rsp.getRiskFundDiff() + Optional.ofNullable(detail.getRiskFundDiff()).orElse(0L));
            long diff = Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L) - Optional.ofNullable(detail.getCost()).orElse(0L);
            rsp.setDiff(rsp.getDiff() + diff);
            rsp.setAdditionalTax(rsp.getAdditionalTax() + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L));
            rsp.setStampTax(rsp.getStampTax() + Optional.ofNullable(detail.getStampTax()).orElse(0L));
            rsp.setProfitAdjust(rsp.getProfitAdjust() + Optional.ofNullable(detail.getProfitAdjust()).orElse(0L));
            rsp.setAssessmentProfit(rsp.getAssessmentProfit() + Optional.ofNullable(detail.getAssessmentProfit()).orElse(0L));
            rsp.setAssessmentProfitWithoutExpense(rsp.getAssessmentProfitWithoutExpense() + Optional.ofNullable(detail.getAssessmentProfitWithoutExpense()).orElse(0L));
            rsp.setAssessmentProfitOriginal(rsp.getAssessmentProfitOriginal() + Optional.ofNullable(detail.getAssessmentProfitOriginal()).orElse(0L));
            rsp.setAssessmentProfitWithoutExpenseOriginal(rsp.getAssessmentProfitWithoutExpenseOriginal() + Optional.ofNullable(detail.getAssessmentProfitWithoutExpenseOriginal()).orElse(0L));
        }
        return rsp;
    }

    public List<BudgetPlanProfitDetailFutureRSP> listDetailFutureRSP(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = budgetPlanProfitService.getById(budgetPlanProfitId);
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算主数据不存在");
        }
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = this.initDefaultQuery(budgetPlanProfitId);
        query.eq(BudgetPlanProfitDetail::getDataCategory, BudgetPlanDataCategoryEnum.FUTURE.name());
        query.orderByAsc(BudgetPlanProfitDetail::getBelongDeptId);
        query.orderByAsc(BudgetPlanProfitDetail::getFtpIndustryCategory);
        List<BudgetPlanProfitDetail> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        Map<Long, String> deptNameMap = sysUserService.listAllDept().stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName));
        List<BudgetPlanProfitDetailFutureRSP> result = new LinkedList<>();
        for (BudgetPlanProfitDetail detail : dbList) {
            BudgetPlanProfitDetailFutureRSP rsp = this.convertFromProfitDetail(detail);
            rsp.setBelongDeptName(deptNameMap.get(rsp.getBelongDeptId()));
            result.add(rsp);
        }
        // 添加公司合计行
        BudgetPlanProfitDetailFutureRSP sumRSP = this.convertFromProfitDetailList(dbList);
        sumRSP.setBelongDeptName("公司新增合计");
        result.add(sumRSP);
        // 按照FTP行业分类分组
        Map<String, List<BudgetPlanProfitDetail>> ftpIndustryCategoryMap = dbList.stream().filter(e -> StrUtil.isNotBlank(e.getFtpIndustryCategory())).collect(Collectors.groupingBy(BudgetPlanProfitDetail::getFtpIndustryCategory));
        for (BudgetFtpIndustryCategory ftpIndustryCategoryEnum : BudgetFtpIndustryCategory.values()) {
            List<BudgetPlanProfitDetail> list = ftpIndustryCategoryMap.get(ftpIndustryCategoryEnum.name());
            BudgetPlanProfitDetailFutureRSP ftpIndustryRSP = this.convertFromProfitDetailList(list);
            ftpIndustryRSP.setBelongDeptName(ftpIndustryCategoryEnum.getDisplay());
            result.add(ftpIndustryRSP);
        }
        return result;
    }

    public List<BudgetPlanProfitDetailHistoryRSP> listDetailHistoryRSP(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = budgetPlanProfitService.getById(budgetPlanProfitId);
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算主数据不存在");
        }
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = this.initDefaultQuery(budgetPlanProfitId);
        query.eq(BudgetPlanProfitDetail::getDataCategory, BudgetPlanDataCategoryEnum.HISTORY.name());
        List<BudgetPlanProfitDetail> dbList = this.list(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        List<BudgetPlanProfitDetailHistoryRSP> result = new LinkedList<>();
        // 按照部门分组
        Map<Long, List<BudgetPlanProfitDetail>> deptMap = dbList.stream().collect(Collectors.groupingBy(BudgetPlanProfitDetail::getBelongDeptId));
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptMap.keySet());
        for (Map.Entry<Long, List<BudgetPlanProfitDetail>> entry : deptMap.entrySet()) {
            List<BudgetPlanProfitDetail> deptDetailList = entry.getValue();
            BudgetPlanProfitDetailHistoryRSP rsp = new BudgetPlanProfitDetailHistoryRSP();
            rsp.setBelongDeptId(entry.getKey());
            rsp.setBelongDeptName(deptNameMap.get(entry.getKey()));
            for (BudgetPlanProfitDetail detail : deptDetailList) {
                // TODO 代码可整理复用
                rsp.setLastPeriodBalance(rsp.getLastPeriodBalance() + Optional.ofNullable(detail.getEndOfLastPeriodBalance()).orElse(0L));
                rsp.setThisPeriodBalance(rsp.getThisPeriodBalance() + Optional.ofNullable(detail.getEndOfThisPeriodBalance()).orElse(0L));
                rsp.setIncomeWithoutTax(rsp.getIncomeWithoutTax() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
                rsp.setCostWithoutTax(rsp.getCostWithoutTax() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
                rsp.setValueAddedTax(rsp.getValueAddedTax() + Optional.ofNullable(detail.getValueAddedTax()).orElse(0L));
                rsp.setTaxOther(rsp.getTaxOther() + Optional.ofNullable(detail.getStampTax()).orElse(0L) + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L));
                rsp.setRiskFund(rsp.getRiskFund() + Optional.ofNullable(detail.getRiskFundDiffIdeal()).orElse(0L));
                rsp.setProfit(rsp.getProfit() + Optional.ofNullable(detail.getAssessmentProfitIdeal()).orElse(0L));
                rsp.setAverageOccupyThisPeriod(rsp.getAverageOccupyThisPeriod() + Optional.ofNullable(detail.getFundOccupyAverage()).orElse(0L));
            }
            result.add(rsp);
        }
        // 增加合计
        BudgetPlanProfitDetailHistoryRSP sumRSP = new BudgetPlanProfitDetailHistoryRSP();
        sumRSP.setBelongDeptName("公司存量合计");
        sumRSP.setLastPeriodBalance(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getLastPeriodBalance).sum());
        sumRSP.setThisPeriodBalance(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getThisPeriodBalance).sum());
        sumRSP.setIncomeWithoutTax(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getIncomeWithoutTax).sum());
        sumRSP.setCostWithoutTax(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getCostWithoutTax).sum());
        sumRSP.setValueAddedTax(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getValueAddedTax).sum());
        sumRSP.setTaxOther(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getTaxOther).sum());
        sumRSP.setRiskFund(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getRiskFund).sum());
        sumRSP.setProfit(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getProfit).sum());
        sumRSP.setAverageOccupyThisPeriod(result.stream().mapToLong(BudgetPlanProfitDetailHistoryRSP::getAverageOccupyThisPeriod).sum());
        result.add(sumRSP);
        // 按照FTP行业分类分组
        Map<String, List<BudgetPlanProfitDetail>> ftpIndustryCategoryMap = dbList.stream().filter(e -> StrUtil.isNotBlank(e.getFtpIndustryCategory())).collect(Collectors.groupingBy(BudgetPlanProfitDetail::getFtpIndustryCategory));
        for (BudgetFtpIndustryCategory ftpIndustryCategoryEnum : BudgetFtpIndustryCategory.values()) {
            List<BudgetPlanProfitDetail> list = ftpIndustryCategoryMap.get(ftpIndustryCategoryEnum.name());
            BudgetPlanProfitDetailHistoryRSP rsp = new BudgetPlanProfitDetailHistoryRSP();
            rsp.setBelongDeptName(ftpIndustryCategoryEnum.getDisplay());
            if (CollectionUtil.isNotEmpty(list)) {
                for (BudgetPlanProfitDetail detail : list) {
                    // TODO 代码可整理复用
                    rsp.setLastPeriodBalance(rsp.getLastPeriodBalance() + Optional.ofNullable(detail.getEndOfLastPeriodBalance()).orElse(0L));
                    rsp.setThisPeriodBalance(rsp.getThisPeriodBalance() + Optional.ofNullable(detail.getEndOfThisPeriodBalance()).orElse(0L));
                    rsp.setIncomeWithoutTax(rsp.getIncomeWithoutTax() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
                    rsp.setCostWithoutTax(rsp.getCostWithoutTax() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
                    rsp.setValueAddedTax(rsp.getValueAddedTax() + Optional.ofNullable(detail.getValueAddedTax()).orElse(0L));
                    rsp.setTaxOther(rsp.getTaxOther() + Optional.ofNullable(detail.getStampTax()).orElse(0L) + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L));
                    rsp.setRiskFund(rsp.getRiskFund() + Optional.ofNullable(detail.getRiskFundDiffIdeal()).orElse(0L));
                    rsp.setProfit(rsp.getProfit() + Optional.ofNullable(detail.getAssessmentProfitIdeal()).orElse(0L));
                    rsp.setAverageOccupyThisPeriod(rsp.getAverageOccupyThisPeriod() + Optional.ofNullable(detail.getFundOccupyAverage()).orElse(0L));
                }
            }
            result.add(rsp);
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfitDetail::getBudgetPlanId, budgetPlanId);
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanProfitId(Long budgetPlanProfitId) {
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfitDetail::getBudgetPlanProfitId, budgetPlanProfitId);
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).remove(query);
    }

    public LambdaQueryWrapper<BudgetPlanProfitDetail> initDefaultQuery(Long budgetPlanProfitId) {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfitDetail::getBudgetPlanProfitId, budgetPlanProfitId);
        boolean isProjManager = sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name());
        boolean isBusinesshead = sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name());
        if (isProjManager && !isBusinesshead) {
            query.eq(BudgetPlanProfitDetail::getSponsorUserId, currentUserId);
            return query;
        }
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        if (Objects.isNull(canViewDeptIds)) {
            // 不限制
            return query;
        }
        if (canViewDeptIds.isEmpty()) {
            // 没有可看的部门，填一个不可能的部门id即可
            query.eq(BudgetPlanProfitDetail::getBelongDeptId, -100);
            return query;
        }
        // 可以看指定部门
        query.in(BudgetPlanProfitDetail::getBelongDeptId, canViewDeptIds);
        return query;
    }

    private BudgetPlanProfitDetailFutureRSP convertFromProfitDetail(BudgetPlanProfitDetail detail) {
        BudgetPlanProfitDetailFutureRSP rsp = new BudgetPlanProfitDetailFutureRSP();
        rsp.setId(detail.getId());
        rsp.setClientName(detail.getClientName());
        rsp.setBelongDeptId(detail.getBelongDeptId());
        rsp.setContractCode(detail.getContractCode());
        rsp.setFtpIndustryCategory(detail.getFtpIndustryCategory());
        rsp.setFtpIndustryCategoryDisplay(Optional.ofNullable(BudgetFtpIndustryCategory.getByName(detail.getFtpIndustryCategory())).map(BudgetFtpIndustryCategory::display).orElse(null));
        rsp.setRiskControlIndustryClassify(detail.getRiskControlIndustryClassify());
        rsp.setRiskControlIndustryClassifyDisplay(Optional.ofNullable(RiskControlIndustryClassify.findByName(detail.getRiskControlIndustryClassify())).map(RiskControlIndustryClassify::display).orElse(null));
        rsp.setLeaseType(detail.getLeaseType());
        rsp.setLeaseTypeDisplay(Optional.ofNullable(LeaseType.of(detail.getLeaseType())).map(LeaseType::display).orElse(null));
        rsp.setPlanPayDate(detail.getPayDate());
        rsp.setTermMonth(detail.getTermMonth());
        rsp.setRepayFrequency(detail.getRepayFrequency());
        rsp.setRepayFrequencyDisplay(Optional.ofNullable(RepayRateEnum.of(detail.getRepayFrequency())).map(RepayRateEnum::display).orElse(null));
        rsp.setPayAmount(detail.getActualPay());
        rsp.setDepositRate(detail.getDepositRate());
        rsp.setContractInterestRate(detail.getContractInterestRate());
        rsp.setIrr(detail.getIrr());
        rsp.setXirr(detail.getXirr());
        rsp.setConsultingFeeRateYear(detail.getConsultingFeeRateYear());
        rsp.setConsultingFeeRate(detail.getConsultingFeeRate());
        rsp.setFtp(detail.getFtp());
        rsp.setInterestIncome(detail.getInterestIncome());
        rsp.setInterestIncomeWithoutTax(detail.getInterestIncomeWithoutTax());
        rsp.setConsultingFeeIncome(detail.getConsultingFeeIncome());
        rsp.setConsultingFeeIncomeWithoutTax(detail.getConsultingFeeIncomeWithoutTax());
        rsp.setIncome(detail.getIncome());
        rsp.setIncomeWithoutTax(detail.getIncomeWithoutTax());
        rsp.setCostWithoutTax(detail.getCostWithoutTax());
        rsp.setValueAddedTax(detail.getValueAddedTax());
        rsp.setStampTax(Optional.ofNullable(detail.getStampTax()).orElse(0L));
        rsp.setAdditionalTax(Optional.ofNullable(detail.getAdditionalTax()).orElse(0L));
        rsp.setFundOccupyAverage(detail.getFundOccupyAverage());
        rsp.setRiskFundDiff(detail.getRiskFundDiff());
        rsp.setAssessmentProfit(detail.getAssessmentProfit());
        rsp.setEndOfThisPeriodBalance(detail.getEndOfThisPeriodBalance());
        return rsp;
    }

    private BudgetPlanProfitDetailFutureRSP convertFromProfitDetailList(List<BudgetPlanProfitDetail> detailList) {
        BudgetPlanProfitDetailFutureRSP sumRSP = new BudgetPlanProfitDetailFutureRSP();
        if (CollectionUtil.isNotEmpty(detailList)) {
            BigDecimal depositRateBD = BigDecimal.ZERO;
            BigDecimal contractInterestRateBD = BigDecimal.ZERO;
            BigDecimal irrBD = BigDecimal.ZERO;
            BigDecimal xirrBD = BigDecimal.ZERO;
            BigDecimal consultingFeeRateBD = BigDecimal.ZERO;
            BigDecimal consultingFeeRateYearBD = BigDecimal.ZERO;
            BigDecimal ftpBD = BigDecimal.ZERO;
            for (BudgetPlanProfitDetail detail : detailList) {
                sumRSP.setPayAmount(sumRSP.getPayAmount() + Optional.ofNullable(detail.getActualPay()).orElse(0L));
                sumRSP.setInterestIncome(sumRSP.getInterestIncome() + Optional.ofNullable(detail.getInterestIncome()).orElse(0L));
                sumRSP.setConsultingFeeIncome(sumRSP.getConsultingFeeIncome() + Optional.ofNullable(detail.getConsultingFeeIncome()).orElse(0L));
                sumRSP.setIncomeWithoutTax(sumRSP.getIncomeWithoutTax() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
                sumRSP.setCostWithoutTax(sumRSP.getCostWithoutTax() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
                sumRSP.setValueAddedTax(sumRSP.getValueAddedTax() + Optional.ofNullable(detail.getValueAddedTax()).orElse(0L));
                sumRSP.setStampTax(sumRSP.getStampTax() + Optional.ofNullable(detail.getStampTax()).orElse(0L));
                sumRSP.setAdditionalTax(sumRSP.getAdditionalTax() + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L));
                sumRSP.setFundOccupyAverage(sumRSP.getFundOccupyAverage() + Optional.ofNullable(detail.getFundOccupyAverage()).orElse(0L));
                sumRSP.setRiskFundDiff(sumRSP.getRiskFundDiff() + Optional.ofNullable(detail.getRiskFundDiff()).orElse(0L));
                sumRSP.setAssessmentProfit(sumRSP.getAssessmentProfit() + Optional.ofNullable(detail.getAssessmentProfit()).orElse(0L));
                sumRSP.setEndOfThisPeriodBalance(sumRSP.getEndOfThisPeriodBalance() + Optional.ofNullable(detail.getEndOfThisPeriodBalance()).orElse(0L));
                // 需要加权平均的先暂存
                depositRateBD = depositRateBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getDepositRate()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                contractInterestRateBD = contractInterestRateBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getContractInterestRate()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                irrBD = irrBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getIrr()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                xirrBD = xirrBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getXirr()).orElse(0.0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                consultingFeeRateBD = consultingFeeRateBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getConsultingFeeRate()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                consultingFeeRateYearBD = consultingFeeRateYearBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getConsultingFeeRateYear()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
                ftpBD = ftpBD.add(BigDecimal.valueOf(Optional.ofNullable(detail.getFtp()).orElse(0)).multiply(BigDecimal.valueOf(Optional.ofNullable(detail.getActualPay()).orElse(0L))));
            }
            // 计算加权平均值
            if (sumRSP.getPayAmount() != 0) {
                sumRSP.setDepositRate(Util.mithrasIntegerDecimalTwo(depositRateBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
                sumRSP.setContractInterestRate(Util.mithrasIntegerDecimalTwo(contractInterestRateBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
                sumRSP.setIrr(Util.mithrasIntegerDecimalTwo(irrBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
                sumRSP.setXirr(xirrBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 10, RoundingMode.HALF_UP).doubleValue());
                sumRSP.setConsultingFeeRate(Util.mithrasIntegerDecimalTwo(consultingFeeRateBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
                sumRSP.setConsultingFeeRateYear(Util.mithrasIntegerDecimalTwo(consultingFeeRateYearBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
                sumRSP.setFtp(Util.mithrasIntegerDecimalTwo(ftpBD.divide(BigDecimal.valueOf(sumRSP.getPayAmount()), 20, RoundingMode.HALF_UP).intValue()));
            }
        }
        return sumRSP;
    }

    public BudgetPlanProfitSummaryOtherRSP summaryOtherCalculate(int year, Map<Long, String> deptNameMap, Map<Long, List<BudgetPlanProfitDetail>> detpDetailMap, String metricName, ToLongFunction<BudgetPlanProfitDetail> mapper) {
        BudgetPlanProfitSummaryOtherRSP rsp = new BudgetPlanProfitSummaryOtherRSP();
        rsp.setYear(String.valueOf(year));
        rsp.setMetricName(metricName);
        List<Long> deptIds = new LinkedList<>(deptNameMap.keySet());
        deptIds.sort(Comparator.comparing(e -> e));
        List<BudgetPlanProfitSummaryOtherRSP.DeptData> deptDataList = new LinkedList<>();
        for (Long deptId : deptIds) {
            BudgetPlanProfitSummaryOtherRSP.DeptData deptData = new BudgetPlanProfitSummaryOtherRSP.DeptData();
            deptData.setBelongDeptId(deptId);
            deptData.setBelongDeptName(deptNameMap.get(deptId));
            List<BudgetPlanProfitDetail> list = detpDetailMap.get(deptId);
            if (CollectionUtil.isNotEmpty(list)) {
                // 按照FTP类型分类
                deptData.setPublicUtilities(list.stream().filter(e -> Objects.equals(e.getFtpIndustryCategory(), BudgetFtpIndustryCategory.FTP_PUBLIC_UTILITIES.name())).mapToLong(mapper).sum());
                deptData.setCivilConsumption(list.stream().filter(e -> Objects.equals(e.getFtpIndustryCategory(), BudgetFtpIndustryCategory.FTP_CIVIL_CONSUMPTION.name())).mapToLong(mapper).sum());
                deptData.setStateOwnedIndustry(list.stream().filter(e -> Objects.equals(e.getFtpIndustryCategory(), BudgetFtpIndustryCategory.FTP_STATE_OWNED_INDUSTRY.name())).mapToLong(mapper).sum());
                deptData.setOtherIndustry(list.stream().filter(e -> Objects.equals(e.getFtpIndustryCategory(), BudgetFtpIndustryCategory.FTP_OTHER_INDUSTRY.name())).mapToLong(mapper).sum());
                // 部门合计
                deptData.setSum(list.stream().mapToLong(mapper).sum());
            }
            deptDataList.add(deptData);
        }
        rsp.setDeptDataList(deptDataList);
        return rsp;
    }

    public BudgetPlanProfitSummaryOtherRSP summaryOtherIrrCalculate(int year, Map<Long, String> deptNameMap, Map<Long, List<BudgetPlanProfitDetail>> detpDetailMap, String metricName) {
        BudgetPlanProfitSummaryOtherRSP rsp = new BudgetPlanProfitSummaryOtherRSP();
        rsp.setYear(String.valueOf(year));
        rsp.setMetricName(metricName);
        List<Long> deptIds = new LinkedList<>(deptNameMap.keySet());
        deptIds.sort(Comparator.comparing(e -> e));
        List<BudgetPlanProfitSummaryOtherRSP.DeptData> deptDataList = new LinkedList<>();
        for (Long deptId : deptIds) {
            BudgetPlanProfitSummaryOtherRSP.DeptData deptData = new BudgetPlanProfitSummaryOtherRSP.DeptData();
            deptData.setBelongDeptId(deptId);
            deptData.setBelongDeptName(deptNameMap.get(deptId));
            List<BudgetPlanProfitDetail> list = detpDetailMap.get(deptId);
            if (CollectionUtil.isNotEmpty(list)) {
                // 按照FTP行业分类
                Map<String, List<BudgetPlanProfitDetail>> ftpMap = list.stream().collect(Collectors.groupingBy(BudgetPlanProfitDetail::getFtpIndustryCategory));
                for (BudgetFtpIndustryCategory ftpIndustryCategoryEnum : BudgetFtpIndustryCategory.values()) {
                    if (ftpIndustryCategoryEnum == BudgetFtpIndustryCategory.FTP_PUBLIC_UTILITIES) {
                        deptData.setPublicUtilities(Long.valueOf(this.calculateAverageIrr(ftpMap.get(ftpIndustryCategoryEnum.name()))));
                    }
                    if (ftpIndustryCategoryEnum == BudgetFtpIndustryCategory.FTP_CIVIL_CONSUMPTION) {
                        deptData.setCivilConsumption(Long.valueOf(this.calculateAverageIrr(ftpMap.get(ftpIndustryCategoryEnum.name()))));
                    }
                    if (ftpIndustryCategoryEnum == BudgetFtpIndustryCategory.FTP_STATE_OWNED_INDUSTRY) {
                        deptData.setStateOwnedIndustry(Long.valueOf(this.calculateAverageIrr(ftpMap.get(ftpIndustryCategoryEnum.name()))));
                    }
                    if (ftpIndustryCategoryEnum == BudgetFtpIndustryCategory.FTP_OTHER_INDUSTRY) {
                        deptData.setOtherIndustry(Long.valueOf(this.calculateAverageIrr(ftpMap.get(ftpIndustryCategoryEnum.name()))));
                    }
                }
                // 部门小计
                deptData.setSum(Long.valueOf(this.calculateAverageIrr(list)));
            }
            deptDataList.add(deptData);
        }
        rsp.setDeptDataList(deptDataList);
        return rsp;
    }

    private Integer calculateAverageIrr(List<BudgetPlanProfitDetail> detailList) {
        if (CollectionUtil.isEmpty(detailList)) {
            return 0;
        }
        BigDecimal temp = BigDecimal.ZERO;
        BigDecimal totalPay = BigDecimal.ZERO;
        for (BudgetPlanProfitDetail detail : detailList) {
            totalPay = totalPay.add(BigDecimal.valueOf(detail.getActualPay()));
            temp = temp.add(BigDecimal.valueOf(detail.getIrr()).multiply(BigDecimal.valueOf(detail.getActualPay())));
        }
        if (totalPay.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal b = temp.divide(totalPay, 20, RoundingMode.HALF_UP);
        return Util.mithrasIntegerDecimalTwo(b.intValue());
    }
}

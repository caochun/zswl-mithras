package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.BudgetExamineBenefitApplicationService;
import cn.zswltech.mithras.budget.application.FinanceRiskHelp;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitAddREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListRSP;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitModifyREQ;
import cn.zswltech.mithras.budget.enums.BudgetExamineBenefitEnum;
import cn.zswltech.mithras.budget.mapper.BudgetExamineBenefitMapper;
import cn.zswltech.mithras.payment.dto.ContractPayInfoDTO;
import cn.zswltech.mithras.budget.mapper.model.BudgetExamine;
import cn.zswltech.mithras.budget.mapper.model.BudgetExamineBenefit;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitDetailMapper;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.DeptNameResolver;
import cn.zswltech.mithras.foundation.port.OrgResolver;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 预算管理-预算考核-效益考核表
 * @date 2025-04-11
 */
@Slf4j
@Service
public class BudgetExamineBenefitService extends ServiceImpl<BudgetExamineBenefitMapper, BudgetExamineBenefit> implements BudgetExamineBenefitApplicationService {

    @Resource
    private BudgetExamineBenefitMapper budgetExamineBenefitMapper;
    @Resource
    private FinanceProjectProfitDetailMapper financeProjectProfitDetailMapper;
    @Resource
    private DeptNameResolver deptNameResolver;
    @Resource
    private OrgResolver orgResolver;
    @Resource
    private FinanceRiskHelp financeRiskHelp;
    @Resource
    private BudgetExamineService budgetExamineService;
    private static final Long COMPANY_NUMBER = 10000396L;

    @Transactional(rollbackFor = Throwable.class)
    public void add(BudgetExamineBenefitAddREQ req) {
        //清理旧数据
        this.remove(Wrappers.<BudgetExamineBenefit>lambdaQuery()
                .eq(BudgetExamineBenefit::getBudgetExamineId, req.getBudgetExamineId()));
        //苍穹科目余额表
        Map<Long, Map<String, BigDecimal>> cqDataMap = financeRiskHelp.getMonthDeptValues(req.getBudgetExamineYear(), req.getBudgetExamineMonth());
        if (CollectionUtil.isEmpty(cqDataMap)) {
            return;
        }
        // 业务部门和资金管理部直接使用，其余部门合并到公共利润中心
        List<OrgDO> allOrg = orgResolver.listAllDept();
        Map<Long, OrgDO> orgMap = allOrg.stream().collect(Collectors.toMap(OrgDO::getId, e -> e));
        Optional<OrgDO> optional = allOrg.stream().filter(e -> Objects.equals(e.getCode(), "GGLRZX")).findFirst();
        if (!optional.isPresent()) {
            throw new MithrasException("没有找到<公共利润中心>这个部门");
        }
        Map<String, BigDecimal> tempMap = new HashMap<>();
        Iterator<Map.Entry<Long, Map<String, BigDecimal>>> iterator = cqDataMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Map<String, BigDecimal>> entry = iterator.next();
            OrgDO orgDO = orgMap.get(entry.getKey());
            if (Objects.isNull(orgDO)) {
                log.error("预算管理-预算考核-创建-部门数据拆分合并-没有找到部门信息[{}]", entry.getKey());
                continue;
            }
            if (!Objects.equals(orgDO.getType(), OrgConstants.BUSINESS_DEPT) && !Objects.equals(orgDO.getCode(), "ZJGLB")) {
                for (Map.Entry<String, BigDecimal> innerEntry : entry.getValue().entrySet()) {
                    BigDecimal b = Optional.ofNullable(tempMap.get(innerEntry.getKey())).orElse(BigDecimal.ZERO);
                    tempMap.put(innerEntry.getKey(), b.add(Optional.ofNullable(innerEntry.getValue()).orElse(BigDecimal.ZERO)));
                }
                iterator.remove();
            }
        }
        cqDataMap.put(optional.get().getId(), tempMap);
        // 处理数据
        Map<Long, List<BudgetExamineBenefit>> todoMap = new HashMap<>();
        //取苍穹数据
        cqDataMap.forEach((deptId, riskNames) -> {
            List<BudgetExamineBenefit> benefitList = todoMap.get(deptId);
            if (CollectionUtil.isEmpty(benefitList)) {
                benefitList = initDeptData(deptId, req.getBudgetExamineId(), req.getBudgetExamineYear(), req.getBudgetExamineMonth());
                todoMap.put(deptId, benefitList);
            }
            Map<String, BudgetExamineBenefit> benefitMap = benefitList.stream().collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, e -> e));
            // 有公式的用公式计算
            for (BudgetExamineBenefitEnum budgetExamineBenefitEnum : BudgetExamineBenefitEnum.values()) {
                BudgetExamineBenefit budgetExamineBenefit = benefitMap.get(budgetExamineBenefitEnum.name());
                Long v = getRiskValue(budgetExamineBenefitEnum.getRiskName(), riskNames);
                budgetExamineBenefit.setFieldValue(v);
            }
            // 计算加工数据
            // 营业收入 = 融资租赁收入 +经营租赁收入
            BudgetExamineBenefit operating = benefitMap.get(BudgetExamineBenefitEnum.OPERATING_REVENUE.name());
            operating.setFieldValue(benefitMap.get(BudgetExamineBenefitEnum.FINANCING_LEASE_INCOME.name()).getFieldValue() + benefitMap.get(BudgetExamineBenefitEnum.OPERATING_LEASE_INCOME.name()).getFieldValue());
            // 差价 = 营业收入-营业成本
            BudgetExamineBenefit price = benefitMap.get(BudgetExamineBenefitEnum.PRICE_DIFFERENCE.name());
            price.setFieldValue(benefitMap.get(BudgetExamineBenefitEnum.OPERATING_REVENUE.name()).getFieldValue() - benefitMap.get(BudgetExamineBenefitEnum.OPERATING_COST.name()).getFieldValue());
            // 减：财务费用 = 其它（财务费用）+手续费+利息支出+利息收入，借方累计发生额
            BudgetExamineBenefit deduction = benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_FINANCIAL_EXPENSES.name());
            deduction.setFieldValue(benefitMap.get(BudgetExamineBenefitEnum.FINANCIAL_OTHER.name()).getFieldValue() + benefitMap.get(BudgetExamineBenefitEnum.SERVICE_FEE.name()).getFieldValue()
                    + benefitMap.get(BudgetExamineBenefitEnum.INTEREST_EXPENSE.name()).getFieldValue() + benefitMap.get(BudgetExamineBenefitEnum.INTEREST_INCOME.name()).getFieldValue());
            // 利润总额  = 差价-税金及附加-销售费用-管理费用-财务费用-资产减值损失+资产处置收益+其他收益+营业外收入-营业外支出
            BudgetExamineBenefit total = benefitMap.get(BudgetExamineBenefitEnum.TOTAL_PROFIT.name());
            total.setFieldValue(benefitMap.get(BudgetExamineBenefitEnum.PRICE_DIFFERENCE.name()).getFieldValue() - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_TAX_AND_SURCHARGE.name()).getFieldValue()
                    - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_SELLING_EXPENSES.name()).getFieldValue() - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_ADMINISTRATIVE_EXPENSES.name()).getFieldValue()
                    - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_FINANCIAL_EXPENSES.name()).getFieldValue() - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_ASSET_IMPAIRMENT_LOSS.name()).getFieldValue()
                    + benefitMap.get(BudgetExamineBenefitEnum.ADDITION_ASSET_DISPOSAL_INCOME.name()).getFieldValue() + benefitMap.get(BudgetExamineBenefitEnum.ADDITION_OTHER_INCOME.name()).getFieldValue()
                    + benefitMap.get(BudgetExamineBenefitEnum.ADDITION_NON_OPERATING_INCOME.name()).getFieldValue() - benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_NON_OPERATING_EXPENSE.name()).getFieldValue());
        });
        // 项目利润表
        List<FinanceProjectProfitDetail> projectProfitDetails = financeProjectProfitDetailMapper.selectList(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                .eq(FinanceProjectProfitDetail::getYear, req.getBudgetExamineYear())
                .eq(FinanceProjectProfitDetail::getMonth, req.getBudgetExamineMonth()));
        if (ObjectUtil.isNotEmpty(projectProfitDetails)) {
            // 按照部门分组
            Map<Long, List<FinanceProjectProfitDetail>> profitDetailMap = projectProfitDetails.stream().collect(Collectors.groupingBy(FinanceProjectProfitDetail::getAssessDeptId));
            profitDetailMap.forEach((deptId, profitDetailList) -> {
                List<BudgetExamineBenefit> benefitList = todoMap.get(deptId);
                if (CollectionUtil.isEmpty(benefitList)) {
                    benefitList = initDeptData(deptId, req.getBudgetExamineId(), req.getBudgetExamineYear(), req.getBudgetExamineMonth());
                    todoMap.put(deptId, benefitList);
                }
                Map<String, BudgetExamineBenefit> benefitMap = benefitList.stream().collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, e -> e));
                // 期（年）初风险金余额
                BudgetExamineBenefit riskBalanceBegin = benefitMap.get(BudgetExamineBenefitEnum.BEGINNING_BALANCE.name());
                riskBalanceBegin.setFieldValue(profitDetailList.stream().filter(e -> Objects.nonNull(e.getRiskBalanceBeginYear())).mapToLong(FinanceProjectProfitDetail::getRiskBalanceBeginYear).sum());
                // 期（年）末风险金余额
                BudgetExamineBenefit riskBalanceEnd = benefitMap.get(BudgetExamineBenefitEnum.ENDING_BALANCE.name());
                riskBalanceEnd.setFieldValue(profitDetailList.stream().filter(e -> Objects.nonNull(e.getTotalRiskThisYear())).mapToLong(FinanceProjectProfitDetail::getTotalRiskThisYear).sum());
                // 风险准备金 = 期（年）末风险金余额 - 期（年）初风险金余额
                BudgetExamineBenefit risk = benefitMap.get(BudgetExamineBenefitEnum.RISK_PROVISION.name());
                risk.setFieldValue(riskBalanceEnd.getFieldValue() - riskBalanceBegin.getFieldValue());
                // 资产减值损失 = 风险准备金
                BudgetExamineBenefit assetLoss = benefitMap.get(BudgetExamineBenefitEnum.DEDUCTION_ASSET_IMPAIRMENT_LOSS.name());
                assetLoss.setFieldValue(risk.getFieldValue());
                // 成本类：FTP成本 项目利润中的本年累计资金成本
                BudgetExamineBenefit costBudgetExamineBenefit = benefitMap.get(BudgetExamineBenefitEnum.COST_TYPE_FTP_COST.name());
                costBudgetExamineBenefit.setFieldValue(profitDetailList.stream().filter(e -> Objects.nonNull(e.getTotalCostThisYear())).mapToLong(FinanceProjectProfitDetail::getTotalCostThisYear).sum());
                // 税金类：补提城建教育 项目利润表中的附加税
                BudgetExamineBenefit taxBudgetExamineBenefit = benefitMap.get(BudgetExamineBenefitEnum.TAX_TYPE_SUPPLEMENTAL_URBAN_CONSTRUCTION_TAX.name());
                taxBudgetExamineBenefit.setFieldValue(profitDetailList.stream().filter(e -> Objects.nonNull(e.getTotalAdditionalTaxThisYear())).mapToLong(FinanceProjectProfitDetail::getTotalAdditionalTaxThisYear).sum());
            });
        }
        // 衍生数据
        LocalDate startDate = LocalDate.of(req.getBudgetExamineYear(), 1, 1);
        LocalDate thisMonthDate = LocalDate.of(req.getBudgetExamineYear(), req.getBudgetExamineMonth(), 1);
        LocalDate endDate = LocalDate.of(thisMonthDate.getYear(), thisMonthDate.getMonthValue(), thisMonthDate.lengthOfMonth());
        List<ContractPayInfoDTO> contractPayInfoList = SpringUtil.getBean(PaymentActualDetailMapper.class).listContractPayInfoBetween(startDate, endDate);
        List<ContractPayInfoDTO> collecContractPayInfoList = SpringUtil.getBean(PaymentActualDetailMapper.class).listContractPayInfoBeforeTargetDate(endDate);
        BigDecimal assessmentProfitSumBD = BigDecimal.ZERO;
        BigDecimal excludeGGLRZXIncomeTaxSumBD = BigDecimal.ZERO;
        for (Map.Entry<Long, List<BudgetExamineBenefit>> entry : todoMap.entrySet()) {
            Map<String, BudgetExamineBenefit> m = entry.getValue().stream().collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, e -> e));
            // 考核调整项 = 成本类：FTP成本+成本类：其它成本摊销+费用类：本年度应计未计提绩效工资+ 税金类：补提城建教育+税金类：补提印花+税金类：其他+资金收益+其他
            m.get(BudgetExamineBenefitEnum.ASSESSMENT_ADJUSTMENT_ITEM.name()).setFieldValue(
                    m.get(BudgetExamineBenefitEnum.COST_TYPE_FTP_COST.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.COST_TYPE_OTHER_COST_AMORTIZATION.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.EXPENSE_TYPE_UNACCRUED_PERFORMANCE_SALARY.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.EXPENSE_TYPE_OTHER_EXPENSE_AMORTIZATION.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.TAX_TYPE_SUPPLEMENTAL_URBAN_CONSTRUCTION_TAX.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.TAX_TYPE_OTHER.name()).getFieldValue()
                            + m.get(BudgetExamineBenefitEnum.GENERAL_OTHER.name()).getFieldValue()
            );
            // 考核利润总额 = 利润总额 +考核调整项
            long assessmentProfit = Optional.ofNullable(m.get(BudgetExamineBenefitEnum.TOTAL_PROFIT.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L) + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.ASSESSMENT_ADJUSTMENT_ITEM.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L);
            m.get(BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name()).setFieldValue(assessmentProfit);
            assessmentProfitSumBD = assessmentProfitSumBD.add(BigDecimal.valueOf(assessmentProfit));
            // 所得税 = 考核利润总额 * 25%
            BigDecimal incomeTaxBD = BigDecimal.valueOf(assessmentProfit).multiply(BigDecimal.valueOf(0.25));
            long incomeTax = BudgetFinancialUtil.mithrasLongDecimalTwo(incomeTaxBD.longValue());
            m.get(BudgetExamineBenefitEnum.INCOME_TAX.name()).setFieldValue(incomeTax < 0 ? 0 : incomeTax);
            if (!Objects.equals(optional.get().getId(), entry.getKey())) {
                excludeGGLRZXIncomeTaxSumBD = excludeGGLRZXIncomeTaxSumBD.add(BigDecimal.valueOf(m.get(BudgetExamineBenefitEnum.INCOME_TAX.name()).getFieldValue()));
            }
            // 考核净利润 = 考核利润总额 - 所得税
            m.get(BudgetExamineBenefitEnum.ASSESSMENT_NET_PROFIT.name()).setFieldValue(assessmentProfit - incomeTax);
            // 业务投放规模 = 付款核销金额 - 首期租金
            long payAmount = 0L;
            long firstRentAmount = 0L;
            for (ContractPayInfoDTO contractPayInfoDTO : contractPayInfoList) {
                if (Objects.equals(contractPayInfoDTO.getBelongDeptId(), entry.getKey())) {
                    payAmount += contractPayInfoDTO.getPayAmount();
                    firstRentAmount += contractPayInfoDTO.getFirstRentAmount();
                }
            }
            m.get(BudgetExamineBenefitEnum.BUSINESS_INVESTMENT_SCALE.name()).setFieldValue(payAmount - firstRentAmount);
            // 月末资产总额 = 月末剩余本金
            long remainingPrincipalThisMonth = 0L;
            for (ContractPayInfoDTO contractPayInfoDTO : collecContractPayInfoList) {
                if (Objects.equals(contractPayInfoDTO.getBelongDeptId(), entry.getKey())) {
                    remainingPrincipalThisMonth += contractPayInfoDTO.getPayAmount() - contractPayInfoDTO.getFirstRentAmount() - contractPayInfoDTO.getPrincipalAmount();
                }
            }
            m.get(BudgetExamineBenefitEnum.END_OF_MONTH_ASSET_TOTAL.name()).setFieldValue(remainingPrincipalThisMonth);
        }
        // 保存前的最后调整，公共利润中心的所得税需要倒减得到
        BigDecimal gglrzxIncomeTaxBD = assessmentProfitSumBD.multiply(BigDecimal.valueOf(0.25)).subtract(excludeGGLRZXIncomeTaxSumBD);
        todoMap.get(optional.get().getId()).stream().filter(e -> Objects.equals(e.getFieldName(), BudgetExamineBenefitEnum.INCOME_TAX.name())).findAny().ifPresent(budgetExamineBenefit -> budgetExamineBenefit.setFieldValue(BudgetFinancialUtil.mithrasLongDecimalTwo(gglrzxIncomeTaxBD.longValue())));
        // 保存
        List<BudgetExamineBenefit> toInsertList = new LinkedList<>();
        todoMap.forEach((deptId, benefits) -> toInsertList.addAll(benefits));
        if (CollectionUtil.isNotEmpty(toInsertList)) {
            SpringUtil.getBean(BudgetExamineBenefitService.class).saveBatch(toInsertList);
        }
        // 添加公司本年数据
        this.initCompanyData(req.getBudgetExamineId());
        // 倒减得到本月数据
        this.initMonthData(req.getBudgetExamineId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(List<BudgetExamineBenefitModifyREQ> req) {
        if (ObjectUtil.isEmpty(req)) {
            return;
        }
        List<BudgetExamineBenefit> info = BeanUtil.copyToList(req, BudgetExamineBenefit.class);
        this.updateBatchById(info, 100);
        // 更新加工数据
        BudgetExamineBenefit benefit = this.getById(req.get(0).getId());
        SpringUtil.getBean(BudgetExamineBenefitService.class).refreshProcessData(benefit.getBudgetExamineId());
        // 产品设计上只允许编辑本年累计值，本月值通过本年累计倒减得到
        SpringUtil.getBean(BudgetExamineBenefitService.class).recalculateThisMonthData(benefit.getBudgetExamineId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void refreshProcessData(Long budgetExamineId) {
        // 找到公共利润中心部门信息（后面要用到，先前置查询）
        Optional<OrgDO> optional = orgResolver.listAllDept().stream().filter(e -> Objects.equals(e.getCode(), "GGLRZX")).findAny();
        if (!optional.isPresent()) {
            throw new MithrasException("<没有找到公共利润中心>的部门信息，请联系系统管理员");
        }
        List<BudgetExamineBenefit> level2List = this.list(
                Wrappers.<BudgetExamineBenefit>lambdaQuery()
                        .ne(BudgetExamineBenefit::getBelongDeptId, COMPANY_NUMBER)
                        .eq(BudgetExamineBenefit::getBudgetExamineId, budgetExamineId)
                        .eq(BudgetExamineBenefit::getFieldLevel, 3)
        );
        List<BudgetExamineBenefit> toUpdateList = new LinkedList<>();
        // 按照部门分组
        Map<Long, List<BudgetExamineBenefit>> map = level2List.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
        for (Map.Entry<Long, List<BudgetExamineBenefit>> entry : map.entrySet()) {
            Map<String, BudgetExamineBenefit> m = entry.getValue().stream().collect(Collectors.toMap(BudgetExamineBenefit::getFieldName, e -> e));
            long origin = Optional.ofNullable(m.get(BudgetExamineBenefitEnum.ASSESSMENT_ADJUSTMENT_ITEM.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L);
            long current = Optional.ofNullable(m.get(BudgetExamineBenefitEnum.COST_TYPE_FTP_COST.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.COST_TYPE_OTHER_COST_AMORTIZATION.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.EXPENSE_TYPE_UNACCRUED_PERFORMANCE_SALARY.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.TAX_TYPE_SUPPLEMENTAL_URBAN_CONSTRUCTION_TAX.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.TAX_TYPE_OTHER.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.FUND_INCOME.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L)
                    + Optional.ofNullable(m.get(BudgetExamineBenefitEnum.GENERAL_OTHER.name())).map(BudgetExamineBenefit::getFieldValue).orElse(0L);
            if (origin == current) {
                continue;
            }
            // 更新考核调整项
            BudgetExamineBenefit assessmentAdjustmentItem = m.get(BudgetExamineBenefitEnum.ASSESSMENT_ADJUSTMENT_ITEM.name());
            assessmentAdjustmentItem.setFieldValue(current);
            toUpdateList.add(assessmentAdjustmentItem);
            // 更新考核利润总额
            BudgetExamineBenefit totalProfit = m.get(BudgetExamineBenefitEnum.TOTAL_PROFIT.name());
            BudgetExamineBenefit assessmentTotalProfit = m.get(BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name());
            assessmentTotalProfit.setFieldValue(totalProfit.getFieldValue() + current);
            toUpdateList.add(assessmentTotalProfit);
            // 更新所得税
            BigDecimal incomeTaxBD = BigDecimal.valueOf(assessmentTotalProfit.getFieldValue()).multiply(BigDecimal.valueOf(0.25));
            long tax = BudgetFinancialUtil.mithrasLongDecimalTwo(incomeTaxBD.longValue());
            BudgetExamineBenefit incomeTax = m.get(BudgetExamineBenefitEnum.INCOME_TAX.name());
            incomeTax.setFieldValue(tax < 0 ? 0 : tax);
            toUpdateList.add(incomeTax);
            // 更新净利润
            BudgetExamineBenefit netProfit = m.get(BudgetExamineBenefitEnum.ASSESSMENT_NET_PROFIT.name());
            netProfit.setFieldValue(assessmentTotalProfit.getFieldValue() - tax);
            toUpdateList.add(netProfit);
        }
        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            // 更新数据
            SpringUtil.getBean(BudgetExamineBenefitService.class).updateBatchById(toUpdateList);
            // 更新公共利润中心所得税（需要倒减）
            List<BudgetExamineBenefit> list = this.list(
                    Wrappers.<BudgetExamineBenefit>lambdaQuery()
                            .eq(BudgetExamineBenefit::getBudgetExamineId, budgetExamineId)
                            .in(BudgetExamineBenefit::getFieldName, ListUtil.of(BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name(), BudgetExamineBenefitEnum.INCOME_TAX.name()))
            );
            BigDecimal assessmentProfitSumBD = BigDecimal.ZERO;
            BigDecimal excludeGGLRZXIncomeTaxSumBD = BigDecimal.ZERO;
            BudgetExamineBenefit gglrzxIncomeTaxBenefit = null;
            for (BudgetExamineBenefit benefit : list) {
                if (Objects.equals(benefit.getFieldName(), BudgetExamineBenefitEnum.ASSESSMENT_TOTAL_PROFIT.name())) {
                    assessmentProfitSumBD = assessmentProfitSumBD.add(BigDecimal.valueOf(benefit.getFieldValue()));
                }
                if (Objects.equals(benefit.getFieldName(), BudgetExamineBenefitEnum.INCOME_TAX.name()) && !Objects.equals(optional.get().getId(), benefit.getBelongDeptId())) {
                    excludeGGLRZXIncomeTaxSumBD = excludeGGLRZXIncomeTaxSumBD.add(BigDecimal.valueOf(benefit.getFieldValue()));
                }
                if (Objects.equals(benefit.getFieldName(), BudgetExamineBenefitEnum.INCOME_TAX.name()) && Objects.equals(optional.get().getId(), benefit.getBelongDeptId())) {
                    gglrzxIncomeTaxBenefit = benefit;
                }
            }
            if (Objects.nonNull(gglrzxIncomeTaxBenefit)) {
                BigDecimal gglrzxIncomeTaxBD = assessmentProfitSumBD.multiply(BigDecimal.valueOf(0.25)).subtract(excludeGGLRZXIncomeTaxSumBD);
                gglrzxIncomeTaxBenefit.setFieldValue(BudgetFinancialUtil.mithrasLongDecimalTwo(gglrzxIncomeTaxBD.longValue()));
                SpringUtil.getBean(BudgetExamineBenefitService.class).updateById(gglrzxIncomeTaxBenefit);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recalculateThisMonthData(Long budgetExamineId) {
        BudgetExamine thisMonthExamine = budgetExamineService.getById(budgetExamineId);
        List<BudgetExamineBenefit> thisMonthCurrentList = this.listByYearMonthLevel(thisMonthExamine.getExamineYear(), thisMonthExamine.getExamineMonth(), 2);
        List<BudgetExamineBenefit> thisMonthYearSumList = this.listByYearMonthLevel(thisMonthExamine.getExamineYear(), thisMonthExamine.getExamineMonth(), 3);
        Map<String, BudgetExamineBenefit> thisMonthYearSumMap = thisMonthYearSumList.stream().collect(Collectors.toMap(e -> e.getBelongDeptId() + "-" + e.getFieldName(), e -> e));
        LocalDate thisMonthDate = LocalDate.of(thisMonthExamine.getExamineYear(), thisMonthExamine.getExamineMonth(), 1);
        LocalDate lastMonthDate = thisMonthDate.minusMonths(1);
        List<BudgetExamineBenefit> lastMonthYearSumList = this.listByYearMonthLevel(lastMonthDate.getYear(), lastMonthDate.getMonthValue(), 3);
        Map<String, BudgetExamineBenefit> lastMonthYearSumMap = lastMonthYearSumList.stream().collect(Collectors.toMap(e -> e.getBelongDeptId() + "-" + e.getFieldName(), e -> e));
        List<BudgetExamineBenefit> updateList = new LinkedList<>();
        for (BudgetExamineBenefit benefit : thisMonthCurrentList) {
            String key = benefit.getBelongDeptId() + "-" + benefit.getFieldName();
            BudgetExamineBenefit lastMonth = lastMonthYearSumMap.get(key);
            BudgetExamineBenefit thisMonth = thisMonthYearSumMap.get(key);
            long lastV = 0L;
            long thisV = 0L;
            if (Objects.nonNull(lastMonth) && Objects.nonNull(lastMonth.getFieldValue())) {
                lastV = lastMonth.getFieldValue();
            }
            if (Objects.nonNull(thisMonth) && Objects.nonNull(thisMonth.getFieldValue())) {
                thisV = thisMonth.getFieldValue();
            }
            long newValue = thisV - lastV;
            if (!Objects.equals(newValue, benefit.getFieldValue())) {
                BudgetExamineBenefit update = new BudgetExamineBenefit();
                update.setId(benefit.getId());
                update.setFieldValue(newValue);
                updateList.add(update);
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            SpringUtil.getBean(BudgetExamineBenefitService.class).updateBatchById(updateList);
        }
        // 更新公司数据
        SpringUtil.getBean(BudgetExamineBenefitService.class).recalculateCompanyData(budgetExamineId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recalculateCompanyData(Long budgetExamineId) {
        BudgetExamine budgetExamine = budgetExamineService.getById(budgetExamineId);
        List<BudgetExamineBenefit> list = this.listByYearMonthLevel(budgetExamine.getExamineYear(), budgetExamine.getExamineMonth(), null);
        Map<String, List<BudgetExamineBenefit>> monthFieldMap = new HashMap<>();
        Map<String, List<BudgetExamineBenefit>> yearFieldMap = new HashMap<>();
        List<BudgetExamineBenefit> companyBenefitList = new LinkedList<>();
        // 分组原始数据
        for (BudgetExamineBenefit benefit : list) {
            if (Objects.equals(benefit.getBelongDeptId(), COMPANY_NUMBER)) {
                companyBenefitList.add(benefit);
            } else {
                if (Objects.equals(benefit.getFieldLevel(), 2)) {
                    List<BudgetExamineBenefit> level2List = monthFieldMap.get(benefit.getFieldName());
                    if (Objects.isNull(level2List)) {
                        level2List = new LinkedList<>();
                        monthFieldMap.put(benefit.getFieldName(), level2List);
                    }
                    level2List.add(benefit);
                }
                if (Objects.equals(benefit.getFieldLevel(), 3)) {
                    List<BudgetExamineBenefit> level3List = yearFieldMap.get(benefit.getFieldName());
                    if (Objects.isNull(level3List)) {
                        level3List = new LinkedList<>();
                        yearFieldMap.put(benefit.getFieldName(), level3List);
                    }
                    level3List.add(benefit);
                }
            }
        }
        List<BudgetExamineBenefit> updateList = new LinkedList<>();
        // 遍历公司数据更新
        for (BudgetExamineBenefit benefit : companyBenefitList) {
            long v = 0;
            List<BudgetExamineBenefit> deptList = null;
            if (Objects.equals(benefit.getFieldLevel(), 2)) {
                deptList = monthFieldMap.get(benefit.getFieldName());
            }
            if (Objects.equals(benefit.getFieldLevel(), 3)) {
                deptList = yearFieldMap.get(benefit.getFieldName());
            }
            if (Objects.nonNull(deptList) && CollectionUtil.isNotEmpty(deptList)) {
                v = deptList.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
            }
            if (!Objects.equals(benefit.getFieldValue(), v)) {
                BudgetExamineBenefit update = new BudgetExamineBenefit();
                update.setId(benefit.getId());
                update.setFieldValue(v);
                updateList.add(update);
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            SpringUtil.getBean(BudgetExamineBenefitService.class).updateBatchById(updateList);
        }
    }

    public List<BudgetExamineBenefit> listByYearMonthLevel(Integer year, Integer month, Integer level) {
        LambdaQueryWrapper<BudgetExamineBenefit> query = Wrappers.lambdaQuery();
        query.eq(BudgetExamineBenefit::getBudgetExamineYear, year);
        query.eq(BudgetExamineBenefit::getBudgetExamineMonth, month);
        if (Objects.nonNull(level)) {
            query.eq(BudgetExamineBenefit::getFieldLevel, level);
        }
        return this.list(query);
    }

    public List<BudgetExamineBenefitListRSP> list(BudgetExamineBenefitListREQ req) {
        List<BudgetExamineBenefitListRSP> rsps = new ArrayList<>();
        List<BudgetExamineBenefit> budgetExamineBenefits = getSumBudgetExamineBenefit(req);
        if (ObjectUtil.isNotEmpty(budgetExamineBenefits)) {
            List<OrgDO> allDeptList = orgResolver.listAllDept();
            Map<Long, List<BudgetExamineBenefit>> deptId2Bean = budgetExamineBenefits.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
            // 处理公司层面的数据
            List<BudgetExamineBenefit> companyList = deptId2Bean.get(COMPANY_NUMBER);
            if (CollectionUtil.isNotEmpty(companyList)) {
                rsps.add(this.convertToBudgetExamineBenefitListRSP(COMPANY_NUMBER, "公司", -100, companyList));
            }
            // 各个部门的
            Map<Long, String> deptId2Name = deptNameResolver.deptId2Name(deptId2Bean.keySet());
            for (OrgDO org : allDeptList) {
                Long deptId = org.getId();
                List<BudgetExamineBenefit> dataList = deptId2Bean.get(deptId);
                if (CollectionUtil.isEmpty(dataList)) {
                    continue;
                }
                rsps.add(this.convertToBudgetExamineBenefitListRSP(deptId, deptId2Name.get(deptId), org.getType(), dataList));
            }
        }
        return sort(rsps);
    }

    private BudgetExamineBenefitListRSP convertToBudgetExamineBenefitListRSP(Long deptId, String deptName, int sort, List<BudgetExamineBenefit> dataList) {
        BudgetExamineBenefitListRSP rsp = new BudgetExamineBenefitListRSP();
        rsp.setBelongDeptId(deptId);
        rsp.setBelongDeptName(deptName);
        rsp.setSort(sort);
        List<BudgetExamineBenefit> monthData = new LinkedList<>();
        List<BudgetExamineBenefit> yearData = new LinkedList<>();
        for (BudgetExamineBenefit benefit : dataList) {
            // 预处理
            if (StrUtil.equalsAny(benefit.getFieldName(), BudgetExamineBenefitEnum.BUSINESS_INVESTMENT_SCALE.name(), BudgetExamineBenefitEnum.END_OF_MONTH_ASSET_TOTAL.name())) {
                // 前端展示为万元，需要扩大缩小10000倍后给前端（前端默认会再除以10000）
                BigDecimal b = BigDecimal.valueOf(benefit.getFieldValue()).divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP);
                benefit.setFieldValue(BudgetFinancialUtil.mithrasLongDecimalTwo(b.longValue()));
            }
            // 分组
            if (ObjectUtil.equals(benefit.getFieldLevel(), 2)) {
                monthData.add(benefit);
            }
            if (ObjectUtil.equals(benefit.getFieldLevel(), 3)) {
                yearData.add(benefit);
            }
        }
        if (ObjectUtil.isNotEmpty(monthData)) {
            rsp.setCurrentMonthList(BeanUtil.copyToList(monthData, BudgetExamineBenefitListRSP.BudgetExamineBenefitBody.class));
        }
        if (ObjectUtil.isNotEmpty(yearData)) {
            rsp.setCurrentYearList(BeanUtil.copyToList(yearData, BudgetExamineBenefitListRSP.BudgetExamineBenefitBody.class));
        }
        return rsp;
    }

    private List<BudgetExamineBenefitListRSP> sort(List<BudgetExamineBenefitListRSP> rsps) {
        if (ObjectUtil.isEmpty(rsps)) {
            return rsps;
        }
        BudgetExamineBenefitEnum[] allBudgetExamineEnum = BudgetExamineBenefitEnum.values();
        List<BudgetExamineBenefitEnum> budgetExamineBenefitColumn = Arrays.stream(allBudgetExamineEnum).filter(e -> e.getSort() > 0).sorted(Comparator.comparing(BudgetExamineBenefitEnum::getSort)).collect(Collectors.toList());
        rsps.forEach(rsp -> {
            rsp.setCurrentMonthList(sortBody(rsp.getCurrentMonthList(), budgetExamineBenefitColumn));
            rsp.setCurrentYearList(sortBody(rsp.getCurrentYearList(), budgetExamineBenefitColumn));
        });
        rsps.sort(Comparator.comparing(BudgetExamineBenefitListRSP::getSort));
        return rsps;
    }

    private List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> sortBody( List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentMonthList,  List<BudgetExamineBenefitEnum> budgetExamineBenefitColumn) {
        if (ObjectUtil.isEmpty(currentMonthList)) {
            return currentMonthList;
        }
        List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> orderList = new ArrayList<>();
        Map<String, BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> name2Map = currentMonthList.stream().collect(Collectors.toMap(BudgetExamineBenefitListRSP.BudgetExamineBenefitBody::getFieldName, e -> e, (a, b) -> a));
        budgetExamineBenefitColumn.forEach(e -> {
            orderList.add(name2Map.get(e.name()));
        });
        return orderList;
    }



    private List<BudgetExamineBenefit> examineBenefitBody2Bean(List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentList, Integer fieldLevel, BudgetExamineBenefitListRSP examineBenefitListRSP) {
        List<BudgetExamineBenefit> budgetExamineBenefits = BeanUtil.copyToList(currentList, BudgetExamineBenefit.class);
        if (ObjectUtil.isNotEmpty(budgetExamineBenefits)) {
            budgetExamineBenefits.forEach(e -> {
                e.setId(null);
                e.setFieldLevel(fieldLevel);
                e.setBudgetExamineYear(examineBenefitListRSP.getBudgetExamineYear());
                e.setBudgetExamineMonth(examineBenefitListRSP.getBudgetExamineMonth());
                e.setBudgetExamineId(examineBenefitListRSP.getBudgetExamineId());
                e.setBelongDeptId(examineBenefitListRSP.getBelongDeptId());
            });
        }
        return budgetExamineBenefits;
    }

    //2 本月 3，本年
    public List<BudgetExamineBenefit> getSumBudgetExamineBenefit (BudgetExamineBenefitListREQ req) {
        List<Integer> fieldLevels = new ArrayList<>();
        fieldLevels.add(2);
        fieldLevels.add(3);
        return budgetExamineBenefitMapper.selectList(Wrappers.<BudgetExamineBenefit>lambdaQuery()
                .eq(BudgetExamineBenefit::getBudgetExamineId, req.getBudgetExamineId())
                .in(BudgetExamineBenefit::getFieldLevel, fieldLevels));
    }

//    public List<BudgetExamineBenefitListRSP> calculation(BudgetExamineBenefitListREQ req) {
//        List<Integer> fieldLevels = new ArrayList<>();
//        fieldLevels.add(2);
//        fieldLevels.add(3);
//        //查询所有数据
//        List<BudgetExamineBenefit> budgetExamineBenefits = budgetExamineBenefitMapper.selectList(Wrappers.<BudgetExamineBenefit>lambdaQuery()
//                .eq(BudgetExamineBenefit::getBudgetExamineId, req.getBudgetExamineId())
//                .notIn(BudgetExamineBenefit::getFieldLevel, fieldLevels));
//        if (ObjectUtil.isEmpty(budgetExamineBenefits)) {
//            return null;
//        }
//        BudgetExamine budgetExamine = budgetExamineService.getById(req.getBudgetExamineId());
//        if (ObjectUtil.isEmpty(budgetExamine)) {
//            return null;
//        }
//        String approvalStatus = budgetExamine.getApprovalStatus();
//        //查询本年数据
//        List<BudgetExamineBenefit> budgetExamineBenefitsYear = budgetExamineBenefitMapper.selectList(Wrappers.<BudgetExamineBenefit>lambdaQuery()
//                .eq(BudgetExamineBenefit::getBudgetExamineYear, budgetExamineBenefits.get(0).getBudgetExamineYear()));
//        List<BudgetExamineBenefitListRSP> rsps = new ArrayList<>();
//        //区分个部门
//        Map<Long, List<BudgetExamineBenefit>> deptId2BudgetExamineBenefit = budgetExamineBenefits.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
//        Map<Long, List<BudgetExamineBenefit>> deptId2BudgetExamineBenefitYear = budgetExamineBenefitsYear.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
//
//        Map<Long, String> deptId2Name = deptNameResolver.deptId2Name(deptId2BudgetExamineBenefit.keySet());
//        //所有字段
//        BudgetExamineBenefitEnum[] allBudgetExamineEnum = BudgetExamineBenefitEnum.values();
//        List<BudgetExamineBenefitEnum> budgetExamineBenefitColumn = Arrays.stream(allBudgetExamineEnum).filter(e -> e.getSort() > 0).sorted(Comparator.comparing(BudgetExamineBenefitEnum::getSort)).collect(Collectors.toList());
//        deptId2BudgetExamineBenefit.forEach((deptId, budgetExamineBenefit) -> {
//            //每个部门都有全量都数据
//            BudgetExamineBenefitListRSP rsp = new BudgetExamineBenefitListRSP();
//            rsp.setBelongDeptId(deptId);
//            rsp.setBudgetExamineId(budgetExamine.getId());
//            rsp.setBudgetExamineYear(budgetExamine.getExamineYear());
//            rsp.setBudgetExamineMonth(budgetExamine.getExamineMonth());
//            rsp.setBelongDeptName(deptId2Name.get(deptId));
//            List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentMonthList = new ArrayList<>();
//            List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentYearList = new ArrayList<>();
//            Map<String, List<BudgetExamineBenefit>> benefitName2List = budgetExamineBenefit.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
//            Map<String, List<BudgetExamineBenefit>> benefitName2ListYear = deptId2BudgetExamineBenefitYear.get(deptId).stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
//            budgetExamineBenefitColumn.forEach(column -> {
//                //本月数
//                List<BudgetExamineBenefit> budgetExamineBenefitsBody = benefitName2List.get(column.name());
//                BudgetExamineBenefitListRSP.BudgetExamineBenefitBody body = new BudgetExamineBenefitListRSP.BudgetExamineBenefitBody();
//                body.setFieldName(column.name());
//                if (ObjectUtil.isNotEmpty(budgetExamineBenefitsBody)) {
//                    body.setFieldLevel(budgetExamineBenefitsBody.get(0).getFieldLevel());
//                    body.setFieldValue(budgetExamineBenefitsBody.stream().map(BudgetExamineBenefit::getFieldValue).reduce(Long::sum).orElse(0L));
//                }
//                currentMonthList.add(body);
//                //本年数
//                List<BudgetExamineBenefit> budgetExamineBenefitsBodyYear = benefitName2ListYear.get(column.name());
//                BudgetExamineBenefitListRSP.BudgetExamineBenefitBody bodyYear = new BudgetExamineBenefitListRSP.BudgetExamineBenefitBody();
//                bodyYear.setFieldName(column.name());
//                if (ObjectUtil.isNotEmpty(budgetExamineBenefitsBodyYear)) {
//                    bodyYear.setFieldLevel(budgetExamineBenefitsBodyYear.get(0).getFieldLevel());
//                    if (ObjectUtil.equals(column, BudgetExamineBenefitEnum.BEGINNING_BALANCE)) {
//                        budgetExamineBenefitsBodyYear = benefitName2ListYear.get(BudgetExamineBenefitEnum.BEGINNING_BALANCE_YEAR_TOTAL.name());
//                        if (ObjectUtil.isNotEmpty(budgetExamineBenefitsBodyYear)) {
//                            bodyYear.setFieldValue(budgetExamineBenefitsBodyYear.get(0).getFieldValue());
//                        }
//                    } else {
//                        bodyYear.setFieldValue(budgetExamineBenefitsBodyYear.stream().map(BudgetExamineBenefit::getFieldValue).filter(ObjectUtil::isNotNull).reduce(Long::sum).orElse(0L));
//                    }
//                }
//                currentYearList.add(bodyYear);
//            });
//            rsp.setCurrentMonthList(currentMonthList);
//            rsp.setCurrentYearList(currentYearList);
//            rsps.add(rsp);
//        });
//        //添加公司数
//        if (ObjectUtil.isNotEmpty(rsps)) {
//            //每个部门都有全量都数据
//            BudgetExamineBenefitListRSP rsp = new BudgetExamineBenefitListRSP();
//            rsp.setBelongDeptId(COMPANY_NUMBER);
//            rsp.setBudgetExamineId(budgetExamine.getId());
//            rsp.setBudgetExamineYear(budgetExamine.getExamineYear());
//            rsp.setBudgetExamineMonth(budgetExamine.getExamineMonth());
//            rsp.setBelongDeptName(BelongTypeEnum.COMPANY.getDisplay());
//            Map<String, BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> monthMap = new HashMap<>();
//            Map<String, BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> yearMap = new HashMap<>();
//
//            rsps.forEach(e -> {
//                List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentMonthList = e.getCurrentMonthList();
//                if (ObjectUtil.isNotEmpty(currentMonthList)) {
//                    currentMonthList.forEach(month -> {
//                        BudgetExamineBenefitListRSP.BudgetExamineBenefitBody body = monthMap.getOrDefault(month.getFieldName(), new BudgetExamineBenefitListRSP.BudgetExamineBenefitBody());
//                        body.setFieldName(month.getFieldName());
//                        body.setFieldValue(LongUtil.null2zero(body.getFieldValue()) + LongUtil.null2zero(month.getFieldValue()));
//                        monthMap.put(month.getFieldName(), body);
//                    });
//                }
//                List<BudgetExamineBenefitListRSP.BudgetExamineBenefitBody> currentYearList = e.getCurrentYearList();
//                if (ObjectUtil.isNotEmpty(currentYearList)) {
//                    currentYearList.forEach(year -> {
//                        BudgetExamineBenefitListRSP.BudgetExamineBenefitBody body = yearMap.getOrDefault(year.getFieldName(), new BudgetExamineBenefitListRSP.BudgetExamineBenefitBody());
//                        body.setFieldName(year.getFieldName());
//                        body.setFieldValue(LongUtil.null2zero(body.getFieldValue()) + LongUtil.null2zero(year.getFieldValue()));
//                        yearMap.put(year.getFieldName(), body);
//                    });
//                }
//            });
//            rsp.setCurrentMonthList(new ArrayList<>(monthMap.values()));
//            rsp.setCurrentYearList(new ArrayList<>(yearMap.values()));
//            rsps.add(0, rsp);
//        }
//        return rsps;
//    }

    @Transactional(rollbackFor = Throwable.class)
    public void initCompanyData(Long budgetExamineId) {
        BudgetExamine budgetExamine = budgetExamineService.getById(budgetExamineId);
        if (Objects.isNull(budgetExamine)) {
            throw new MithrasException("预算考核主表数据不存在");
        }
        // 待保存数据
        List<BudgetExamineBenefit> insertList = new LinkedList<>();
        // 按月查询
        LambdaQueryWrapper<BudgetExamineBenefit> query = Wrappers.lambdaQuery();
        query.eq(BudgetExamineBenefit::getBudgetExamineYear, budgetExamine.getExamineYear());
        query.eq(BudgetExamineBenefit::getBudgetExamineMonth, budgetExamine.getExamineMonth());
        query.ne(BudgetExamineBenefit::getBelongDeptId, COMPANY_NUMBER);
        query.eq(BudgetExamineBenefit::getFieldLevel, 3);
        List<BudgetExamineBenefit> list = this.list(query);
        Map<String, List<BudgetExamineBenefit>> map = list.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
        // 添加公司维度数据
        for (BudgetExamineBenefitEnum item : BudgetExamineBenefitEnum.values()) {
            List<BudgetExamineBenefit> itemList = map.get(item.name());
            long v = 0L;
            if (CollectionUtil.isNotEmpty(itemList)) {
                v = itemList.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
            }
            BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
            budgetExamineBenefit.setBudgetExamineId(budgetExamine.getId());
            budgetExamineBenefit.setBudgetExamineYear(budgetExamine.getExamineYear());
            budgetExamineBenefit.setBudgetExamineMonth(budgetExamine.getExamineMonth());
            budgetExamineBenefit.setBelongDeptId(COMPANY_NUMBER);
            budgetExamineBenefit.setFieldName(item.name());
            budgetExamineBenefit.setFieldValue(v);
            budgetExamineBenefit.setFieldLevel(3);
            insertList.add(budgetExamineBenefit);
        }
        // 保存
        if (CollectionUtil.isNotEmpty(insertList)) {
            SpringUtil.getBean(BudgetExamineBenefitService.class).saveBatch(insertList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void initMonthData(Long budgetExamineId) {
        BudgetExamine budgetExamine = budgetExamineService.getById(budgetExamineId);
        if (Objects.isNull(budgetExamine)) {
            throw new MithrasException("预算考核主表数据不存在");
        }
        List<BudgetExamineBenefit> insertList = new LinkedList<>();
        // 查本月数据
        LambdaQueryWrapper<BudgetExamineBenefit> thisMonthQuery = Wrappers.lambdaQuery();
        thisMonthQuery.eq(BudgetExamineBenefit::getBudgetExamineYear, budgetExamine.getExamineYear());
        thisMonthQuery.eq(BudgetExamineBenefit::getBudgetExamineMonth, budgetExamine.getExamineMonth());
        thisMonthQuery.eq(BudgetExamineBenefit::getFieldLevel, 3);
        List<BudgetExamineBenefit> thisMonthList = this.list(thisMonthQuery);
        Map<Long, List<BudgetExamineBenefit>> thisMonthDeptMap = thisMonthList.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
        // 查上月数据
        Map<String, Long> lastMonthDeptFieldMap = new HashMap<>();
        // 1月份的本年累计和本月一致，无需倒减
        if (budgetExamine.getExamineMonth() != 1) {
            LocalDate thisMonthDate = LocalDate.of(budgetExamine.getExamineYear(), budgetExamine.getExamineMonth(), 1);
            LocalDate lastMonthDate = thisMonthDate.minusMonths(1);
            LambdaQueryWrapper<BudgetExamineBenefit> lastMonthQuery = Wrappers.lambdaQuery();
            lastMonthQuery.eq(BudgetExamineBenefit::getBudgetExamineYear, lastMonthDate.getYear());
            lastMonthQuery.eq(BudgetExamineBenefit::getBudgetExamineMonth, lastMonthDate.getMonth());
            lastMonthQuery.eq(BudgetExamineBenefit::getFieldLevel, 3);
            List<BudgetExamineBenefit> lastMonthList = this.list(lastMonthQuery);
            if (CollectionUtil.isEmpty(lastMonthList)) {
                throw new MithrasException("没有找到上月数据，无法计算");
            }
            for (BudgetExamineBenefit benefit : lastMonthList) {
                String key = benefit.getBelongDeptId() + "-" + benefit.getFieldName();
                lastMonthDeptFieldMap.put(key, Optional.ofNullable(benefit.getFieldValue()).orElse(0L));
            }
        }

        for (Map.Entry<Long, List<BudgetExamineBenefit>> entry : thisMonthDeptMap.entrySet()) {
            // 按照款项类型分组
            Map<String, List<BudgetExamineBenefit>> m = entry.getValue().stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
            // 添加本年合计
            for (BudgetExamineBenefitEnum item : BudgetExamineBenefitEnum.values()) {
                List<BudgetExamineBenefit> list = m.get(item.name());
                long v = 0L;
                if (CollectionUtil.isNotEmpty(list)) {
                    v = list.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
                }
                long lastV = 0L;
                if (item != BudgetExamineBenefitEnum.END_OF_MONTH_ASSET_TOTAL && item != BudgetExamineBenefitEnum.ENDING_BALANCE) {
                    lastV = Optional.ofNullable(lastMonthDeptFieldMap.get(entry.getKey() + "-" + item.name())).orElse(0L);
                }
                BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
                budgetExamineBenefit.setBudgetExamineId(budgetExamine.getId());
                budgetExamineBenefit.setBudgetExamineYear(budgetExamine.getExamineYear());
                budgetExamineBenefit.setBudgetExamineMonth(budgetExamine.getExamineMonth());
                budgetExamineBenefit.setBelongDeptId(entry.getKey());
                budgetExamineBenefit.setFieldName(item.name());
                // 风险准备金期初余额取上一个月的期末余额
                if (item == BudgetExamineBenefitEnum.BEGINNING_BALANCE) {
                    budgetExamineBenefit.setFieldValue(lastV);
                } else {
                    budgetExamineBenefit.setFieldValue(v - lastV);
                }
                budgetExamineBenefit.setFieldLevel(2);
                insertList.add(budgetExamineBenefit);
            }
        }
        if (CollectionUtil.isNotEmpty(insertList)) {
            SpringUtil.getBean(BudgetExamineBenefitService.class).saveBatch(insertList);
        }
    }

    private List<BudgetExamineBenefit> initDeptData(Long deptId, Long budgetExamineId, Integer year, Integer month) {
        List<BudgetExamineBenefit> result = new LinkedList<>();
        for (BudgetExamineBenefitEnum budgetExamineBenefitEnum : BudgetExamineBenefitEnum.values()) {
            BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
            budgetExamineBenefit.setBelongDeptId(deptId);
            budgetExamineBenefit.setBudgetExamineId(budgetExamineId);
            budgetExamineBenefit.setBudgetExamineYear(year);
            budgetExamineBenefit.setBudgetExamineMonth(month);
            budgetExamineBenefit.setFieldName(budgetExamineBenefitEnum.name());
            budgetExamineBenefit.setFieldValue(0L);
            budgetExamineBenefit.setFieldLevel(3);
            result.add(budgetExamineBenefit);
        }
        return result;
    }

    private Long getRiskValue(String riskCode, Map<String, BigDecimal> riskMap) {
        return LongUtil.other2Long(financeRiskHelp.getRiskValue(riskMap, riskCode).toString());
    }

//    @Transactional(rollbackFor = Throwable.class)
//    public void calculation(Long budgetExamineId) {
//        BudgetExamine budgetExamine = budgetExamineService.getById(budgetExamineId);
//        if (Objects.isNull(budgetExamine)) {
//            throw new MithrasException("预算考核主表数据不存在");
//        }
//        List<BudgetExamineBenefit> monthInsertList = new LinkedList<>();
//        // TODO 代码可整理复用
//        // 按月查询
//        LambdaQueryWrapper<BudgetExamineBenefit> monthQuery = Wrappers.lambdaQuery();
//        monthQuery.eq(BudgetExamineBenefit::getBudgetExamineYear, budgetExamine.getExamineYear());
//        monthQuery.eq(BudgetExamineBenefit::getBudgetExamineMonth, budgetExamine.getExamineMonth());
//        monthQuery.ne(BudgetExamineBenefit::getBelongDeptId, COMPANY_NUMBER);
//        monthQuery.eq(BudgetExamineBenefit::getFieldLevel, 2);
//        List<BudgetExamineBenefit> monthList = this.list(monthQuery);
//        Map<String, List<BudgetExamineBenefit>> monthMap = monthList.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
//        // 添加公司维度数据
//        for (BudgetExamineBenefitEnum item : BudgetExamineBenefitEnum.values()) {
//            List<BudgetExamineBenefit> list = monthMap.get(item.name());
//            long v = 0L;
//            if (CollectionUtil.isNotEmpty(list)) {
//                v = list.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
//            }
//            BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
//            budgetExamineBenefit.setBudgetExamineId(budgetExamine.getId());
//            budgetExamineBenefit.setBudgetExamineYear(budgetExamine.getExamineYear());
//            budgetExamineBenefit.setBudgetExamineMonth(budgetExamine.getExamineMonth());
//            budgetExamineBenefit.setBelongDeptId(COMPANY_NUMBER);
//            budgetExamineBenefit.setFieldName(item.name());
//            budgetExamineBenefit.setFieldValue(v);
//            budgetExamineBenefit.setFieldLevel(2);
//            monthInsertList.add(budgetExamineBenefit);
//        }
//        if (CollectionUtil.isNotEmpty(monthInsertList)) {
//            this.saveBatch(monthInsertList);
//        }
//        List<BudgetExamineBenefit> yearInsertList = new LinkedList<>();
//        // 按年查询
//        LambdaQueryWrapper<BudgetExamineBenefit> yearQuery = Wrappers.lambdaQuery();
//        yearQuery.eq(BudgetExamineBenefit::getBudgetExamineYear, budgetExamine.getExamineYear());
//        yearQuery.le(BudgetExamineBenefit::getBudgetExamineMonth, budgetExamine.getExamineMonth());
//        yearQuery.ne(BudgetExamineBenefit::getBelongDeptId, COMPANY_NUMBER);
//        yearQuery.eq(BudgetExamineBenefit::getFieldLevel, 2);
//        List<BudgetExamineBenefit> yearList = this.list(yearQuery);
//        Map<Long, List<BudgetExamineBenefit>> yearDeptMap = yearList.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getBelongDeptId));
//        for (Map.Entry<Long, List<BudgetExamineBenefit>> entry : yearDeptMap.entrySet()) {
//            // 按照款项类型分组
//            Map<String, List<BudgetExamineBenefit>> m = entry.getValue().stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
//            // 添加部门本年合计
//            for (BudgetExamineBenefitEnum item : BudgetExamineBenefitEnum.values()) {
//                List<BudgetExamineBenefit> list = m.get(item.name());
//                long v = 0L;
//                if (CollectionUtil.isNotEmpty(list)) {
//                    v = list.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
//                }
//                BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
//                budgetExamineBenefit.setBudgetExamineId(budgetExamine.getId());
//                budgetExamineBenefit.setBudgetExamineYear(budgetExamine.getExamineYear());
//                budgetExamineBenefit.setBudgetExamineMonth(budgetExamine.getExamineMonth());
//                budgetExamineBenefit.setBelongDeptId(entry.getKey());
//                budgetExamineBenefit.setFieldName(item.name());
//                budgetExamineBenefit.setFieldValue(v);
//                budgetExamineBenefit.setFieldLevel(3);
//                yearInsertList.add(budgetExamineBenefit);
//            }
//        }
//        // 公司维度本年合计
//        Map<String, List<BudgetExamineBenefit>> yearMap = yearInsertList.stream().collect(Collectors.groupingBy(BudgetExamineBenefit::getFieldName));
//        for (BudgetExamineBenefitEnum item : BudgetExamineBenefitEnum.values()) {
//            List<BudgetExamineBenefit> list = yearMap.get(item.name());
//            long v = 0L;
//            if (CollectionUtil.isNotEmpty(list)) {
//                v = list.stream().filter(e -> Objects.nonNull(e.getFieldValue())).mapToLong(BudgetExamineBenefit::getFieldValue).sum();
//            }
//            BudgetExamineBenefit budgetExamineBenefit = new BudgetExamineBenefit();
//            budgetExamineBenefit.setBudgetExamineId(budgetExamine.getId());
//            budgetExamineBenefit.setBudgetExamineYear(budgetExamine.getExamineYear());
//            budgetExamineBenefit.setBudgetExamineMonth(budgetExamine.getExamineMonth());
//            budgetExamineBenefit.setBelongDeptId(COMPANY_NUMBER);
//            budgetExamineBenefit.setFieldName(item.name());
//            budgetExamineBenefit.setFieldValue(v);
//            budgetExamineBenefit.setFieldLevel(3);
//            yearInsertList.add(budgetExamineBenefit);
//        }
//        if (CollectionUtil.isNotEmpty(yearInsertList)) {
//            this.saveBatch(yearInsertList);
//        }
//    }
}

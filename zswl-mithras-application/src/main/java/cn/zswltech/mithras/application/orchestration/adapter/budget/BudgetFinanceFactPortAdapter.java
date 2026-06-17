package cn.zswltech.mithras.application.orchestration.adapter.budget;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.budget.application.port.BudgetFinanceFactPort;
import cn.zswltech.mithras.budget.application.port.BudgetFinanceProjectProfitSnapshot;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitDetailMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceSubjectBalanceAssistMapper;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceBcmBalanceMf;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceSubjectBalanceAssist;
import cn.zswltech.mithras.finance.service.budget.FinanceBcmBalanceMfService;
import cn.zswltech.mithras.foundation.port.OrgResolver;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BudgetFinanceFactPortAdapter implements BudgetFinanceFactPort {

    @Resource
    private FinanceSubjectBalanceAssistMapper financeSubjectBalanceAssistMapper;
    @Resource
    private FinanceBcmBalanceMfService financeBcmBalanceMfService;
    @Resource
    private FinanceProjectProfitDetailMapper financeProjectProfitDetailMapper;
    @Resource
    private OrgResolver orgResolver;

    @Override
    public boolean hasSubjectBalanceAssist(Integer year, Integer month) {
        return financeSubjectBalanceAssistMapper.selectCount(Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
                .eq(FinanceSubjectBalanceAssist::getYear, year)
                .eq(FinanceSubjectBalanceAssist::getMonth, month)) > 0;
    }

    @Override
    public Map<Long, Map<String, BigDecimal>> listMonthDeptRiskValues(Integer year, Integer month) {
        List<OrgDO> orgList = orgResolver.listAllDept();
        Map<String, OrgDO> orgMap = orgList.stream()
                .filter(e -> Objects.nonNull(e.getMainOrgId()))
                .collect(Collectors.toMap(e -> e.getMainOrgId().toString(), e -> e, (a, b) -> b));
        List<FinanceSubjectBalanceAssist> balanceAssists = financeSubjectBalanceAssistMapper.selectList(Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
                .eq(FinanceSubjectBalanceAssist::getYear, year)
                .eq(FinanceSubjectBalanceAssist::getMonth, month));
        Map<Long, Map<String, BigDecimal>> dept2RiskName2Value = new HashMap<>();

        Map<String, List<FinanceSubjectBalanceAssist>> mainOrgId2Balance = balanceAssists.stream()
                .collect(Collectors.groupingBy(FinanceSubjectBalanceAssist::getDimFnumber));
        Map<String, Map<String, BigDecimal>> fid2BcmBalanceMf = listBcmRiskValueByAssistGroup(year, month);

        mainOrgId2Balance.forEach((mainOrgId, assists) -> {
            OrgDO orgDO = orgMap.get(mainOrgId);
            if (Objects.isNull(orgDO)) {
                log.error("预算管理-预算考核-通过mainOrgId没有找到对应的deptId[{}]", mainOrgId);
                return;
            }
            Map<String, BigDecimal> riskName2Value = new HashMap<>();
            if (ObjectUtil.isNotEmpty(assists)) {
                assists.forEach(balanceAssist -> {
                    Map<String, BigDecimal> riskValues = fid2BcmBalanceMf.get(balanceAssist.getFid());
                    if (ObjectUtil.isNotEmpty(riskValues)) {
                        riskValues.forEach((k, v) -> riskName2Value.put(k, riskName2Value.getOrDefault(k, BigDecimal.ZERO).add(v == null ? BigDecimal.ZERO : v)));
                    }
                });
            }
            dept2RiskName2Value.put(orgDO.getId(), riskName2Value);
        });
        return dept2RiskName2Value;
    }

    @Override
    public List<BudgetFinanceProjectProfitSnapshot> listProjectProfitDetails(Integer year, Integer month) {
        return financeProjectProfitDetailMapper.selectList(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                        .eq(FinanceProjectProfitDetail::getYear, year)
                        .eq(FinanceProjectProfitDetail::getMonth, month))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private Map<String, Map<String, BigDecimal>> listBcmRiskValueByAssistGroup(Integer year, Integer month) {
        Map<String, Map<String, BigDecimal>> fid2BcmBalanceMf = new HashMap<>();
        int pageSize = 1000;
        int pageNum = 1;
        while (true) {
            List<FinanceBcmBalanceMf> bcmBalanceMfs = financeBcmBalanceMfService.page(
                            new Page<>(pageNum, pageSize),
                            Wrappers.<FinanceBcmBalanceMf>lambdaQuery()
                                    .eq(FinanceBcmBalanceMf::getF_year, year)
                                    .eq(FinanceBcmBalanceMf::getF_period, month))
                    .getRecords();
            if (ObjectUtil.isEmpty(bcmBalanceMfs)) {
                break;
            }
            Map<String, List<FinanceBcmBalanceMf>> assistGroupId2Data = bcmBalanceMfs.stream()
                    .collect(Collectors.groupingBy(FinanceBcmBalanceMf::getFassgrpid));
            assistGroupId2Data.forEach((assistGroupId, datas) -> mergeRiskValue(fid2BcmBalanceMf, assistGroupId, datas));
            pageNum++;
        }
        return fid2BcmBalanceMf;
    }

    private void mergeRiskValue(Map<String, Map<String, BigDecimal>> fid2BcmBalanceMf,
                                String assistGroupId,
                                List<FinanceBcmBalanceMf> datas) {
        if (ObjectUtil.isEmpty(datas)) {
            return;
        }
        Map<String, List<FinanceBcmBalanceMf>> riskName2Bean = datas.stream()
                .collect(Collectors.groupingBy(FinanceBcmBalanceMf::getRiskName));
        riskName2Bean.forEach((riskName, riskBcmBalanceMf) -> {
            Map<String, BigDecimal> riskValueMap = fid2BcmBalanceMf.computeIfAbsent(assistGroupId, k -> new HashMap<>());
            BigDecimal riskValue = riskBcmBalanceMf.stream()
                    .map(FinanceBcmBalanceMf::getRiskValue)
                    .filter(ObjectUtil::isNotEmpty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            riskValueMap.put(riskName, riskValueMap.computeIfAbsent(riskName, k -> BigDecimal.ZERO).add(riskValue));
        });
    }

    private BudgetFinanceProjectProfitSnapshot convert(FinanceProjectProfitDetail detail) {
        BudgetFinanceProjectProfitSnapshot snapshot = new BudgetFinanceProjectProfitSnapshot();
        snapshot.setAssessDeptId(detail.getAssessDeptId());
        snapshot.setRiskBalanceBeginYear(detail.getRiskBalanceBeginYear());
        snapshot.setTotalRiskThisYear(detail.getTotalRiskThisYear());
        snapshot.setTotalCostThisYear(detail.getTotalCostThisYear());
        snapshot.setTotalAdditionalTaxThisYear(detail.getTotalAdditionalTaxThisYear());
        return snapshot;
    }
}

package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceBcmBalanceMf;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceSubjectBalanceAssist;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.finance.FinanceSubjectBalanceAssistService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName FinanceRiskHelp
 * @Description 用于解析获取指标公式
 * @Author jackerhe
 * @Date 2025/5/20 14:47
 * @Version 1.0
 **/
@Slf4j
@Service
public class FinanceRiskHelp {
    @Resource
    private FinanceBcmBalanceMfService financeBcmBalanceMfService;
    @Resource
    private FinanceSubjectBalanceAssistService financeSubjectBalanceAssistService;

    //获取某个月下所有合同下的指标
    /*public Map<Long, Map<String, BigDecimal>> getMonthDeptValues(Integer year, Integer month) {
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listAllDept();
        Map<String, OrgDO> orgMap = orgList.stream().filter(e -> Objects.nonNull(e.getMainOrgId())).collect(Collectors.toMap(e -> e.getMainOrgId().toString(), e -> e, (a, b) -> b));
        //科目余额辅助表
        List<FinanceSubjectBalanceAssist> balanceAssists = financeSubjectBalanceAssistService.list(Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
                .eq(FinanceSubjectBalanceAssist::getYear, year)
                .eq(FinanceSubjectBalanceAssist::getMonth, month));
        Map<Long, Map<String, BigDecimal>> dept2RiskName2Value = new HashMap<>();

        List<FinanceBcmBalanceMf> bcmBalanceMfs = financeBcmBalanceMfService.list(Wrappers.<FinanceBcmBalanceMf>lambdaQuery()
                .eq(FinanceBcmBalanceMf::getF_year, year)
                .eq(FinanceBcmBalanceMf::getF_period, month));
        if (ObjectUtil.isEmpty(balanceAssists) || ObjectUtil.isEmpty(bcmBalanceMfs)) {
            return dept2RiskName2Value;
        }
        Map<String, List<FinanceSubjectBalanceAssist>> mainOrgId2Balance = balanceAssists.stream().collect(Collectors.groupingBy(FinanceSubjectBalanceAssist::getDimFnumber));
        Map<String, List<FinanceBcmBalanceMf>> fid2BcmBalanceMf = bcmBalanceMfs.stream().collect(Collectors.groupingBy(FinanceBcmBalanceMf::getFassgrpid));
        mainOrgId2Balance.forEach((mainOrgId, assists) -> {
            OrgDO orgDO = orgMap.get(mainOrgId);
            if (Objects.isNull(orgDO)) {
                log.error("预算管理-预算执行情况表-通过mainOrgId没有找到对应的deptId[{}]", mainOrgId);
                return;
            }
            //部门下每个指标对应值
            Map<String, BigDecimal> riskName2Value = new HashMap<>();
            if (ObjectUtil.isNotEmpty(assists)) {
                assists.forEach(balanceAssist -> {
                    List<FinanceBcmBalanceMf> tmpBcmBalanceMfs = fid2BcmBalanceMf.get(balanceAssist.getFid());
                    if (ObjectUtil.isNotEmpty(tmpBcmBalanceMfs)) {
                        tmpBcmBalanceMfs.forEach(e -> riskName2Value.put(e.getRiskName(), riskName2Value.getOrDefault(e.getRiskName(), BigDecimal.ZERO).add(e.getRiskValue() == null ? BigDecimal.ZERO : e.getRiskValue())));
                    }
                });
            }
            dept2RiskName2Value.put(orgDO.getId(), riskName2Value);
        });
        return dept2RiskName2Value;
    }*/

    //获取某个月下所有合同下的指标
    public Map<Long, Map<String, BigDecimal>> getMonthDeptValues(Integer year, Integer month) {
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listAllDept();
        Map<String, OrgDO> orgMap = orgList.stream().filter(e -> Objects.nonNull(e.getMainOrgId())).collect(Collectors.toMap(e -> e.getMainOrgId().toString(), e -> e, (a, b) -> b));
        //科目余额辅助表
        List<FinanceSubjectBalanceAssist> balanceAssists = financeSubjectBalanceAssistService.list(Wrappers.<FinanceSubjectBalanceAssist>lambdaQuery()
                .eq(FinanceSubjectBalanceAssist::getYear, year)
                .eq(FinanceSubjectBalanceAssist::getMonth, month));
        Map<Long, Map<String, BigDecimal>> dept2RiskName2Value = new HashMap<>();

        Map<String, List<FinanceSubjectBalanceAssist>> mainOrgId2Balance = balanceAssists.stream().collect(Collectors.groupingBy(FinanceSubjectBalanceAssist::getDimFnumber));
        Map<String, Map<String, BigDecimal>> fid2BcmBalanceMf = new HashMap<>();
        int pageSize = 1000; // 根据实际情况调整
        int pageNum = 1;
        List<FinanceBcmBalanceMf> bcmBalanceMfs = null;
        while (true) {
            bcmBalanceMfs = financeBcmBalanceMfService.page(
                    new Page<>(pageNum, pageSize),
                    Wrappers.<FinanceBcmBalanceMf>lambdaQuery()
                            .eq(FinanceBcmBalanceMf::getF_year, year)
                            .eq(FinanceBcmBalanceMf::getF_period, month))
                    .getRecords();

            if (ObjectUtil.isEmpty(bcmBalanceMfs)) {
                break;
            }
            // 合并到总映射中
            Map<String, List<FinanceBcmBalanceMf>> faid2Data = bcmBalanceMfs.stream().collect(Collectors.groupingBy(FinanceBcmBalanceMf::getFassgrpid));
            faid2Data.forEach((fassgrpid, datas) -> {
                if (ObjectUtil.isNotEmpty(datas)) {
                    Map<String, List<FinanceBcmBalanceMf>> riskName2Bean = datas.stream().collect(Collectors.groupingBy(FinanceBcmBalanceMf::getRiskName));
                    riskName2Bean.forEach((riskName, riskBcmBalanceMf) -> {
                        Map<String, BigDecimal> stringBigDecimalMap = fid2BcmBalanceMf.computeIfAbsent(fassgrpid, k -> new HashMap<>());
                        stringBigDecimalMap.put(riskName, stringBigDecimalMap.computeIfAbsent(riskName, k -> BigDecimal.ZERO).add(riskBcmBalanceMf.stream().map(FinanceBcmBalanceMf::getRiskValue).filter(ObjectUtil::isNotEmpty).reduce(BigDecimal.ZERO, BigDecimal::add)));
                    });
                }
            });
            pageNum++;
        }
        mainOrgId2Balance.forEach((mainOrgId, assists) -> {
            OrgDO orgDO = orgMap.get(mainOrgId);
            if (Objects.isNull(orgDO)) {
                log.error("预算管理-预算执行情况表-通过mainOrgId没有找到对应的deptId[{}]", mainOrgId);
                return;
            }
            //部门下每个指标对应值
            Map<String, BigDecimal> riskName2Value = new HashMap<>();
            if (ObjectUtil.isNotEmpty(assists)) {
                assists.forEach(balanceAssist -> {
                    Map<String, BigDecimal> tmpBcmBalanceMfs = fid2BcmBalanceMf.get(balanceAssist.getFid());
                    if (ObjectUtil.isNotEmpty(tmpBcmBalanceMfs)) {
                        tmpBcmBalanceMfs.forEach((k, v) -> riskName2Value.put(k, riskName2Value.getOrDefault(k, BigDecimal.ZERO).add(v == null ? BigDecimal.ZERO : v)));
                    }
                });
            }
            dept2RiskName2Value.put(orgDO.getId(), riskName2Value);
        });
        return dept2RiskName2Value;
    }


    public BigDecimal getRiskValue(Map<String, BigDecimal> riskTree, String riskFormula) {
        BigDecimal value = BigDecimal.ZERO;
        if (ObjectUtil.isEmpty(riskFormula)) {
            return value;
        }
        String[] split = riskFormula.split("\\+");
        for(int i = 0; i< split.length; i++) {
            value = value.add(riskTree.getOrDefault(split[i], BigDecimal.ZERO));
        }
        return value;
    }

    /**
     *  for(int i = 0; i<split.length; i++) {
     *             String addString = split[i];
     *             if (ObjectUtil.isNotEmpty(addString)) {
     *                 String[] addList = addString.split("");
     *                 for(int j = 0; j < addList.length; j++) {
     *
     *                 }
     *             }
     *
     *         }
     **/
}

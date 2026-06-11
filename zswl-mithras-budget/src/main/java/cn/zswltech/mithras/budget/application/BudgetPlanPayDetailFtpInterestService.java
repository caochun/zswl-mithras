package cn.zswltech.mithras.budget.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDynamicTableRSP;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayDetailMapper;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanPayDetailCostGroupDTO;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetail;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailPrice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayDetailFtpInterestMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailFtpInterest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-资金成本（ftp计息）
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayDetailFtpInterestService extends ServiceImpl<BudgetPlanPayDetailFtpInterestMapper, BudgetPlanPayDetailFtpInterest> implements BudgetPlanPayDetailFtpInterestApplicationService {
    @Resource
    private BudgetPlanPayDetailMapper budgetPlanPayDetailMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void copyByDetailId(Long oldDetailId, Long newDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailFtpInterest> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailFtpInterest::getBudgetPlanPayDetailId, oldDetailId);
        List<BudgetPlanPayDetailFtpInterest> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<BudgetPlanPayDetailFtpInterest> copy = BeanUtil.copyToList(list, BudgetPlanPayDetailFtpInterest.class);
        copy.forEach(e -> {
            e.reset();
            e.setBudgetPlanPayDetailId(newDetailId);
        });
        this.saveBatch(copy);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetailFtpInterest> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailFtpInterest::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanPayDetailFtpInterest> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailFtpInterest::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public Map<Long, Long> getCostMapByDetailIds(Collection<Long> detailIds, LocalDate startDate, LocalDate endDate) {
        if (CollectionUtil.isEmpty(detailIds)) {
            return Collections.emptyMap();
        }
        List<BudgetPlanPayDetailCostGroupDTO> list = this.getBaseMapper().listCostGroupByDetail(detailIds, startDate, endDate);
        return list.stream().collect(Collectors.toMap(BudgetPlanPayDetailCostGroupDTO::getBudgetPlanPayDetailId, BudgetPlanPayDetailCostGroupDTO::getCostSum));
    }

    public BudgetPlanPayDetailDynamicTableRSP getFtpInterestDynamicTable(Long budgetPlanPayDetailId) {
        BudgetPlanPayDetail budgetPlanPayDetail = budgetPlanPayDetailMapper.selectById(budgetPlanPayDetailId);
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> priceQuery = Wrappers.lambdaQuery();
        priceQuery.eq(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, budgetPlanPayDetailId);
        BudgetPlanPayDetailDynamicTableRSP rsp = new BudgetPlanPayDetailDynamicTableRSP();
        rsp.setFtp(budgetPlanPayDetail.getFtp());
        List<BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO> groupResult = this.getBaseMapper().listGroupResult(budgetPlanPayDetailId);
        if (CollectionUtil.isEmpty(groupResult)) {
            rsp.setHeaderList(Collections.emptyList());
            rsp.setDataList(Collections.emptyList());
        } else {
            // 根据返回年份确定表头
            List<Integer> allYears = groupResult.stream().sorted(Comparator.comparing(BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO::getInterestYear)).map(BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO::getInterestYear).distinct().collect(Collectors.toList());
            // 按年月分组
            Map<String, BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO> groupYearMonthMap = groupResult.stream().collect(Collectors.toMap(e -> e.getInterestYear() + "-" + e.getInterestMonth(), e -> e));
            // 表头行
            List<String> headerList = new LinkedList<>();
            headerList.add("月份");
            allYears.forEach(e -> headerList.add(e + "年"));
            rsp.setHeaderList(headerList);
            // 数据行
            List<List<Object>> dataList = new LinkedList<>();
            // 1-12月的收入数据
            for (int i = 1; i <= 12; i++) {
                List<Object> rowData = new LinkedList<>();
                rowData.add(i + "月");
                for (Integer year : allYears) {
                    BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO dto = groupYearMonthMap.get(year + "-" + i);
                    if (Objects.nonNull(dto)) {
                        rowData.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(dto.getCostMonthTotal()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                    } else {
                        rowData.add("0.00");
                    }
                }
                dataList.add(rowData);
            }
            // 统计行
            Map<Integer, List<BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO>> groupYearMap = groupResult.stream().collect(Collectors.groupingBy(BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO::getInterestYear));
            List<Object> averageOccupyRow = new LinkedList<>();
            averageOccupyRow.add("全年平均资金占用额");
            List<Object> fundCostRow = new LinkedList<>();
            fundCostRow.add("营业成本");
//            List<Object> fundCostWithoutTaxRow = new LinkedList<>();
//            fundCostWithoutTaxRow.add("营业成本（不含税）");
            for (int year : allYears) {
                List<BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO> list = groupYearMap.get(year);
                if (Objects.isNull(list)) {
                    list = new LinkedList<>();
                }
                long sum = list.stream().mapToLong(BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO::getCostMonthTotal).sum();
                long sumOccupy = list.stream().mapToLong(BudgetPlanPayDetailFtpInterestMapper.IncomeGroupDTO::getOccupyMonthTotal).sum();
                BigDecimal averageBD = BigDecimal.valueOf(sumOccupy).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP);
//                BigDecimal sumWithoutTaxBD = BigDecimal.valueOf(sum).divide(BigDecimal.ONE.add(FinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType())), 20, RoundingMode.HALF_UP);
                averageOccupyRow.add(NumberUtil.decimalFormat("#,##0.00", averageBD.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                fundCostRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
//                fundCostWithoutTaxRow.add(NumberUtil.decimalFormat("#,##0.00", sumWithoutTaxBD.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
            }
            dataList.add(averageOccupyRow);
            dataList.add(fundCostRow);
//            dataList.add(fundCostWithoutTaxRow);
            rsp.setDataList(dataList);
        }
        return rsp;
    }
}

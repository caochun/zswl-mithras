package cn.zswltech.mithras.budget.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDynamicTableRSP;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayDetailMapper;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanPayDetailIncomeGroupDTO;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetail;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailPrice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayDetailIncomeSharingMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailIncomeSharing;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-收入分摊
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayDetailIncomeSharingService extends ServiceImpl<BudgetPlanPayDetailIncomeSharingMapper, BudgetPlanPayDetailIncomeSharing> implements BudgetPlanPayDetailIncomeSharingApplicationService {
    @Resource
    private BudgetPlanPayDetailMapper budgetPlanPayDetailMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void copyByDetailId(Long oldDetailId, Long newDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailIncomeSharing> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailIncomeSharing::getBudgetPlanPayDetailId, oldDetailId);
        List<BudgetPlanPayDetailIncomeSharing> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<BudgetPlanPayDetailIncomeSharing> copy = BeanUtil.copyToList(list, BudgetPlanPayDetailIncomeSharing.class);
        copy.forEach(e -> {
            e.reset();
            e.setBudgetPlanPayDetailId(newDetailId);
        });
        this.saveBatch(copy);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetailIncomeSharing> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailIncomeSharing::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanPayDetailIncomeSharing> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailIncomeSharing::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public Map<Long, Long> getIncomeMapByDetailIds(Collection<Long> detailIds, LocalDate startDate, LocalDate endDate) {
        if (CollectionUtil.isEmpty(detailIds)) {
            return Collections.emptyMap();
        }
        List<BudgetPlanPayDetailIncomeGroupDTO> list = this.getBaseMapper().listIncomeGroupByDetail(detailIds, startDate, endDate);
        return list.stream().collect(Collectors.toMap(BudgetPlanPayDetailIncomeGroupDTO::getBudgetPlanPayDetailId, BudgetPlanPayDetailIncomeGroupDTO::getIncomeSum));
    }

    public BudgetPlanPayDetailDynamicTableRSP getIncomeSharingDynamicTable(Long budgetPlanPayDetailId) {
        BudgetPlanPayDetail budgetPlanPayDetail = budgetPlanPayDetailMapper.selectById(budgetPlanPayDetailId);
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> priceQuery = Wrappers.lambdaQuery();
        priceQuery.eq(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, budgetPlanPayDetailId);
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).getOne(priceQuery);
        BudgetPlanPayDetailDynamicTableRSP rsp = new BudgetPlanPayDetailDynamicTableRSP();
        List<BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO> groupResult = this.getBaseMapper().listGroupResult(budgetPlanPayDetailId);
        if (CollectionUtil.isEmpty(groupResult)) {
            rsp.setHeaderList(Collections.emptyList());
            rsp.setDataList(Collections.emptyList());
        } else {
            // 根据返回年份确定表头
            List<Integer> allYears = groupResult.stream().sorted(Comparator.comparing(BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO::getYear)).map(BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO::getYear).distinct().collect(Collectors.toList());
            // 按年月分组
            Map<String, BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO> groupYearMonthMap = groupResult.stream().collect(Collectors.toMap(e -> e.getYear() + "-" + e.getMonth(), e -> e));
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
                    BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO dto = groupYearMonthMap.get(year + "-" + i);
                    if (Objects.nonNull(dto)) {
                        rowData.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(dto.getIncomeSum()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                    } else {
                        rowData.add("0.00");
                    }
                }
                dataList.add(rowData);
            }
            // 统计行
            Map<Integer, List<BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO>> groupYearMap = groupResult.stream().collect(Collectors.groupingBy(BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO::getYear));
//            List<Object> sumYearRow = new LinkedList<>();
//            sumYearRow.add("年小计");
            List<Object> consultingFeeRow = new LinkedList<>();
            consultingFeeRow.add("咨询服务费");
            List<Object> sumYearTotalRow = new LinkedList<>();
            sumYearTotalRow.add("营业收入合计（含税）");
            List<Object> sumYearTotalWithoutTaxRow = new LinkedList<>();
            sumYearTotalWithoutTaxRow.add("营业收入合计（不含税）");
            for (int i = 0; i < allYears.size(); i++) {
                int year = allYears.get(i);
                long consultingFee = 0L;
                long consultingFeeWithoutTax = 0L;
                if (i == 0 && Objects.nonNull(budgetPlanPayDetailPrice) && Objects.nonNull(budgetPlanPayDetailPrice.getProjectAmount()) && Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
                    // 需要在首年计算咨询服务费
                    BigDecimal consultingFeeBD = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate())).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP);
                    consultingFee = BudgetFinancialUtil.mithrasLongDecimalTwo(consultingFeeBD.longValue());
                    BigDecimal consultingFeeWithoutTaxDB = consultingFeeBD.divide(BigDecimal.ONE.add(BudgetFinancialUtil.ensureConsultingTaxRate()), 20, RoundingMode.HALF_UP);
                    consultingFeeWithoutTax = BudgetFinancialUtil.mithrasLongDecimalTwo(consultingFeeWithoutTaxDB.longValue());
                }
                consultingFeeRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(consultingFee).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                List<BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO> list = groupYearMap.get(year);
                if (Objects.isNull(list)) {
                    list = new LinkedList<>();
                }
                long sum = list.stream().mapToLong(BudgetPlanPayDetailIncomeSharingMapper.IncomeGroupDTO::getIncomeSum).sum();
                BigDecimal sumWithoutTaxBD = new BigDecimal(sum).divide(BigDecimal.ONE.add(BudgetFinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType())), 20, RoundingMode.HALF_UP);
                long sumWithoutTax = BudgetFinancialUtil.mithrasLongDecimalTwo(sumWithoutTaxBD.longValue());
//                sumYearRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                sumYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(sum + consultingFee).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                sumYearTotalWithoutTaxRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(sumWithoutTax + consultingFeeWithoutTax).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
            }
//            dataList.add(sumYearRow);
            dataList.add(consultingFeeRow);
            dataList.add(sumYearTotalRow);
            dataList.add(sumYearTotalWithoutTaxRow);
            rsp.setDataList(dataList);
        }
        return rsp;
    }
}

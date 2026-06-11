package cn.zswltech.mithras.dashboard.controller;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardProjectPayApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dashboard.application.DashboardProjectPayApplicationService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;


/**
 * @author dingqi
 * @date 2024/6/25
 * @description
 */
@RestController
@Slf4j
public class DashboardProjectPayController implements DashboardProjectPayApi {
    @Resource
    private DashboardProjectPayApplicationService dashboardProjectPayInfoService;

    @Override
    public R<Map<String, Object>> actualPayList(@Valid DashboardProjectPayListREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardProjectPayListRSP> list = dashboardProjectPayInfoService.list(req);
        map.put(RECORDS, list);
        try {
            DashboardProjectPayListRSP value = DashboardHelpUtil.countValueUnitDTO(list, new DashboardProjectPayListRSP());
            if (CollUtil.isNotEmpty(list)) {
                // 重新计算那四个百分比字段
                BigDecimal actualIrr = BigDecimal.ZERO;
                BigDecimal interestRate = BigDecimal.ZERO;
                BigDecimal consultingFeeRate = BigDecimal.ZERO;
                BigDecimal commissionRate = BigDecimal.ZERO;
                BigDecimal totalPayAmount = BigDecimal.ZERO;
                List<BigDecimal> collect = list.stream().map(e -> e.getActualPayAmount().getValue()).map(BigDecimal::new).collect(Collectors.toList());
                for (BigDecimal decimal : collect) {
                    totalPayAmount = totalPayAmount.add(decimal);
                }
                for (DashboardProjectPayListRSP item : list) {
                    actualIrr = actualIrr.add(new BigDecimal(item.getActualIrr().getValue())
                            .multiply(new BigDecimal(item.getActualPayAmount().getValue()))
                            .divide(totalPayAmount, 4, RoundingMode.HALF_UP));
                    interestRate = interestRate.add(new BigDecimal(item.getInterestRate().getValue())
                            .multiply(new BigDecimal(item.getActualPayAmount().getValue()))
                            .divide(totalPayAmount, 4, RoundingMode.HALF_UP));
                    consultingFeeRate = consultingFeeRate.add(new BigDecimal(item.getConsultingFeeRate().getValue())
                            .multiply(new BigDecimal(item.getActualPayAmount().getValue()))
                            .divide(totalPayAmount, 4, RoundingMode.HALF_UP));
                    commissionRate = commissionRate.add(new BigDecimal(item.getCommissionRate().getValue())
                            .multiply(new BigDecimal(item.getActualPayAmount().getValue()))
                            .divide(totalPayAmount, 4, RoundingMode.HALF_UP));
                }
                value.setActualIrr(new ValueUnitDTO(actualIrr.setScale(2, RoundingMode.HALF_UP).toString(), "%"));
                value.setInterestRate(new ValueUnitDTO(interestRate.setScale(2, RoundingMode.HALF_UP).toString(), "%"));
                value.setConsultingFeeRate(new ValueUnitDTO(consultingFeeRate.setScale(2, RoundingMode.HALF_UP).toString(), "%"));
                value.setCommissionRate(new ValueUnitDTO(commissionRate.setScale(2, RoundingMode.HALF_UP).toString(), "%"));
            }
            map.put(SUM_DATE, value);
        } catch (Exception e) {
            log.warn("DashboardProjectPayController actualPayList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<DashboardProjectPayStatisticsRSP> statistics(@Valid DashboardProjectPayStatisticsREQ req) {
        return R.ok(dashboardProjectPayInfoService.statistics(req));
    }

    @Override
    public R<List<DashboardProjectPayStatisticsByDeptRSP>> statisticsGroupByDept(@Valid DashboardProjectPayStatisticsREQ req) {
        return R.ok(dashboardProjectPayInfoService.statisticsListByDept(req));
    }
}

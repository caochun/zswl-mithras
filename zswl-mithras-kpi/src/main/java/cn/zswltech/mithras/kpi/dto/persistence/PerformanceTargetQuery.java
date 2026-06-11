package cn.zswltech.mithras.kpi.dto.persistence;

import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class PerformanceTargetQuery {

    private Map<Integer, List<Integer>> map;

    /**
     * 业务部门
     */
    private List<Long> bizDeptIdList;

    /**
     * @see BusinessTypeEnum
     */
    @NotNull
    private String businessType;

    /**
     * @see BelongTypeEnum
     */
    @NotNull
    private String belongType;


    /**
     * 根据时间区间填充查询需要使用的map
     */
    public void buildDateMap(LocalDate queryDateFrom, LocalDate queryDateTo) {
        Map<Integer, List<Integer>> yearMonthMap = new LinkedHashMap<>();
        long monthsBetween = ChronoUnit.MONTHS.between(queryDateFrom, queryDateTo) + 1;
        for (long i = 0; i < monthsBetween; i++) {
            LocalDate date = queryDateFrom.plusMonths(i);
            int year = date.getYear();
            int month = date.getMonthValue();
            yearMonthMap.computeIfAbsent(year, k -> new ArrayList<>()).add(month);
        }
        this.setMap(yearMonthMap);
    }

}

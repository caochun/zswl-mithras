package cn.zswltech.mithras.dto.dashboard.operation;

import lombok.Data;

import java.util.List;

@Data
public class DashboardOperationConversionStatisticsREQ extends DashboardOperationBaseREQ {
    private List<Long> bizDeptIdList;
}

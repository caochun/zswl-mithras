package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.dashboard.operation.DashboardOperationPayListREQ;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
public class DashboardOperationPayQuery {
    private LocalDate queryDateFrom;
    private LocalDate queryDateTo;
    private List<Long> bizDeptIdList;
    private List<String> riskControlList;



}

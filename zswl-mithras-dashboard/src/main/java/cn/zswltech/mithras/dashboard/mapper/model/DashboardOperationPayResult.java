package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Data
public class DashboardOperationPayResult {

    private Long actualPayAmountLong;
//    private Long planAmountLong;
    private Long bizDeptId;

}

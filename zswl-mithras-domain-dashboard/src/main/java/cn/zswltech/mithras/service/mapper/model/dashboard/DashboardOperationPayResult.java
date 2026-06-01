package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Data
public class DashboardOperationPayResult {

    private Long actualPayAmountLong;
//    private Long planAmountLong;
    private Long bizDeptId;

}

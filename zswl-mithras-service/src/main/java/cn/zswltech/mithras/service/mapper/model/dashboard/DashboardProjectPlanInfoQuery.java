package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPlanInfoQuery extends CommonAuthQuery {
    private String contractCode;
    private String projName;
    private Long bizDeptId;
    private LocalDate queryDateFrom;
    private LocalDate queryDateTo;
}

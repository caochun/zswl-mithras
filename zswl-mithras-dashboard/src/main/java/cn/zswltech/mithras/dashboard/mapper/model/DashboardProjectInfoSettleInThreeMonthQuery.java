package cn.zswltech.mithras.dashboard.mapper.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectInfoSettleInThreeMonthQuery extends CommonAuthQuery {
    private Long clientId;
    private String contractCode;
    private LocalDate deadlineFrom;
    private LocalDate deadlineTo;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private LocalDate queryDateFrom;
    private LocalDate queryDateTo;
    private List<Long> ids;
    private String permissionType;
}

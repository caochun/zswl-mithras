package cn.zswltech.mithras.dto.budget;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BudgetPlanPayDetailMonthListREQ extends PageReq {
    @ApiModelProperty("投放计划id")
    @NotNull(message = "<投放计划id>不能为空")
    private Long budgetPlanPayId;

    @ApiModelProperty("部门id")
    private Long belongDeptId;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("投放日-起")
    private LocalDate planPayDateFrom;

    @ApiModelProperty("投放日-止")
    private LocalDate planPayDateTo;
}

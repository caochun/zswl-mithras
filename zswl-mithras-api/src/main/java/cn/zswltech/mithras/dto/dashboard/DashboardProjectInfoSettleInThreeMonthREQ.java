package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Data
public class DashboardProjectInfoSettleInThreeMonthREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("到期日-起")
    private LocalDate deadlineFrom;
    @ApiModelProperty("到期日-止")
    private LocalDate deadlineTo;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    private List<Long> ids;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}

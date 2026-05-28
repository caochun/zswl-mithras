package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@Data
public class DashboardProjectStagePaymentDetailREQ {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("申请付款日期开始")
    private LocalDate applyPayDateFrom;
    @ApiModelProperty("申请付款日期结束")
    private LocalDate applyPayDateTo;
    @ApiModelProperty("付款申请状态")
    private String paymentStatusCode;
    @ApiModelProperty("付款申请审批状态")
    private String paymentProcessStatusCode;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("可见范围 workbenchProjectDataRange")
    @NotNull
    private String permissionType;
}

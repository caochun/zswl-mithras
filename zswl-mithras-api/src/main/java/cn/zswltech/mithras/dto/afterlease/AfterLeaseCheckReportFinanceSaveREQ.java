package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/24
 * @description
 */
@Data
@ApiModel("租后管理-保存客户财务报表快照数据")
public class AfterLeaseCheckReportFinanceSaveREQ {
    @ApiModelProperty("检查计划客户记录id")
    @NotNull(message = "检查计划客户记录id不能为空")
    private Long checkPlanClientId;

    @ApiModelProperty("客户id")
    @NotNull(message = "客户id不能为空")
    private Long clientId;

    @ApiModelProperty("客户在项目中的身份 承租人-LESSEE 担保人-GUARANTOR")
    @NotBlank(message = "客户项目身份不能为空")
    private String clientProjectIdentity;

    @ApiModelProperty("报表类型")
    @NotBlank(message = "报表类型不能为空")
    private String subjectType;

    @ApiModelProperty("查询条件json数据")
    @NotBlank(message = "查询条件不能为空")
    private String queryJsonData;

    @ApiModelProperty("查询结果json数据")
    private String resultJsonData;
}

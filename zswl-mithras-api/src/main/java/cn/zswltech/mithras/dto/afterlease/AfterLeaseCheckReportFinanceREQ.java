package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-租后检查财务报表快照数据-请求体")
public class AfterLeaseCheckReportFinanceREQ extends VersionBaseREQ {
    @ApiModelProperty("检查客户记录id")
    @NotNull(message = "检查客户记录id不能为空")
    private Long checkPlanClientId;

    @ApiModelProperty("客户id不能为空")
    @NotNull(message = "客户id不能为空")
    private Long clientId;

    @ApiModelProperty("客户在项目中的身份 承租人-LESSEE 担保人-GUARANTOR")
    @NotBlank(message = "客户项目身份不能为空")
    private String clientProjectIdentity;

    @ApiModelProperty("财务报表类型")
    @NotNull(message = "财务报表类型不能为空")
    private String subjectType;
}

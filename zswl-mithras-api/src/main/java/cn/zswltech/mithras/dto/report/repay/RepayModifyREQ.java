package cn.zswltech.mithras.dto.report.repay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 征信报送-还款表查询入参
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-还款表编辑入参")
public class RepayModifyREQ {

    @ApiModelProperty("idKey(结构：计划id_实际还款id)")
    @NotBlank
    private String idKey;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @NotNull(message = "修改原因不能为空")
    @ApiModelProperty("修改原因")
    private String reason;

    @ApiModelProperty("宽限期(天)")
    @NotNull
    private Integer gracePeriod;

}

package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/9 10:49
 */
@Data
@ApiModel("租后管理-检查计划关联客户变更请求体")
public class AfterLeaseCheckClientModifyReq {
    public static final String SCENE_ASSET_MANAGER = "assetManager";
    public static final String SCENE_RISK_MANAGER = "riskManager";

    @ApiModelProperty("id")
    @NotNull(message = "客户检查计划id不能为空")
    private Long id;

    @ApiModelProperty("协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty("检查时间")
    private LocalDate checkTime;

    @ApiModelProperty("检查形式")
    private String checkWay;

    @NotBlank(message = "操作场景不能为空")
    @ApiModelProperty("更新数据场景，assetManager-资产管理，riskManager-协查风控经理")
    private String scene;
}

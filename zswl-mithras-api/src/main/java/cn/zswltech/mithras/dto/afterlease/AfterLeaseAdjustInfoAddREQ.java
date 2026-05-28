package cn.zswltech.mithras.dto.afterlease;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@ApiModel("租后调整信息表新增-请求体")
public class AfterLeaseAdjustInfoAddREQ {

    /**
    * 项目id
    */
    @ApiModelProperty(value = "项目id")
    @NotNull(message = "项目id不能为空")
    private Long projId;

    @ApiModelProperty(value = "业务调整类型")
    @NotNull(message = "业务调整类型不能为空 AfterLeaseAdjustEnum")
    private String AfterLeaseAdjustType;

}

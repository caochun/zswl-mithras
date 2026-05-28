package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 租后调整信息表
 * @author vico
 * @date 2022-11-08
 */
@Data
@Builder
@ApiModel("租后调整信息表新增-请求体")
public class AfterLeaseAdjustInfoAddRSP {

    /**
    * 项目id
    */
    @ApiModelProperty(value = "调整id")
    private Long adjustId;

}

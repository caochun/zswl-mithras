package cn.zswltech.mithras.dto.client.external.environment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 环保处罚
 * @author yeqing
 * @date 2022-06-23
 */
@Data
@ApiModel("外部信息-环保处罚-删除")
public class EnvironmentPenaltyRemoveREQ {

    /**
     * 主键
     */
    @ApiModelProperty("id")
    @NotNull
    private Long id;

}
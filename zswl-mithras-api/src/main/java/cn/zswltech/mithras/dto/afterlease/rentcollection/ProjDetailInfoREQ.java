package cn.zswltech.mithras.dto.afterlease.rentcollection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-11-18
 **/
@Data
public class ProjDetailInfoREQ {
    @ApiModelProperty("合同id")
    private Long contractId;
}

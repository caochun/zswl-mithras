package cn.zswltech.mithras.dto.projpricing.price;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("比较入参-请求体")
public class ProjPricingCompareREQ {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("流程ID")
    private String processInstanceId;

}

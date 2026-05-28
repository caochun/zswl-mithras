package cn.zswltech.mithras.dto.projpricing.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@Accessors(chain = true)
@ApiModel("项目评审基本信息表新增-返回体")
public class ProjPricingBaseInfoAddRSP {

    @ApiModelProperty(value = "项目评审基本id")
    private Long id;

    @ApiModelProperty(value = "业务类型")
    private String bizType;
}

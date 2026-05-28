package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名风险规模-请求体")
public class BlackGrayEnterpriseRiskScaleREQ {


    @ApiModelProperty(value = "企业名称")
    @NotNull
    private String enterpriseName;


}

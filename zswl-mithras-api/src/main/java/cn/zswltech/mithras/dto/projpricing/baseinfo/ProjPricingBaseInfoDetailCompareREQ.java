package cn.zswltech.mithras.dto.projpricing.baseinfo;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("项目定价基本信息详情查询-请求体")
public class ProjPricingBaseInfoDetailCompareREQ extends VersionBaseREQ {
    @ApiModelProperty("project pricing Id")
    private Long id;

    @ApiModelProperty("类型:PROJ_REVIEW PROJ_PRICING")
    private String type;

    @ApiModelProperty("流程ID")
    private String processInstanceId;
}

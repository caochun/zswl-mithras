package cn.zswltech.mithras.dto.projpricing.baseinfo;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@ApiModel("项目定价基本信息详情查询-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ProjPricingBaseInfoDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("project pricing Id")
    private Long id;

    @ApiModelProperty("流程ID")
    private String processInstanceId;
}

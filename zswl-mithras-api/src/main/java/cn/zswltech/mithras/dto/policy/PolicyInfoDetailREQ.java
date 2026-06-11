package cn.zswltech.mithras.dto.policy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
@ApiModel("保单详情-保单详情-请求体")
public class PolicyInfoDetailREQ {

    @ApiModelProperty("id")
    private Long id;


    @ApiModelProperty("项目id")
    private Long projId;
}

package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel
@Data
@Accessors(chain = true)
public class BlackGrayGroupDetailRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "groupName")
    private String groupName;

    @ApiModelProperty(value = "groupCreditCode")
    private String groupCreditCode;
}

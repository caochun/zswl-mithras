package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class BlackGrayGroupListRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "groupName")
    private String groupName;

    @ApiModelProperty(value = "groupCreditCode")
    private String groupCreditCode;

    @ApiModelProperty(value = "blackGrayType")
    private String blackGrayType;

    @ApiModelProperty(value = "下属企业在库数")
    private Integer entCount = 0;
}

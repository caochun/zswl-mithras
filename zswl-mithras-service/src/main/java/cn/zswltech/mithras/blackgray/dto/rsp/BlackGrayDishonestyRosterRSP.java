package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class BlackGrayDishonestyRosterRSP {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("企业名称")
    private String peopleEnforced;

    @ApiModelProperty("统一社会信用代码")
    private String certCode;

    @ApiModelProperty("黑灰标识")
    private String blackGrayType;

    @ApiModelProperty("名单类型")
    private String rosterType;

    @ApiModelProperty("列入原因")
    private String obligations;

    @ApiModelProperty("披露时间")
    private Date infoPublDate;

}

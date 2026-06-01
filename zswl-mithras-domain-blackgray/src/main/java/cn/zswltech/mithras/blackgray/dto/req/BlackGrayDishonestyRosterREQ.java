package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BlackGrayDishonestyRosterREQ  extends PageReq {

    @ApiModelProperty("被执行企业名称")
    private String peopleEnforced;

    @ApiModelProperty("统一社会编码")
    private String certCode;

    @ApiModelProperty("名单类型")
    private String rosterType;

}

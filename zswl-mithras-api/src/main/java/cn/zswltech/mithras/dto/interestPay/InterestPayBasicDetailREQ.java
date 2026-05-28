package cn.zswltech.mithras.dto.interestPay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("提交入参")
public class InterestPayBasicDetailREQ {

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资类型")
    private String type;

}

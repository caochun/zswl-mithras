package cn.zswltech.mithras.dto.interestpay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("提交入参")
public class InterestPayListREQ extends PageReq {

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty("借款性质")
    private String loanProperty;
}

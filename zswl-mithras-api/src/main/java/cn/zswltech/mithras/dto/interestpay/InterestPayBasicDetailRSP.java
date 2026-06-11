package cn.zswltech.mithras.dto.interestpay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InterestPayBasicDetailRSP extends PageReq {

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty(value = "起息日")
    private LocalDate valueDate;

    @ApiModelProperty(value = "融资金额(元)")
    private Long financingAmount;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务类型展示")
    private String bizTypeDisplay;

    @ApiModelProperty("借款性质")
    private String loanProperty;

}

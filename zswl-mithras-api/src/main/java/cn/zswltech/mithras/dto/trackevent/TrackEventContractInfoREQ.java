package cn.zswltech.mithras.dto.trackevent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class TrackEventContractInfoREQ{

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "业务id")
    private Long bizId;

    @ApiModelProperty(value = "业务来源 -TrackTaskBizSourceEnum")
    private String bizSource;

}



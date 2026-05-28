package cn.zswltech.mithras.dto.trackEvent;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

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



package cn.zswltech.mithras.service.service.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author bigbear
 * @date 2024/11/18 17:26
 * @description
 */
@Data
public class ContractTextManageBO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "客户名字")
    private String clientName;

    @ApiModelProperty("项目主办id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办名字")
    private String projSponsorUserName;

    @ApiModelProperty(value = "签约方式 SigningWayEnum#name")
    private String signingWay;

    @ApiModelProperty(value = "推送时间开始 format: yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pushTime;
}

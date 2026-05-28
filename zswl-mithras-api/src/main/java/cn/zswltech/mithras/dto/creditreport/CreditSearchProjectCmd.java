package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("征信查询-获取交易结构主体下的客户信息-请求参数")
public class CreditSearchProjectCmd {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("业务类型 PROJ_ESTABLISH:项目立项 PROJ_REVIEW:项目评审 GROUP_CREDIT_ESTABLISH:授信立项 GROUP_CREDIT_REVIEW：授信评审 PAYMENT：付款申请")
    private String bizType;
}

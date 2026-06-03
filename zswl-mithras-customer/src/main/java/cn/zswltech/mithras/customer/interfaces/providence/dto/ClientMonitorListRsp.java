package cn.zswltech.mithras.customer.interfaces.providence.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 15:20
 */
@ApiModel
@Data
public class ClientMonitorListRsp {

    @ApiModelProperty("客户id")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("统一社会信用代码")
    private String uscc;
    @ApiModelProperty("所属部门")
    private String bizDeptName;
    @ApiModelProperty("红灯预警数量")
    private Integer redWarnCount;
    @ApiModelProperty("黄灯预警数量")
    private Integer yellowWarnCount;
    @ApiModelProperty("舆情数量")
    private Integer opCount;
}

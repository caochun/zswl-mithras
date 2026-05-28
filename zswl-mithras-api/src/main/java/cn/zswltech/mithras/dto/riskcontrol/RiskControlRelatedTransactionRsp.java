package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/13 16:09
 */
@ApiModel(value = "风控管理-关联方交易分页查询响应体")
@Data
public class RiskControlRelatedTransactionRsp {

    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value= "关联方类型")
    private String relatedPartyType;
    @ApiModelProperty(value= "关联关系父类型")
    private String parentRelationType;
    @ApiModelProperty(value= "关联关系子类型")
    private String subRelationType;
    @ApiModelProperty(value= "合同编号 ")
    private String contractCode;
    @ApiModelProperty(value = "交易金额")
    private Long transactionAmount;
    @ApiModelProperty(value = "关联关系说明")
    private String description;
    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;
    @ApiModelProperty(value = "交易时间")
    private LocalDate transactionDate;

}

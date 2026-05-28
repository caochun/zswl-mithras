package cn.zswltech.mithras.api.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description mock数据体
 * @since
 */
@Data
@Accessors(chain = true)
@ApiModel("合同基本信息查询-返回体")
public class ContractBaseInfoRSP {
    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户名称")
    private String clientName;
    @ApiModelProperty(value = "客户类型")
    private String clientType;
    @ApiModelProperty(value = "客户类型展示")
    private String clientTypeDisplay;
    @ApiModelProperty(value = "合同id")
    private Long contractId;
    @ApiModelProperty(value = "合同编号")
    private String contractNo;
    @ApiModelProperty(value = "合同金额")
    private Long contractAmount;
    @ApiModelProperty(value = "合同到期日期")
    private LocalDate contractDueDate;
    @ApiModelProperty(value = "合同状态")
    private String contractStatus;
}

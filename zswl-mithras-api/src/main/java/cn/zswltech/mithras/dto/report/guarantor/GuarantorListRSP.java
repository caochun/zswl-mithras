package cn.zswltech.mithras.dto.report.guarantor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-保证表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-保证表返回值")
public class GuarantorListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("保证合同编号")
    private String guaranteContractCode;

    @ApiModelProperty("保证合同编号(简)")
    private String guaranteContractCode2;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户分类")
    private String clientType;

    @ApiModelProperty("身份标识类型")
    private String guarantorIdType;

    @ApiModelProperty("身份标识号码")
    private String guarantorId;

    @ApiModelProperty("客户类型")
    private String clientClass;

    @ApiModelProperty("还款责任金额")
    private Long repayLiabilityAmount;

    @ApiModelProperty("联保标志")
    private String jointGuarantorFlag;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;
}

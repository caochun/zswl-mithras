package cn.zswltech.mithras.dto.report.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 征信报送-质押表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-质押表返回值")
public class PledgeListRSP {

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

    @ApiModelProperty("借据本金")
    private Long applyPaymentAmount;

    @ApiModelProperty("质押合同编号")
    private String pledgeContractCode;

    @ApiModelProperty("质押合同编号(简)")
    private String pledgeContractCode2;

    @ApiModelProperty("出质人类型")
    private String pledgeType;

    @ApiModelProperty("出质人名称")
    private String pledgeName;

    @ApiModelProperty("出质人身份标识类型")
    private String pledgeIdType;

    @ApiModelProperty("出质人身份标识号码")
    private String pledgeId;

    @ApiModelProperty("最高额担保标识（0否，1是）")
    private Integer maxFlag;

    @ApiModelProperty("序号")
    private String sequence;

    @ApiModelProperty("质物种类")
    private String type;

    @ApiModelProperty("质物价值")
    private Long assessedValue;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;
}

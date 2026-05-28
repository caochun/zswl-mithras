package cn.zswltech.mithras.dto.report.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 账户表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-账户表返回值")
public class AccountListRSP {

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

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("租金计算方式")
    private String rentalCalcType;

    @ApiModelProperty("还款频率")
    private String repayRate;

    @ApiModelProperty("借款金额")
    private Long paymentAmount;

    @ApiModelProperty("保证金")
    private Long earnestMoney;

    @ApiModelProperty("借款期限(月)")
    private Integer projLeaseMonthCount;

    @ApiModelProperty("放款日期")
    private LocalDate lendingDate;

    @ApiModelProperty("结清日期")
    private LocalDate closedDate;

    @ApiModelProperty("到期日期")
    private LocalDate expirationDate;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

}

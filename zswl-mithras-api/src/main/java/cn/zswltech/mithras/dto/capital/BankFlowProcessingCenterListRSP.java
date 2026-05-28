package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/11:35
 * @description
 */
@Data
public class BankFlowProcessingCenterListRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     *流水状态
     */
    @ApiModelProperty(value = "流水状态")
    private String flowStatus;

    /**
     * 交易明细编号
     */
    @ApiModelProperty(value = "交易明细编号")
    private String transactionDetailsNumber;

    /**
     * 资金组织
     */
    @ApiModelProperty(value = "资金组织")
    private String financialOrganization;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    /**
     * 开户银行
     */
    @ApiModelProperty(value = "开户银行")
    private String bankName;

    /**
     * 币别
     */
    @ApiModelProperty(value = "币别")
    private String currency;

    /**
     * 交易时间
     */
    @ApiModelProperty(value = "交易时间")
    private String transactionDate;

    /**
     * 摘要
     */
    @ApiModelProperty(value = "摘要")
    private String mainInfo;

    /**
     * 收款金额
     */
    @ApiModelProperty(value = "收款金额")
    private Long collectionAmount;

    /**
     * 付款金额
     */
    @ApiModelProperty(value = "付款金额")
    private Long paymentAmount;

    /**
     * 余额
     */
    @ApiModelProperty(value = "余额")
    private Long depositAmount;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long handingFees;

    /**
     * 对方户名
     */
    @ApiModelProperty(value = "对方户名")
    private String otherName;

    /**
     * 对方账号
     */
    @ApiModelProperty(value = "对方账号")
    private String otherBankAccount;

    /**
     * 对方开户行
     */
    @ApiModelProperty(value = "对方开户行")
    private String otherBankName;

    /**
     * 明细流水号
     */
    @ApiModelProperty(value = "明细流水号")
    private String detailSerialNumber;

    /**
     * 数据来源
     */
    @ApiModelProperty(value = "数据来源")
    private String dataSource;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private String updateTime;

    /**
     * 提示标签【苍穹已删除】
     */
    @ApiModelProperty(value = "提示标签【苍穹已删除】")
    private String promptLabel;

    /**
     * 已核销金额
     */
    @ApiModelProperty(value = "已核销金额")
    private List<BankFlowCenterListDTO> writeOffedAmountList;

    @ApiModelProperty(value = "保融流水id")
    private String cicoBruid;

}

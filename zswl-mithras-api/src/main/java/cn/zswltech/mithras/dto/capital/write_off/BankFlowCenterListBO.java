package cn.zswltech.mithras.dto.capital.write_off;

import cn.zswltech.mithras.dto.capital.BankFlowCenterListDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bigbear
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankFlowCenterListBO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水状态")
    private String flowStatus;

    @ApiModelProperty(value = "交易明细编号")
    private String transactionDetailsNumber;

    @ApiModelProperty(value = "资金组织")
    private String financialOrganization;

    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    @ApiModelProperty(value = "开户银行")
    private String bankName;

    @ApiModelProperty(value = "币别")
    private String currency;

    @ApiModelProperty(value = "交易时间")
    private String transactionDate;

    @ApiModelProperty(value = "摘要")
    private String mainInfo;

    @ApiModelProperty(value = "收款金额")
    private Long collectionAmount;

    @ApiModelProperty(value = "付款金额")
    private Long paymentAmount;

    @ApiModelProperty(value = "余额")
    private Long depositAmount;

    @ApiModelProperty(value = "手续费")
    private Long handingFees;

    @ApiModelProperty(value = "对方户名")
    private String otherName;

    @ApiModelProperty(value = "对方账号")
    private String otherBankAccount;

    @ApiModelProperty(value = "对方开户行")
    private String otherBankName;

    @ApiModelProperty(value = "明细流水号")
    private String detailSerialNumber;

    @ApiModelProperty(value = "数据来源")
    private String dataSource;

    @ApiModelProperty(value = "最后更新时间")
    private String updateTime;

    @ApiModelProperty(value = "提示标签【苍穹已删除】")
    private String promptLabel;

    @ApiModelProperty(value = "剩余可核销金额")
    private Long surplusAmount;

    @ApiModelProperty(value = "保融流水id")
    private String cicoBruid;
}
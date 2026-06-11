package cn.zswltech.mithras.fund.application.financing.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bigbear
 * @date 2024/9/19 11:06
 * @description 获取直融和间融的合同监管情况转换BO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FundPledgeSupervisedBO {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "账户名称")
    private String accountName;

    @ApiModelProperty(value = "账号")
    private String accountNumber;

    @ApiModelProperty(value = "支行名称")
    private String accountBank;

    @ApiModelProperty(value = "是否质押")
    private Boolean isPledge;

    @ApiModelProperty(value = "是否监管")
    private Boolean isSupervise;
}

package cn.zswltech.mithras.dto.contract.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/1
 * @description
 */
@Data
@ApiModel("合同-报价方案-债权转让报价方案请求体")
public class ContractAocPriceModifyREQ {
    @ApiModelProperty(value = "合同报价方案id")
    @NotNull(message = "'合同报价方案id'不能为空")
    private Long id;

    @ApiModelProperty(value = "所属合同id")
    @NotNull(message = "'所属合同id'不能为空")
    private Long contractId;

    @ApiModelProperty(value = "合同金额")
    @NotNull(message = "'合同金额'不能为空")
    private Long contractAmount;

    @ApiModelProperty(value = "转让额度有效期（月）")
    @NotNull(message = "'转让额度有效期'不能为空")
    private Integer aocCreditTerm;

    @ApiModelProperty(value = "额度是否可循环")
    @NotNull(message = "'额度是否可循环'不能为空")
    private Integer creditAmountLoop;

    @ApiModelProperty(value = "保证金")
    @NotNull(message = "'保证金'不能为空")
    private Long earnestMoney;

    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

    @ApiModelProperty(value = "结构化利息列表")
    private List<StructuredInterest> structuredInterestList;

    @ApiModelProperty(value = "转让费率类型。固定利率：fixed、浮动利率：float")
    @NotNull(message = "'转让费率类型'不能为空")
    private String rateType;

    @ApiModelProperty(value = "lpr品种 一年期，五年期")
    @NotBlank(message = "'LPR品种'不能为空")
    private String lprType;

    @ApiModelProperty(value = "LPR利率")
    @NotNull(message = "'LPR利率'不能为空")
    private Integer lprPercent;

    @ApiModelProperty(value = "LPR加点")
    @NotNull(message = "'LPR加点不能为空'")
    private Integer lprAddPercent;

    @ApiModelProperty(value = "转让费率")
    private Integer aocRatePercent;

    @ApiModelProperty(value = "IRR")
//    @NotNull(message = "'IRR'不能为空")
    private Integer irrPercent;

    @ApiModelProperty(value = "还款计算方式")
    @NotBlank(message = "'还款计算方式'不能为空")
    private String repayCalcType;

    /**
     * 利息计算方式
     */
    @NotBlank(message = "利息计算方式不能为空")
    @ApiModelProperty("利息计算方式")
    private String interestWay;

    /**
     * 还款频率。按月，按季，按年，不规则
     */
    @ApiModelProperty(value = "还款频率。按月，按季，按年，不规则")
    @NotNull(message = "'还款频率'不能为空")
    private String repayRate;
}

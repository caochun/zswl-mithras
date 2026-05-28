package cn.zswltech.mithras.dto.app;

import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("保理报价方案表列表-返回体")
public class FactoringPriceDetailRSP extends ListBaseRSP {

    /**
     * 所属立项id
     */
    @ApiModelProperty(value = "所属立项id")
    private Long projEstablishId;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
     * 保理额度有效期（月
     */
    @ApiModelProperty(value = "保理额度有效期（月")
    private Integer factoringCreditTerm;

    /**
     * 额度是否可循环
     */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @ApiModelProperty(value = "保证金")
    private Long earnestMoney;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

    @ApiModelProperty(value = "保理融资比例")
    private Integer factoringFinancingProportion;

    /**
     * 保理费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "保理费率类型。固定利率：fixed、浮动利率：float")
    private String rateType;

    /**
     * 保理费率值。百分之多少
     */
    @ApiModelProperty(value = "保理费率值。百分之多少")
    private Integer factoringRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

    @ApiModelProperty(value = "存续合同列表")
    private List<ContractBaseInfoRSP> contracts;

    /**
     * 还款计算方式。
     */
    @ApiModelProperty(value = "还款计算方式")
    private String rentalCalcType;

    @ApiModelProperty(value = "利息计算方式")
    private String interestWay;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}

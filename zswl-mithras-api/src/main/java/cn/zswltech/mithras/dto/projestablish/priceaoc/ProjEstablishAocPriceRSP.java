package cn.zswltech.mithras.dto.projestablish.priceaoc;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("债权转让报价方案表列表-返回体")
public class ProjEstablishAocPriceRSP extends ListBaseRSP {

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
     * 转让额度有效期（月
     */
    @ApiModelProperty(value = "转让额度有效期（月")
    private Integer aocCreditTerm;

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

    /**
     * 转让费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "转让费率类型。固定利率：fixed、浮动利率：float")
    private String rateType;

    /**
     * 转让费率值。百分之多少
     */
    @ApiModelProperty(value = "转让费率值。百分之多少")
    private Integer aocRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

    /**
     * 还款计算方式
     */
    @ApiModelProperty(value = "还款计算方式")
    private String rentalCalcType;

    @ApiModelProperty(value = "利息计算方式")
    private String interestWay;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}

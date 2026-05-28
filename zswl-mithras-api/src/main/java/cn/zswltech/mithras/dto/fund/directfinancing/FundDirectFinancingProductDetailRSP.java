package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/18 11:24
 */
@Data
@ApiModel("直接融资-产品明细详情-返回体")
public class FundDirectFinancingProductDetailRSP {
    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 证券代码
     */
    @ApiModelProperty(value = "证券代码")
    private String securitiesCode;

    /**
     * 证券简称
     */
    @ApiModelProperty(value = "证券简称")
    private String abbreviation;

    /**
     * 发行金额（万元）
     */
    @ApiModelProperty(value = "发行金额（万元）")
    private Long issuanceAmount;

    /**
     * 分层占比（%）
     */
    @ApiModelProperty(value = "分层占比（%）")
    private Long layeredProportion;

    /**
     * 还本方式
     */
    @ApiModelProperty(value = "还本方式")
    private String repaymentMethod;

    /**
     * 发行利率
     */
    @ApiModelProperty(value = "发行利率")
    private Long issuanceRate;

    /**
     * 年付息次数
     */
    @ApiModelProperty(value = "年付息次数")
    private Long annualPayCount;

    /**
     * 起息日
     */
    @ApiModelProperty(value = "起息日")
    private LocalDate valueDate;

    /**
     * 预计到期日
     */
    @ApiModelProperty(value = "预计到期日")
    private LocalDate expectedExpirationDate;

    /**
     * 剩余本金余额（万元）
     */
    @ApiModelProperty(value = "剩余本金余额（万元）")
    private Long remainingPrincipal;

    /**
     * 评级
     */
    @ApiModelProperty(value = "评级")
    private String rating;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;


    /**
     * FTP收益率
     */
    @ApiModelProperty("FTP收益率")
    private Integer ftpYieldRate;
}

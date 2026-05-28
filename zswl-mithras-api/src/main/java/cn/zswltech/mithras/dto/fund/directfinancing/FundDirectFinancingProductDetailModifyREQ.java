package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-产品明细编辑-请求体")
public class FundDirectFinancingProductDetailModifyREQ {
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 证券代码
     */
    @ApiModelProperty(value = "证券代码")
    @NotNull(message = "证券代码不能为空")
    private String securitiesCode;

    /**
     * 证券简称
     */
    @ApiModelProperty(value = "证券简称")
    @NotNull(message = "证券简称不能为空")
    private String abbreviation;

    /**
     * 发行金额（万元）
     */
    @ApiModelProperty(value = "发行金额（万元）")
    @NotNull(message = "发行金额不能为空")
    private Long issuanceAmount;

    /**
     * 分层占比（%）
     */
    @ApiModelProperty(value = "分层占比（%）")
    @NotNull(message = "分层占比不能为空")
    private Long layeredProportion;

    /**
     * 还本方式
     */
    @ApiModelProperty(value = "还本方式")
    @NotNull(message = "还本方式不能为空")
    private String repaymentMethod;

    /**
     * 发行利率
     */
    @ApiModelProperty(value = "发行利率")
    @NotNull(message = "发行利率不能为空")
    private Long issuanceRate;

    /**
     * 年付息次数
     */
    @ApiModelProperty(value = "年付息次数")
    @NotNull(message = "年付息次数不能为空")
    private Long annualPayCount;

    /**
     * 起息日
     */
    @ApiModelProperty(value = "起息日")
    @NotNull(message = "起息日不能为空")
    private LocalDate valueDate;

    /**
     * 预计到期日
     */
    @ApiModelProperty(value = "预计到期日")
    @NotNull(message = "预计到期日不能为空")
    private LocalDate expectedExpirationDate;

    /**
     * 剩余本金余额（万元）
     */
    @ApiModelProperty(value = "剩余本金余额（万元）")
    @NotNull(message = "剩余本金余额不能为空")
    private String remainingPrincipal;

    /**
     * 评级
     */
    @ApiModelProperty(value = "评级")
    @NotNull(message = "评级不能为空")
    private String rating;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

}

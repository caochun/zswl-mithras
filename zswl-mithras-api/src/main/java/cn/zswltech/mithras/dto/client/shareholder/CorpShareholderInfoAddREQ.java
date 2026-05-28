package cn.zswltech.mithras.dto.client.shareholder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static cn.zswltech.mithras.dto.ApiConst.MONEY_MAX;

/**
 * @author luyi
 */
@Data
@ApiModel("股东信息新增-请求体")
public class CorpShareholderInfoAddREQ {
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
     * 股东类型
     */
    @NotBlank
    @ApiModelProperty("股东类型")
    private String shareholderType;

    /**
     * 股东姓名
     */
    @NotBlank
    @ApiModelProperty("股东姓名")
    private String shareholderName;

    /**
     * 万元
     */

    @ApiModelProperty("认缴金额")
    @Max(value = MONEY_MAX, message = "必须小于或等于1000000000000000")
    private Long paidTotal;

    /**
     * 万元
     */

    @ApiModelProperty("实缴金额")
    @Max(value = MONEY_MAX, message = "必须小于或等于1000000000000000")
    private Long actualPaidTotal;

    /**
     * 出资方式
     */
    @ApiModelProperty("出资方式")
    private String capitalWay;

    /**
     * 出资占比
     */
    @ApiModelProperty("出资占比")
    private Long capitalPercent;

    /**
     * 是否实际控制人
     */
    @ApiModelProperty("是否实际控制人")
    private Boolean realController;
}

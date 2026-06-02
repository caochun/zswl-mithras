package cn.zswltech.mithras.client.service.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author junke
 */
@Data
public class MithrasShareholderInfo {

    /**
     * 股东类型
     */
    @ApiModelProperty("股东类型")
    private String shareholderType;

    /**
     * 股东姓名
     */
    @ApiModelProperty("股东姓名")
    private String shareholderName;

    /**
     *
     */
    @ApiModelProperty("认缴金额")
    private Long paidTotal;


    @ApiModelProperty("实缴金额")
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

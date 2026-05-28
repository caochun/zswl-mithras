package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailShahStorInfoRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String onum;

    /**
     * 股东全称
     */
    @ApiModelProperty(value = "股东全称")
    private String shahFn;

    /**
     * 统一社会信用代码/身份证号
     */
    @ApiModelProperty(value = "统一社会信用代码/身份证号")
    private String shahCertNum;

    /**
     * 股东性质
     */
    @ApiModelProperty(value = "股东性质")
    private String shahCharCode;

    @ApiModelProperty(value = "股东性质Display")
    private String shahCharDisplay;

    /**
     * 股东进入方式
     */
    @ApiModelProperty(value = "股东进入方式")
    private String shahGtoMode;

    /**
     * 变更前股东出资金额(万元)
     */
    @ApiModelProperty(value = "变更前股东出资金额(万元)")
    private Double altrBefShahFndrAmt;

    /**
     * 变更前出资比例
     */
    @ApiModelProperty(value = "变更前出资比例")
    private Double altrBefFndrRati;

    /**
     * 股权转让标志
     */
    @ApiModelProperty(value = "股权转让标志")
    private String storTranFlag;

    @ApiModelProperty(value = "股权转让标志Display")
    private String storTranFlagDisplay;

    /**
     * 增减资金金额(万元)
     */
    @ApiModelProperty(value = "增减资金金额(万元)")
    private Double iordCptlAmt;

    /**
     * 最新出资金额(万元)
     */
    @ApiModelProperty(value = "最新出资金额(万元)")
    private Double lastFndrAmt;

    /**
     * 最新持股比例
     */
    @ApiModelProperty(value = "最新持股比例")
    private Double lastHoldRati;

    /**
     * 批复文件号
     */
    @ApiModelProperty(value = "批复文件号")
    private String aprvFileNum;

    /**
     * 批复时间
     */
    @ApiModelProperty(value = "批复时间")
    private LocalDate aprvTime;
}

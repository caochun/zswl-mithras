package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @date 2025/4/22
 * @description 股东股权信息一览表-股东变更记录
 */
@Data
public class AssociationShahChangeInfoModel extends AssociationReportBaseModel {
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty(value = "行号 | 同一批次数据从1开始递增")
    private Integer rowNum;

    /**
     * 企业统一社会信用代码
     */
    @ApiModelProperty(value = "企业统一社会信用代码")
    private String unifSociCredCode;

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
     * 股东证件号码
     */
    @ApiModelProperty(value = "股东证件号码")
    private String shahCertNum;

    /**
     * 股东性质
     */
    @ApiModelProperty(value = "股东性质")
    private String shahCharCode;

    /**
     * 股东进入方式
     */
    @ApiModelProperty(value = "股东进入方式")
    private String shahGtoMode;

    /**
     * 变更前股东出资金额(万元)
     */
    @ApiModelProperty(value = "变更前股东出资金额(万元)")
    private BigDecimal altrBefShahFndrAmt;

    /**
     * 变更前持股比例
     */
    @ApiModelProperty(value = "变更前持股比例")
    private BigDecimal altrBefFndrRati;

    /**
     * 股权转让标志
     */
    @ApiModelProperty(value = "股权转让标志")
    private String storTranFlag;

    /**
     * 增减资金金额(万元)
     */
    @ApiModelProperty(value = "增减资金金额(万元)")
    private BigDecimal iordCptlAmt;

    /**
     * 变更后股东出资金额(万元)
     */
    @ApiModelProperty(value = "变更后股东出资金额(万元)")
    private BigDecimal altrShahFndrAmt;

    /**
     * 变更后持股比例
     */
    @ApiModelProperty(value = "变更后持股比例")
    private BigDecimal altrHoldRati;

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

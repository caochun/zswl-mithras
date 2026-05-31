package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @date 2025/4/22
 * @description 业务情况表
 */
@Data
public class AssociationBusinessSituationModel extends AssociationReportBaseModel {
    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty("行号")
    private Integer rowNum;

    /**
     * 企业统一社会信用代码
     */
    @ApiModelProperty("企业统一社会信用代码")
    private String unifSociCredCode;

    @ApiModelProperty("总收入_期初数（万元）")
    private BigDecimal totIncmAbop;
    
    @ApiModelProperty("总收入_本期发生额（万元）")
    private BigDecimal totIncmAotc;
    
    @ApiModelProperty("总收入_期末数（万元）")
    private BigDecimal totIncmAeop;

    @ApiModelProperty("经营租赁业务收入_期初数（万元）")
    private BigDecimal operLeasBusiIncmAbop;
    
    @ApiModelProperty("经营租赁业务收入_本期发生额（万元）")
    private BigDecimal operLeasBusiIncmAotc;

    @ApiModelProperty("经营租赁业务收入_期末数（万元）")
    private BigDecimal operLeasBusiIncmAeop;

    @ApiModelProperty("融资租赁业务收入_期初数（万元）")
    private BigDecimal fnlBusiIncmAbop;

    @ApiModelProperty("融资租赁业务收入_本期发生额（万元）")
    private BigDecimal fnlBusiIncmAotc;

    @ApiModelProperty("融资租赁业务收入_期末数（万元）")
    private BigDecimal fnlBusiIncmAeop;
    
    @ApiModelProperty("利息收入_期初数（万元）")
    private BigDecimal intrIncmAbop;
    
    @ApiModelProperty("利息收入_本期发生额（万元）")
    private BigDecimal intrIncmAotc;

    @ApiModelProperty("利息收入_期末数（万元）")
    private BigDecimal intrIncmAeop;
    
    @ApiModelProperty("费用收入_期初数（万元）")
    private BigDecimal feeIncmAbop;

    @ApiModelProperty("费用收入_本期发生额（万元）")
    private BigDecimal feeIncmAotc;
    
    @ApiModelProperty("费用收入_期末数（万元）")
    private BigDecimal feeIncmAeop;
    
    @ApiModelProperty("其他收入_期初数（万元）")
    private BigDecimal othIncmAbop;

    @ApiModelProperty("其他收入_本期发生额（万元）")
    private BigDecimal othIncmAotc;

    @ApiModelProperty("其他收入_期末数（万元）")
    private BigDecimal othIncmAeop;

    @ApiModelProperty("租赁资产_期初数（万元）")
    private BigDecimal leasAstAbop;

    @ApiModelProperty("租赁资产_本期发生额（万元）")
    private BigDecimal leasAstAotc;

    @ApiModelProperty("租赁资产_期末数（万元）")
    private BigDecimal leasAstAeop;

    @ApiModelProperty("经营性租赁资产_期初数（万元）")
    private BigDecimal operLeasAstAbop;

    @ApiModelProperty("经营性租赁资产_本期发生额（万元）")
    private BigDecimal operLeasAstAotc;
    
    @ApiModelProperty("经营性租赁资产_期末数（万元）")
    private BigDecimal operLeasAstAeop;

    @ApiModelProperty("融资租赁资产_期初数（万元）")
    private BigDecimal finLeasAstAbop;
    
    @ApiModelProperty("融资租赁资产_本期发生额（万元）")
    private BigDecimal finLeasAstAotc;
    
    @ApiModelProperty("融资租赁资产_期末数（万元）")
    private BigDecimal finLeasAstAeop;
    
    @ApiModelProperty("直接租赁资产_期初数（万元）")
    private BigDecimal dirtLeasAstAbop;

    @ApiModelProperty("直接租赁资产_本期发生额（万元）")
    private BigDecimal dirtLeasAstAotc;
    
    @ApiModelProperty("直接租赁资产_期末数（万元）")
    private BigDecimal dirtLeasAstAeop;

    @ApiModelProperty("售后回租资产_期初数（万元）")
    private BigDecimal slbkAstAbop;
    
    @ApiModelProperty("售后回租资产_本期发生额（万元）")
    private BigDecimal slbkAstAotc;

    @ApiModelProperty("售后回租资产_期末数（万元）")
    private BigDecimal slbkAstAeop;
    
    @ApiModelProperty("跨省融资租赁资产余额_期初数（万元）")
    private BigDecimal iprvFnlAstBalAbop;
    
    @ApiModelProperty("跨省融资租赁资产余额_本期发生额（万元）")
    private BigDecimal iprvFnlAstBalAotc;
    
    @ApiModelProperty("跨省融资租赁资产余额_期末数（万元）")
    private BigDecimal iprvFnlAstBalAeop;
    
    @ApiModelProperty("跨省售后回租资产余额_期初数（万元）")
    private BigDecimal iprvSlbkAstBalAbop;

    @ApiModelProperty("跨省售后回租资产余额_本期发生额（万元）")
    private BigDecimal iprvSlbkAstBalAotc;

    @ApiModelProperty("跨省售后回租资产余额_期末数（万元）")
    private BigDecimal iprvSlbkAstBalAeop;

    @ApiModelProperty("融资租赁投放额_期初数（万元）")
    private BigDecimal fnlRelsAbop;
    
    @ApiModelProperty("融资租赁投放额_本期发生额（万元）")
    private BigDecimal fnlRelsAotc;
    
    @ApiModelProperty("融资租赁投放额_期末数（万元）")
    private BigDecimal fnlRelsAeop;
    
    @ApiModelProperty("直接租赁投放额_期初数（万元）")
    private BigDecimal dirtLeasRelsAbop;
    
    @ApiModelProperty("直接租赁投放额_本期发生额（万元）")
    private BigDecimal dirtLeasRelsAotc;
    
    @ApiModelProperty("直接租赁投放额_期末数（万元）")
    private BigDecimal dirtLeasRelsAeop;
    
    @ApiModelProperty("售后回租投放额_期初数（万元）")
    private BigDecimal slbkRelsAbop;
    
    @ApiModelProperty("售后回租投放额_本期发生额（万元）")
    private BigDecimal slbkRelsAotc;
    
    @ApiModelProperty("售后回租投放额_期末数（万元）")
    private BigDecimal slbkRelsAeop;
    
    @ApiModelProperty("固定收益类证券投资余额_期初数（万元）")
    private BigDecimal fixPayfScrIvsmAbop;

    @ApiModelProperty("固定收益类证券投资余额_本期发生额（万元）")
    private BigDecimal fixPayfScrIvsmAotc;
    
    @ApiModelProperty("固定收益类证券投资_期末数（万元）")
    private BigDecimal fixPayfScrIvsmAeop;

    @ApiModelProperty("国债余额_期初数（万元）")
    private BigDecimal treaAbop;
    
    @ApiModelProperty("国债余额_本期发生额（万元）")
    private BigDecimal treaAotc;
    
    @ApiModelProperty("国债余额_期末数（万元）")
    private BigDecimal treaAeop;
    
    @ApiModelProperty("资产减值损失准备_期初数（万元）")
    private BigDecimal ipoaLossAbop;
    
    @ApiModelProperty("资产减值损失准备_本期发生额（万元）")
    private BigDecimal ipoaLossAotc;
    
    @ApiModelProperty("资产减值损失准备_期末数（万元）")
    private BigDecimal ipoaLossAeop;

}

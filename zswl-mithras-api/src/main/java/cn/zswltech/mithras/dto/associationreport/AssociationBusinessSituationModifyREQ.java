package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
/**
 * @description 业务情况表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("业务情况表编辑-请求体")
public class AssociationBusinessSituationModifyREQ {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

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
    * 总收入_期初数（万元）
    */
    @ApiModelProperty(value = "总收入_期初数（万元）")
    private BigDecimal totIncmAbop;

    /**
    * 总收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "总收入_本期发生额（万元）")
    private BigDecimal totIncmAotc;

    /**
    * 总收入_期末数（万元）
    */
    @ApiModelProperty(value = "总收入_期末数（万元）")
    private BigDecimal totIncmAeop;

    /**
    * 经营租赁业务收入_期初数（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_期初数（万元）")
    private BigDecimal operLeasBusiIncmAbop;

    /**
    * 经营租赁业务收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_本期发生额（万元）")
    private BigDecimal operLeasBusiIncmAotc;

    /**
    * 经营租赁业务收入_期末数（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_期末数（万元）")
    private BigDecimal operLeasBusiIncmAeop;

    /**
    * 融资租赁业务收入_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_期初数（万元）")
    private BigDecimal fnlBusiIncmAbop;

    /**
    * 融资租赁业务收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_本期发生额（万元）")
    private BigDecimal fnlBusiIncmAotc;

    /**
    * 融资租赁业务收入_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_期末数（万元）")
    private BigDecimal fnlBusiIncmAeop;

    /**
    * 利息收入_期初数（万元）
    */
    @ApiModelProperty(value = "利息收入_期初数（万元）")
    private BigDecimal intrIncmAbop;

    /**
    * 利息收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "利息收入_本期发生额（万元）")
    private BigDecimal intrIncmAotc;

    /**
    * 利息收入_期末数（万元）
    */
    @ApiModelProperty(value = "利息收入_期末数（万元）")
    private BigDecimal intrIncmAeop;

    /**
    * 费用收入_期初数（万元）
    */
    @ApiModelProperty(value = "费用收入_期初数（万元）")
    private BigDecimal feeIncmAbop;

    /**
    * 费用收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "费用收入_本期发生额（万元）")
    private BigDecimal feeIncmAotc;

    /**
    * 费用收入_期末数（万元）
    */
    @ApiModelProperty(value = "费用收入_期末数（万元）")
    private BigDecimal feeIncmAeop;

    /**
    * 其他收入_期初数（万元）
    */
    @ApiModelProperty(value = "其他收入_期初数（万元）")
    private BigDecimal othIncmAbop;

    /**
    * 其他收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "其他收入_本期发生额（万元）")
    private BigDecimal othIncmAotc;

    /**
    * 其他收入_期末数（万元）
    */
    @ApiModelProperty(value = "其他收入_期末数（万元）")
    private BigDecimal othIncmAeop;

    /**
    * 租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "租赁资产_期初数（万元）")
    private BigDecimal leasAstAbop;

    /**
    * 租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "租赁资产_本期发生额（万元）")
    private BigDecimal leasAstAotc;

    /**
    * 租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "租赁资产_期末数（万元）")
    private BigDecimal leasAstAeop;

    /**
    * 经营性租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_期初数（万元）")
    private BigDecimal operLeasAstAbop;

    /**
    * 经营性租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_本期发生额（万元）")
    private BigDecimal operLeasAstAotc;

    /**
    * 经营性租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_期末数（万元）")
    private BigDecimal operLeasAstAeop;

    /**
    * 融资租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_期初数（万元）")
    private BigDecimal finLeasAstAbop;

    /**
    * 融资租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_本期发生额（万元）")
    private BigDecimal finLeasAstAotc;

    /**
    * 融资租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_期末数（万元）")
    private BigDecimal finLeasAstAeop;

    /**
    * 直接租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_期初数（万元）")
    private BigDecimal dirtLeasAstAbop;

    /**
    * 直接租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_本期发生额（万元）")
    private BigDecimal dirtLeasAstAotc;

    /**
    * 直接租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_期末数（万元）")
    private BigDecimal dirtLeasAstAeop;

    /**
    * 售后回租资产_期初数（万元）
    */
    @ApiModelProperty(value = "售后回租资产_期初数（万元）")
    private BigDecimal slbkAstAbop;

    /**
    * 售后回租资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "售后回租资产_本期发生额（万元）")
    private BigDecimal slbkAstAotc;

    /**
    * 售后回租资产_期末数（万元）
    */
    @ApiModelProperty(value = "售后回租资产_期末数（万元）")
    private BigDecimal slbkAstAeop;

    /**
    * 跨省融资租赁资产余额_期初数（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_期初数（万元）")
    private BigDecimal iprvFnlAstBalAbop;

    /**
    * 跨省融资租赁资产余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_本期发生额（万元）")
    private BigDecimal iprvFnlAstBalAotc;

    /**
    * 跨省融资租赁资产余额_期末数（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_期末数（万元）")
    private BigDecimal iprvFnlAstBalAeop;

    /**
    * 跨省售后回租资产余额_期初数（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_期初数（万元）")
    private BigDecimal iprvSlbkAstBalAbop;

    /**
    * 跨省售后回租资产余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_本期发生额（万元）")
    private BigDecimal iprvSlbkAstBalAotc;

    /**
    * 跨省售后回租资产余额_期末数（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_期末数（万元）")
    private BigDecimal iprvSlbkAstBalAeop;

    /**
    * 融资租赁投放额_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_期初数（万元）")
    private BigDecimal fnlRelsAbop;

    /**
    * 融资租赁投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_本期发生额（万元）")
    private BigDecimal fnlRelsAotc;

    /**
    * 融资租赁投放额_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_期末数（万元）")
    private BigDecimal fnlRelsAeop;

    /**
    * 直接租赁投放额_期初数（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_期初数（万元）")
    private BigDecimal dirtLeasRelsAbop;

    /**
    * 直接租赁投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_本期发生额（万元）")
    private BigDecimal dirtLeasRelsAotc;

    /**
    * 直接租赁投放额_期末数（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_期末数（万元）")
    private BigDecimal dirtLeasRelsAeop;

    /**
    * 售后回租投放额_期初数（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_期初数（万元）")
    private BigDecimal slbkRelsAbop;

    /**
    * 售后回租投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_本期发生额（万元）")
    private BigDecimal slbkRelsAotc;

    /**
    * 售后回租投放额_期末数（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_期末数（万元）")
    private BigDecimal slbkRelsAeop;

    /**
    * 固定收益类证券投资余额_期初数（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资余额_期初数（万元）")
    private BigDecimal fixPayfScrIvsmAbop;

    /**
    * 固定收益类证券投资余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资余额_本期发生额（万元）")
    private BigDecimal fixPayfScrIvsmAotc;

    /**
    * 固定收益类证券投资_期末数（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资_期末数（万元）")
    private BigDecimal fixPayfScrIvsmAeop;

    /**
    * 国债余额_期初数（万元）
    */
    @ApiModelProperty(value = "国债余额_期初数（万元）")
    private BigDecimal treaAbop;

    /**
    * 国债余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "国债余额_本期发生额（万元）")
    private BigDecimal treaAotc;

    /**
    * 国债余额_期末数（万元）
    */
    @ApiModelProperty(value = "国债余额_期末数（万元）")
    private BigDecimal treaAeop;

    /**
    * 资产减值损失准备_期初数（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_期初数（万元）")
    private BigDecimal ipoaLossAbop;

    /**
    * 资产减值损失准备_本期发生额（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_本期发生额（万元）")
    private BigDecimal ipoaLossAotc;

    /**
    * 资产减值损失准备_期末数（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_期末数（万元）")
    private BigDecimal ipoaLossAeop;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;

}

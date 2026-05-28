package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 业务情况表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("业务情况表列表-返回体")
public class AssociationBusinessSituationListRSP {

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
    private Double totIncmAbop;

    /**
    * 总收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "总收入_本期发生额（万元）")
    private Double totIncmAotc;

    /**
    * 总收入_期末数（万元）
    */
    @ApiModelProperty(value = "总收入_期末数（万元）")
    private Double totIncmAeop;

    /**
    * 经营租赁业务收入_期初数（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_期初数（万元）")
    private Double operLeasBusiIncmAbop;

    /**
    * 经营租赁业务收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_本期发生额（万元）")
    private Double operLeasBusiIncmAotc;

    /**
    * 经营租赁业务收入_期末数（万元）
    */
    @ApiModelProperty(value = "经营租赁业务收入_期末数（万元）")
    private Double operLeasBusiIncmAeop;

    /**
    * 融资租赁业务收入_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_期初数（万元）")
    private Double fnlBusiIncmAbop;

    /**
    * 融资租赁业务收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_本期发生额（万元）")
    private Double fnlBusiIncmAotc;

    /**
    * 融资租赁业务收入_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁业务收入_期末数（万元）")
    private Double fnlBusiIncmAeop;

    /**
    * 利息收入_期初数（万元）
    */
    @ApiModelProperty(value = "利息收入_期初数（万元）")
    private Double intrIncmAbop;

    /**
    * 利息收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "利息收入_本期发生额（万元）")
    private Double intrIncmAotc;

    /**
    * 利息收入_期末数（万元）
    */
    @ApiModelProperty(value = "利息收入_期末数（万元）")
    private Double intrIncmAeop;

    /**
    * 费用收入_期初数（万元）
    */
    @ApiModelProperty(value = "费用收入_期初数（万元）")
    private Double feeIncmAbop;

    /**
    * 费用收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "费用收入_本期发生额（万元）")
    private Double feeIncmAotc;

    /**
    * 费用收入_期末数（万元）
    */
    @ApiModelProperty(value = "费用收入_期末数（万元）")
    private Double feeIncmAeop;

    /**
    * 其他收入_期初数（万元）
    */
    @ApiModelProperty(value = "其他收入_期初数（万元）")
    private Double othIncmAbop;

    /**
    * 其他收入_本期发生额（万元）
    */
    @ApiModelProperty(value = "其他收入_本期发生额（万元）")
    private Double othIncmAotc;

    /**
    * 其他收入_期末数（万元）
    */
    @ApiModelProperty(value = "其他收入_期末数（万元）")
    private Double othIncmAeop;

    /**
    * 租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "租赁资产_期初数（万元）")
    private Double leasAstAbop;

    /**
    * 租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "租赁资产_本期发生额（万元）")
    private Double leasAstAotc;

    /**
    * 租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "租赁资产_期末数（万元）")
    private Double leasAstAeop;

    /**
    * 经营性租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_期初数（万元）")
    private Double operLeasAstAbop;

    /**
    * 经营性租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_本期发生额（万元）")
    private Double operLeasAstAotc;

    /**
    * 经营性租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "经营性租赁资产_期末数（万元）")
    private Double operLeasAstAeop;

    /**
    * 融资租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_期初数（万元）")
    private Double finLeasAstAbop;

    /**
    * 融资租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_本期发生额（万元）")
    private Double finLeasAstAotc;

    /**
    * 融资租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁资产_期末数（万元）")
    private Double finLeasAstAeop;

    /**
    * 直接租赁资产_期初数（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_期初数（万元）")
    private Double dirtLeasAstAbop;

    /**
    * 直接租赁资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_本期发生额（万元）")
    private Double dirtLeasAstAotc;

    /**
    * 直接租赁资产_期末数（万元）
    */
    @ApiModelProperty(value = "直接租赁资产_期末数（万元）")
    private Double dirtLeasAstAeop;

    /**
    * 售后回租资产_期初数（万元）
    */
    @ApiModelProperty(value = "售后回租资产_期初数（万元）")
    private Double slbkAstAbop;

    /**
    * 售后回租资产_本期发生额（万元）
    */
    @ApiModelProperty(value = "售后回租资产_本期发生额（万元）")
    private Double slbkAstAotc;

    /**
    * 售后回租资产_期末数（万元）
    */
    @ApiModelProperty(value = "售后回租资产_期末数（万元）")
    private Double slbkAstAeop;

    /**
    * 跨省融资租赁资产余额_期初数（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_期初数（万元）")
    private Double iprvFnlAstBalAbop;

    /**
    * 跨省融资租赁资产余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_本期发生额（万元）")
    private Double iprvFnlAstBalAotc;

    /**
    * 跨省融资租赁资产余额_期末数（万元）
    */
    @ApiModelProperty(value = "跨省融资租赁资产余额_期末数（万元）")
    private Double iprvFnlAstBalAeop;

    /**
    * 跨省售后回租资产余额_期初数（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_期初数（万元）")
    private Double iprvSlbkAstBalAbop;

    /**
    * 跨省售后回租资产余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_本期发生额（万元）")
    private Double iprvSlbkAstBalAotc;

    /**
    * 跨省售后回租资产余额_期末数（万元）
    */
    @ApiModelProperty(value = "跨省售后回租资产余额_期末数（万元）")
    private Double iprvSlbkAstBalAeop;

    /**
    * 融资租赁投放额_期初数（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_期初数（万元）")
    private Double fnlRelsAbop;

    /**
    * 融资租赁投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_本期发生额（万元）")
    private Double fnlRelsAotc;

    /**
    * 融资租赁投放额_期末数（万元）
    */
    @ApiModelProperty(value = "融资租赁投放额_期末数（万元）")
    private Double fnlRelsAeop;

    /**
    * 直接租赁投放额_期初数（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_期初数（万元）")
    private Double dirtLeasRelsAbop;

    /**
    * 直接租赁投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_本期发生额（万元）")
    private Double dirtLeasRelsAotc;

    /**
    * 直接租赁投放额_期末数（万元）
    */
    @ApiModelProperty(value = "直接租赁投放额_期末数（万元）")
    private Double dirtLeasRelsAeop;

    /**
    * 售后回租投放额_期初数（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_期初数（万元）")
    private Double slbkRelsAbop;

    /**
    * 售后回租投放额_本期发生额（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_本期发生额（万元）")
    private Double slbkRelsAotc;

    /**
    * 售后回租投放额_期末数（万元）
    */
    @ApiModelProperty(value = "售后回租投放额_期末数（万元）")
    private Double slbkRelsAeop;

    /**
    * 固定收益类证券投资余额_期初数（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资余额_期初数（万元）")
    private Double fixPayfScrIvsmAbop;

    /**
    * 固定收益类证券投资余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资余额_本期发生额（万元）")
    private Double fixPayfScrIvsmAotc;

    /**
    * 固定收益类证券投资_期末数（万元）
    */
    @ApiModelProperty(value = "固定收益类证券投资_期末数（万元）")
    private Double fixPayfScrIvsmAeop;

    /**
    * 国债余额_期初数（万元）
    */
    @ApiModelProperty(value = "国债余额_期初数（万元）")
    private Double treaAbop;

    /**
    * 国债余额_本期发生额（万元）
    */
    @ApiModelProperty(value = "国债余额_本期发生额（万元）")
    private Double treaAotc;

    /**
    * 国债余额_期末数（万元）
    */
    @ApiModelProperty(value = "国债余额_期末数（万元）")
    private Double treaAeop;

    /**
    * 资产减值损失准备_期初数（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_期初数（万元）")
    private Double ipoaLossAbop;

    /**
    * 资产减值损失准备_本期发生额（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_本期发生额（万元）")
    private Double ipoaLossAotc;

    /**
    * 资产减值损失准备_期末数（万元）
    */
    @ApiModelProperty(value = "资产减值损失准备_期末数（万元）")
    private Double ipoaLossAeop;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;

    /**
    * 报表周期 | 格式：yyyymm
    */
    @ApiModelProperty(value = "报表周期 | 格式：yyyymm")
    private String reportInstancePeriod;

    /**
    * 批次号 | 从0000递增，最大9999
    */
    @ApiModelProperty(value = "批次号 | 从0000递增，最大9999")
    private String batchNo;

    /**
    * 版本号 | 格式：报表周期版本流水号
    */
    @ApiModelProperty(value = "版本号 | 格式：报表周期版本流水号")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @ApiModelProperty(value = "操作标识 | insert/update")
    private String op;

    /**
    * 上报时间 | 文件上传时间
    */
    @ApiModelProperty(value = "上报时间 | 文件上传时间")
    private LocalDateTime reportTime;

    /**
    * 写入时间 | 数据库记录时间
    */
    @ApiModelProperty(value = "写入时间 | 数据库记录时间")
    private LocalDateTime writeTime;

}

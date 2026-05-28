package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 公司利润表数据表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("公司利润表数据表列表-返回体")
public class AssociationCompanyProfitStatementListRSP {

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
    * 主营业务收入_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务收入_本季发生额(元)")
    private Double mainBusiIncmActm;

    /**
    * 主营业务成本_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务成本_本季发生额(元)")
    private Double mainBusiCostActm;

    /**
    * 主营业务税金及附加_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_本季发生额(元)")
    private Double mainBusiTaxAddActm;

    /**
    * 主营业务利润_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务利润_本季发生额(元)")
    private Double mainBusiProfActm;

    /**
    * 其他业务利润_本季发生额(元)
    */
    @ApiModelProperty(value = "其他业务利润_本季发生额(元)")
    private Double othBusiProfActm;

    /**
    * 营业费用_本季发生额(元)
    */
    @ApiModelProperty(value = "营业费用_本季发生额(元)")
    private Double busiFeeActm;

    /**
    * 管理费用_本季发生额(元)
    */
    @ApiModelProperty(value = "管理费用_本季发生额(元)")
    private Double magFeeActm;

    /**
    * 财务费用_本季发生额(元)
    */
    @ApiModelProperty(value = "财务费用_本季发生额(元)")
    private Double finFeeActm;

    /**
    * 资产减值损失_本季发生额(元)
    */
    @ApiModelProperty(value = "资产减值损失_本季发生额(元)")
    private Double ipoaLossActm;

    /**
    * 信用减值损失_本季发生额(元)
    */
    @ApiModelProperty(value = "信用减值损失_本季发生额(元)")
    private Double credDecrLossActm;

    /**
    * 营业利润_本季发生额(元)
    */
    @ApiModelProperty(value = "营业利润_本季发生额(元)")
    private Double busiProfActm;

    /**
    * 投资收益_本季发生额(元)
    */
    @ApiModelProperty(value = "投资收益_本季发生额(元)")
    private Double ivsmPayfActm;

    /**
    * 营业外收入_本季发生额(元)
    */
    @ApiModelProperty(value = "营业外收入_本季发生额(元)")
    private Double noprIncmActm;

    /**
    * 营业外支出_本季发生额(元)
    */
    @ApiModelProperty(value = "营业外支出_本季发生额(元)")
    private Double noprPayActm;

    /**
    * 利润总额_本季发生额(元)
    */
    @ApiModelProperty(value = "利润总额_本季发生额(元)")
    private Double profGamtActm;

    /**
    * 所得税费用_本季发生额(元)
    */
    @ApiModelProperty(value = "所得税费用_本季发生额(元)")
    private Double inctFeeActm;

    /**
    * 净利润_本季发生额(元)
    */
    @ApiModelProperty(value = "净利润_本季发生额(元)")
    private Double netProfActm;

    /**
    * 主营业务收入_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务收入_本年累计(元)")
    private Double mainBusiIncmTyag;

    /**
    * 主营业务成本_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务成本_本年累计(元)")
    private Double mainBusiCostTyag;

    /**
    * 主营业务税金及附加_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_本年累计(元)")
    private Double mainBusiTaxAddTyag;

    /**
    * 主营业务利润_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务利润_本年累计(元)")
    private Double mainBusiProfTyag;

    /**
    * 其他业务利润_本年累计(元)
    */
    @ApiModelProperty(value = "其他业务利润_本年累计(元)")
    private Double othBusiProfTyag;

    /**
    * 营业费用_本年累计(元)
    */
    @ApiModelProperty(value = "营业费用_本年累计(元)")
    private Double busiFeeTyag;

    /**
    * 管理费用_本年累计(元)
    */
    @ApiModelProperty(value = "管理费用_本年累计(元)")
    private Double magFeeTyag;

    /**
    * 财务费用_本年累计(元)
    */
    @ApiModelProperty(value = "财务费用_本年累计(元)")
    private Double finFeeTyag;

    /**
    * 资产减值损失_本年累计(元)
    */
    @ApiModelProperty(value = "资产减值损失_本年累计(元)")
    private Double ipoaLossTyag;

    /**
    * 信用减值损失_本年累计(元)
    */
    @ApiModelProperty(value = "信用减值损失_本年累计(元)")
    private Double credDecrLossTyag;

    /**
    * 营业利润_本年累计(元)
    */
    @ApiModelProperty(value = "营业利润_本年累计(元)")
    private Double busiProfTyag;

    /**
    * 投资收益_本年累计(元)
    */
    @ApiModelProperty(value = "投资收益_本年累计(元)")
    private Double ivsmPayfTyag;

    /**
    * 营业外收入_本年累计(元)
    */
    @ApiModelProperty(value = "营业外收入_本年累计(元)")
    private Double noprIncmTyag;

    /**
    * 营业外支出_本年累计(元)
    */
    @ApiModelProperty(value = "营业外支出_本年累计(元)")
    private Double noprPayTyag;

    /**
    * 利润总额_本年累计(元)
    */
    @ApiModelProperty(value = "利润总额_本年累计(元)")
    private Double profGamtTyag;

    /**
    * 所得税费用_本年累计(元)
    */
    @ApiModelProperty(value = "所得税费用_本年累计(元)")
    private Double inctFeeTyag;

    /**
    * 净利润_本年累计(元)
    */
    @ApiModelProperty(value = "净利润_本年累计(元)")
    private Double netProfTyag;

    /**
    * 主营业务收入_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务收入_去年同期(元)")
    private Double mainBusiIncmCply;

    /**
    * 主营业务成本_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务成本_去年同期(元)")
    private Double mainBusiCostCply;

    /**
    * 主营业务税金及附加_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_去年同期(元)")
    private Double mainBusiTaxAddCply;

    /**
    * 主营业务利润_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务利润_去年同期(元)")
    private Double mainBusiProfCply;

    /**
    * 其他业务利润_去年同期(元)
    */
    @ApiModelProperty(value = "其他业务利润_去年同期(元)")
    private Double othBusiProfCply;

    /**
    * 营业费用_去年同期(元)
    */
    @ApiModelProperty(value = "营业费用_去年同期(元)")
    private Double busiFeeCply;

    /**
    * 管理费用_去年同期(元)
    */
    @ApiModelProperty(value = "管理费用_去年同期(元)")
    private Double magFeeCply;

    /**
    * 财务费用_去年同期(元)
    */
    @ApiModelProperty(value = "财务费用_去年同期(元)")
    private Double finFeeCply;

    /**
    * 资产减值损失_去年同期(元)
    */
    @ApiModelProperty(value = "资产减值损失_去年同期(元)")
    private Double ipoaLossCply;

    /**
    * 信用减值损失_去年同期(元)
    */
    @ApiModelProperty(value = "信用减值损失_去年同期(元)")
    private Double credDecrLossCply;

    /**
    * 营业利润_去年同期(元)
    */
    @ApiModelProperty(value = "营业利润_去年同期(元)")
    private Double busiProfCply;

    /**
    * 投资收益_去年同期(元)
    */
    @ApiModelProperty(value = "投资收益_去年同期(元)")
    private Double ivsmPayfCply;

    /**
    * 营业外收入_去年同期(元)
    */
    @ApiModelProperty(value = "营业外收入_去年同期(元)")
    private Double noprIncmCply;

    /**
    * 营业外支出_去年同期(元)
    */
    @ApiModelProperty(value = "营业外支出_去年同期(元)")
    private Double noprPayCply;

    /**
    * 利润总额_去年同期(元)
    */
    @ApiModelProperty(value = "利润总额_去年同期(元)")
    private Double profGamtCply;

    /**
    * 所得税费用_去年同期(元)
    */
    @ApiModelProperty(value = "所得税费用_去年同期(元)")
    private Double inctFeeCply;

    /**
    * 净利润_去年同期(元)
    */
    @ApiModelProperty(value = "净利润_去年同期(元)")
    private Double netProfCply;

    /**
    * 报表实例id | uuid
    */
    @ApiModelProperty(value = "报表实例id | uuid")
    private String reportInstanceId;

    /**
    * 报表周期 | yyyyqq格式
    */
    @ApiModelProperty(value = "报表周期 | yyyyqq格式")
    private String reportInstancePeriod;

    /**
    * 批次号 | 0000-9999
    */
    @ApiModelProperty(value = "批次号 | 0000-9999")
    private String batchNo;

    /**
    * 版本号 | 周期版本
    */
    @ApiModelProperty(value = "版本号 | 周期版本")
    private String version;

    /**
    * 操作标识 | insert/update
    */
    @ApiModelProperty(value = "操作标识 | insert/update")
    private String op;

    /**
    * 上报时间
    */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportTime;

    /**
    * 入库时间
    */
    @ApiModelProperty(value = "入库时间")
    private LocalDateTime writeTime;

}

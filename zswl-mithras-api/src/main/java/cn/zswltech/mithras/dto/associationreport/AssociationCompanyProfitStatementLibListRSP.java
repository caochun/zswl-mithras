package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 公司利润表数据表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("公司利润表数据表(流程节点记录版本表)列表-返回体")
public class AssociationCompanyProfitStatementLibListRSP {

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
    private BigDecimal mainBusiIncmActm;

    /**
    * 主营业务成本_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务成本_本季发生额(元)")
    private BigDecimal mainBusiCostActm;

    /**
    * 主营业务税金及附加_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_本季发生额(元)")
    private BigDecimal mainBusiTaxAddActm;

    /**
    * 主营业务利润_本季发生额(元)
    */
    @ApiModelProperty(value = "主营业务利润_本季发生额(元)")
    private BigDecimal mainBusiProfActm;

    /**
    * 其他业务利润_本季发生额(元)
    */
    @ApiModelProperty(value = "其他业务利润_本季发生额(元)")
    private BigDecimal othBusiProfActm;

    /**
    * 营业费用_本季发生额(元)
    */
    @ApiModelProperty(value = "营业费用_本季发生额(元)")
    private BigDecimal busiFeeActm;

    /**
    * 管理费用_本季发生额(元)
    */
    @ApiModelProperty(value = "管理费用_本季发生额(元)")
    private BigDecimal magFeeActm;

    /**
    * 财务费用_本季发生额(元)
    */
    @ApiModelProperty(value = "财务费用_本季发生额(元)")
    private BigDecimal finFeeActm;

    /**
    * 资产减值损失_本季发生额(元)
    */
    @ApiModelProperty(value = "资产减值损失_本季发生额(元)")
    private BigDecimal ipoaLossActm;

    /**
    * 信用减值损失_本季发生额(元)
    */
    @ApiModelProperty(value = "信用减值损失_本季发生额(元)")
    private BigDecimal credDecrLossActm;

    /**
    * 营业利润_本季发生额(元)
    */
    @ApiModelProperty(value = "营业利润_本季发生额(元)")
    private BigDecimal busiProfActm;

    /**
    * 投资收益_本季发生额(元)
    */
    @ApiModelProperty(value = "投资收益_本季发生额(元)")
    private BigDecimal ivsmPayfActm;

    /**
    * 营业外收入_本季发生额(元)
    */
    @ApiModelProperty(value = "营业外收入_本季发生额(元)")
    private BigDecimal noprIncmActm;

    /**
    * 营业外支出_本季发生额(元)
    */
    @ApiModelProperty(value = "营业外支出_本季发生额(元)")
    private BigDecimal noprPayActm;

    /**
    * 利润总额_本季发生额(元)
    */
    @ApiModelProperty(value = "利润总额_本季发生额(元)")
    private BigDecimal profGamtActm;

    /**
    * 所得税费用_本季发生额(元)
    */
    @ApiModelProperty(value = "所得税费用_本季发生额(元)")
    private BigDecimal inctFeeActm;

    /**
    * 净利润_本季发生额(元)
    */
    @ApiModelProperty(value = "净利润_本季发生额(元)")
    private BigDecimal netProfActm;

    /**
    * 主营业务收入_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务收入_本年累计(元)")
    private BigDecimal mainBusiIncmTyag;

    /**
    * 主营业务成本_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务成本_本年累计(元)")
    private BigDecimal mainBusiCostTyag;

    /**
    * 主营业务税金及附加_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_本年累计(元)")
    private BigDecimal mainBusiTaxAddTyag;

    /**
    * 主营业务利润_本年累计(元)
    */
    @ApiModelProperty(value = "主营业务利润_本年累计(元)")
    private BigDecimal mainBusiProfTyag;

    /**
    * 其他业务利润_本年累计(元)
    */
    @ApiModelProperty(value = "其他业务利润_本年累计(元)")
    private BigDecimal othBusiProfTyag;

    /**
    * 营业费用_本年累计(元)
    */
    @ApiModelProperty(value = "营业费用_本年累计(元)")
    private BigDecimal busiFeeTyag;

    /**
    * 管理费用_本年累计(元)
    */
    @ApiModelProperty(value = "管理费用_本年累计(元)")
    private BigDecimal magFeeTyag;

    /**
    * 财务费用_本年累计(元)
    */
    @ApiModelProperty(value = "财务费用_本年累计(元)")
    private BigDecimal finFeeTyag;

    /**
    * 资产减值损失_本年累计(元)
    */
    @ApiModelProperty(value = "资产减值损失_本年累计(元)")
    private BigDecimal ipoaLossTyag;

    /**
    * 信用减值损失_本年累计(元)
    */
    @ApiModelProperty(value = "信用减值损失_本年累计(元)")
    private BigDecimal credDecrLossTyag;

    /**
    * 营业利润_本年累计(元)
    */
    @ApiModelProperty(value = "营业利润_本年累计(元)")
    private BigDecimal busiProfTyag;

    /**
    * 投资收益_本年累计(元)
    */
    @ApiModelProperty(value = "投资收益_本年累计(元)")
    private BigDecimal ivsmPayfTyag;

    /**
    * 营业外收入_本年累计(元)
    */
    @ApiModelProperty(value = "营业外收入_本年累计(元)")
    private BigDecimal noprIncmTyag;

    /**
    * 营业外支出_本年累计(元)
    */
    @ApiModelProperty(value = "营业外支出_本年累计(元)")
    private BigDecimal noprPayTyag;

    /**
    * 利润总额_本年累计(元)
    */
    @ApiModelProperty(value = "利润总额_本年累计(元)")
    private BigDecimal profGamtTyag;

    /**
    * 所得税费用_本年累计(元)
    */
    @ApiModelProperty(value = "所得税费用_本年累计(元)")
    private BigDecimal inctFeeTyag;

    /**
    * 净利润_本年累计(元)
    */
    @ApiModelProperty(value = "净利润_本年累计(元)")
    private BigDecimal netProfTyag;

    /**
    * 主营业务收入_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务收入_去年同期(元)")
    private BigDecimal mainBusiIncmCply;

    /**
    * 主营业务成本_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务成本_去年同期(元)")
    private BigDecimal mainBusiCostCply;

    /**
    * 主营业务税金及附加_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务税金及附加_去年同期(元)")
    private BigDecimal mainBusiTaxAddCply;

    /**
    * 主营业务利润_去年同期(元)
    */
    @ApiModelProperty(value = "主营业务利润_去年同期(元)")
    private BigDecimal mainBusiProfCply;

    /**
    * 其他业务利润_去年同期(元)
    */
    @ApiModelProperty(value = "其他业务利润_去年同期(元)")
    private BigDecimal othBusiProfCply;

    /**
    * 营业费用_去年同期(元)
    */
    @ApiModelProperty(value = "营业费用_去年同期(元)")
    private BigDecimal busiFeeCply;

    /**
    * 管理费用_去年同期(元)
    */
    @ApiModelProperty(value = "管理费用_去年同期(元)")
    private BigDecimal magFeeCply;

    /**
    * 财务费用_去年同期(元)
    */
    @ApiModelProperty(value = "财务费用_去年同期(元)")
    private BigDecimal finFeeCply;

    /**
    * 资产减值损失_去年同期(元)
    */
    @ApiModelProperty(value = "资产减值损失_去年同期(元)")
    private BigDecimal ipoaLossCply;

    /**
    * 信用减值损失_去年同期(元)
    */
    @ApiModelProperty(value = "信用减值损失_去年同期(元)")
    private BigDecimal credDecrLossCply;

    /**
    * 营业利润_去年同期(元)
    */
    @ApiModelProperty(value = "营业利润_去年同期(元)")
    private BigDecimal busiProfCply;

    /**
    * 投资收益_去年同期(元)
    */
    @ApiModelProperty(value = "投资收益_去年同期(元)")
    private BigDecimal ivsmPayfCply;

    /**
    * 营业外收入_去年同期(元)
    */
    @ApiModelProperty(value = "营业外收入_去年同期(元)")
    private BigDecimal noprIncmCply;

    /**
    * 营业外支出_去年同期(元)
    */
    @ApiModelProperty(value = "营业外支出_去年同期(元)")
    private BigDecimal noprPayCply;

    /**
    * 利润总额_去年同期(元)
    */
    @ApiModelProperty(value = "利润总额_去年同期(元)")
    private BigDecimal profGamtCply;

    /**
    * 所得税费用_去年同期(元)
    */
    @ApiModelProperty(value = "所得税费用_去年同期(元)")
    private BigDecimal inctFeeCply;

    /**
    * 净利润_去年同期(元)
    */
    @ApiModelProperty(value = "净利润_去年同期(元)")
    private BigDecimal netProfCply;

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

    /**
    * 逻辑删除标识
    */
    @ApiModelProperty(value = "逻辑删除标识")
    private Integer deleted;

    /**
    * 版本号
    */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
    * 临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写
    */
    @ApiModelProperty(value = "临时数据表id 需要用来比对数据 或者 流程拒绝时全量回写")
    private Long originId;

    /**
    * data_create_time
    */
    @ApiModelProperty(value = "data_create_time")
    private LocalDateTime dataCreateTime;

    /**
    * data_create_by
    */
    @ApiModelProperty(value = "data_create_by")
    private Long dataCreateBy;

    /**
    * data_update_time
    */
    @ApiModelProperty(value = "data_update_time")
    private LocalDateTime dataUpdateTime;

    /**
    * data_update_by
    */
    @ApiModelProperty(value = "data_update_by")
    private Long dataUpdateBy;

    /**
    * 版本标志，0无效，1有效...业务自扩展
    */
    @ApiModelProperty(value = "版本标志，0无效，1有效...业务自扩展")
    private Integer versionType;

}

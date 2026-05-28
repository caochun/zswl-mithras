package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 金融协会报送-主要业务清单表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融协会报送-主要业务清单表(流程节点记录版本表)列表-返回体")
public class AssociationMainBusinessLibListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 逻辑删除，0-未删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除")
    private Integer deleted;

    /**
    * 行号
    */
    @ApiModelProperty(value = "行号")
    private Integer rowNum;

    /**
    * 报表实例编号
    */
    @ApiModelProperty(value = "报表实例编号")
    private String reportInstanceId;

    /**
    * 报表实例周期
    */
    @ApiModelProperty(value = "报表实例周期")
    private String reportInstancePeriod;

    /**
    * 批次号
    */
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    /**
    * 操作标识
    */
    @ApiModelProperty(value = "操作标识")
    private String op;

    /**
    * 上报时间
    */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportTime;

    /**
    * 写入数据库时间
    */
    @ApiModelProperty(value = "写入数据库时间")
    private LocalDateTime writeTime;

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
    * 合同名称
    */
    @ApiModelProperty(value = "合同名称")
    private String agmtName;

    /**
    * 合同编号
    */
    @ApiModelProperty(value = "合同编号")
    private String agmtNo;

    /**
    * 合同签订日期
    */
    @ApiModelProperty(value = "合同签订日期")
    private LocalDateTime agmtSignDate;

    /**
    * 协议到期日期
    */
    @ApiModelProperty(value = "协议到期日期")
    private LocalDateTime agmtMatuDate;

    /**
    * 合同类型
    */
    @ApiModelProperty(value = "合同类型")
    private String agmtTypeCode;

    /**
    * 租赁物类型
    */
    @ApiModelProperty(value = "租赁物类型")
    private String lasdType;

    /**
    * 项目行业分类
    */
    @ApiModelProperty(value = "项目行业分类")
    private String projIndtClasCode;

    /**
    * 客户姓名
    */
    @ApiModelProperty(value = "客户姓名")
    private String custName;

    /**
    * 客户证件号码
    */
    @ApiModelProperty(value = "客户证件号码")
    private String custCertNum;

    /**
    * 客户规模
    */
    @ApiModelProperty(value = "客户规模")
    private String custScalCode;

    /**
    * 融资租赁投放额
    */
    @ApiModelProperty(value = "融资租赁投放额")
    private BigDecimal fnlRels;

    /**
    * 收回本金
    */
    @ApiModelProperty(value = "收回本金")
    private BigDecimal wthdPrin;

    /**
    * 租金余额
    */
    @ApiModelProperty(value = "租金余额")
    private BigDecimal rentBal;

    /**
    * 综合融资成本
    */
    @ApiModelProperty(value = "综合融资成本")
    private BigDecimal cmphFinCost;

    /**
    * 增信情况
    */
    @ApiModelProperty(value = "增信情况")
    private String udpnSituCode;

    /**
    * 增信方
    */
    @ApiModelProperty(value = "增信方")
    private String udpn;

    /**
    * 逾期租金
    */
    @ApiModelProperty(value = "逾期租金")
    private BigDecimal ovduRent;

    /**
    * 逾期天数
    */
    @ApiModelProperty(value = "逾期天数")
    private String ovduDaysCode;

    /**
    * 逾期处置情况
    */
    @ApiModelProperty(value = "逾期处置情况")
    private String ovduDspsProg;

    /**
    * 是否纳入不良
    */
    @ApiModelProperty(value = "是否纳入不良")
    private String npFlag;

    /**
    * 不良余额
    */
    @ApiModelProperty(value = "不良余额")
    private BigDecimal npBal;

    /**
    * 客户数量
    */
    @ApiModelProperty(value = "客户数量")
    private Integer custVol;

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

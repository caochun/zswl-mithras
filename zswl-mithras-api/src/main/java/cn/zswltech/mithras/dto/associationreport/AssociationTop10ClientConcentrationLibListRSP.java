package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融协会报送-最大10家客户（含集团）集中度统计表(流程节点记录版本表)列表-返回体")
public class AssociationTop10ClientConcentrationLibListRSP {

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
    * 客户姓名
    */
    @ApiModelProperty(value = "客户姓名")
    private String custName;

    /**
    * 表内业务-前十大客户租赁余额
    */
    @ApiModelProperty(value = "表内业务-前十大客户租赁余额")
    private BigDecimal onblToptCustLeasBal;

    /**
    * 表内业务-占净资产比例
    */
    @ApiModelProperty(value = "表内业务-占净资产比例")
    private BigDecimal onblOnar;

    /**
    * 表外业务-担保
    */
    @ApiModelProperty(value = "表外业务-担保")
    private BigDecimal ofblGuar;

    /**
    * 表外业务-其他
    */
    @ApiModelProperty(value = "表外业务-其他")
    private BigDecimal ofblOth;

    /**
    * 扣减项-合格质物
    */
    @ApiModelProperty(value = "扣减项-合格质物")
    private BigDecimal deitQulfSbim;

    /**
    * 扣减项-合格保证
    */
    @ApiModelProperty(value = "扣减项-合格保证")
    private BigDecimal deitQulfAsue;

    /**
    * 扣减项-其他
    */
    @ApiModelProperty(value = "扣减项-其他")
    private BigDecimal deitOth;

    /**
    * 信用风险敞口
    */
    @ApiModelProperty(value = "信用风险敞口")
    private BigDecimal credExps;

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

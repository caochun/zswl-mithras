package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 金融协会报送-关联方信息汇总表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融协会报送-关联方信息汇总表(流程节点记录版本表)列表-返回体")
public class AssociationRelationLibListRSP {

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
    * 关联方名称
    */
    @ApiModelProperty(value = "关联方名称")
    private String relpName;

    /**
    * 是否为本公司股东关联方
    */
    @ApiModelProperty(value = "是否为本公司股东关联方")
    private String corpShahRelpFlag;

    /**
    * 本公司股东名称
    */
    @ApiModelProperty(value = "本公司股东名称")
    private String corpShahName;

    /**
    * 表内业务-关联方租赁余额_单一关联方
    */
    @ApiModelProperty(value = "表内业务-关联方租赁余额_单一关联方")
    private BigDecimal onblRelpLeasBalSrlp;

    /**
    * 表内业务-占净资产比例_单一关联方
    */
    @ApiModelProperty(value = "表内业务-占净资产比例_单一关联方")
    private BigDecimal onblOnarSrlp;

    /**
    * 表外业务-担保_单一关联方
    */
    @ApiModelProperty(value = "表外业务-担保_单一关联方")
    private BigDecimal ofblGuarSrlp;

    /**
    * 表外业务-其他_单一关联方
    */
    @ApiModelProperty(value = "表外业务-其他_单一关联方")
    private BigDecimal ofblOthSrlp;

    /**
    * 扣减项-合格质物_单一关联方
    */
    @ApiModelProperty(value = "扣减项-合格质物_单一关联方")
    private BigDecimal deitQulfSbimSrlp;

    /**
    * 扣减项-合格保证_单一关联方
    */
    @ApiModelProperty(value = "扣减项-合格保证_单一关联方")
    private BigDecimal deitQulfAsueSrlp;

    /**
    * 扣减项-其他_单一关联方
    */
    @ApiModelProperty(value = "扣减项-其他_单一关联方")
    private BigDecimal deitOthSrlp;

    /**
    * 信用风险敞口_单一关联方
    */
    @ApiModelProperty(value = "信用风险敞口_单一关联方")
    private BigDecimal credExpsSrlp;

    /**
    * 所在集团名称_关联方所在集团
    */
    @ApiModelProperty(value = "所在集团名称_关联方所在集团")
    private String grlpName;

    /**
    * 表内业务-关联方租赁余额_关联方所在集团
    */
    @ApiModelProperty(value = "表内业务-关联方租赁余额_关联方所在集团")
    private BigDecimal onblRelpLeasBalGrlp;

    /**
    * 表内业务-占净资产比例_关联方所在集团
    */
    @ApiModelProperty(value = "表内业务-占净资产比例_关联方所在集团")
    private BigDecimal onblOnarGrlp;

    /**
    * 表外业务-担保_关联方所在集团
    */
    @ApiModelProperty(value = "表外业务-担保_关联方所在集团")
    private BigDecimal ofblGuarGrlp;

    /**
    * 表外业务-其他_关联方所在集团
    */
    @ApiModelProperty(value = "表外业务-其他_关联方所在集团")
    private BigDecimal ofblOthGrlp;

    /**
    * 扣减项-合格质物_关联方所在集团
    */
    @ApiModelProperty(value = "扣减项-合格质物_关联方所在集团")
    private BigDecimal deitQulfSbimGrlp;

    /**
    * 扣减项-合格保证_关联方所在集团
    */
    @ApiModelProperty(value = "扣减项-合格保证_关联方所在集团")
    private BigDecimal deitQulfAsueGrlp;

    /**
    * 扣减项-其他_关联方所在集团
    */
    @ApiModelProperty(value = "扣减项-其他_关联方所在集团")
    private BigDecimal deitOthGrlp;

    /**
    * 信用风险敞口_关联方所在集团
    */
    @ApiModelProperty(value = "信用风险敞口_关联方所在集团")
    private BigDecimal credExpsGrlp;

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

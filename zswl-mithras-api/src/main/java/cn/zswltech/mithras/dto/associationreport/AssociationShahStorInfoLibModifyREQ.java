package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description 股东股权信息一览表-股东股权信息(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("股东股权信息一览表-股东股权信息(流程节点记录版本表)编辑-请求体")
public class AssociationShahStorInfoLibModifyREQ {

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
    * 变更前出资比例
    */
    @ApiModelProperty(value = "变更前出资比例")
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
    * 最新出资金额(万元)
    */
    @ApiModelProperty(value = "最新出资金额(万元)")
    private BigDecimal lastFndrAmt;

    /**
    * 最新持股比例
    */
    @ApiModelProperty(value = "最新持股比例")
    private BigDecimal lastHoldRati;

    /**
    * 批复文件号
    */
    @ApiModelProperty(value = "批复文件号")
    private String aprvFileNum;

    /**
    * 批复时间
    */
    @ApiModelProperty(value = "批复时间")
    private LocalDateTime aprvTime;

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

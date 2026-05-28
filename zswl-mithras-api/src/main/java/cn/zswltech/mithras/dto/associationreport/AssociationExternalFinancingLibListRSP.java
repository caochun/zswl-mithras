package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description 金融局报送-对外融资信息清单表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融局报送-对外融资信息清单表(流程节点记录版本表)列表-返回体")
public class AssociationExternalFinancingLibListRSP {

    /**
    * 自增主键
    */
    @ApiModelProperty(value = "自增主键")
    private Long id;

    /**
    * 行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）
    */
    @ApiModelProperty(value = "行号 | 同一批次数据，行号1开始进行递增（确认数据问题快速定位）")
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
    * 借款余额 | 单位：万元
    */
    @ApiModelProperty(value = "借款余额 | 单位：万元")
    private BigDecimal loanBal;

    /**
    * 融资业务类型 | 数据字典：evt00052
    */
    @ApiModelProperty(value = "融资业务类型 | 数据字典：evt00052")
    private String finBusiTypeCode;

    /**
    * 资金提供方
    */
    @ApiModelProperty(value = "资金提供方")
    private String cptlProv;

    /**
    * 融资利率
    */
    @ApiModelProperty(value = "融资利率")
    private BigDecimal finIntr;

    /**
    * 融资借款日期
    */
    @ApiModelProperty(value = "融资借款日期")
    private LocalDateTime finLoanDate;

    /**
    * 融资到期日期
    */
    @ApiModelProperty(value = "融资到期日期")
    private LocalDateTime finMatuDate;

    /**
    * 报表实例编号 | 格式：uuid
    */
    @ApiModelProperty(value = "报表实例编号 | 格式：uuid")
    private String reportInstanceId;

    /**
    * 报表实例周期 | 格式：yyyyqq
    */
    @ApiModelProperty(value = "报表实例周期 | 格式：yyyyqq")
    private String reportInstancePeriod;

    /**
    * 批次号 | 4位字符，根据月报/季报/年报的上报周期，同期数据重复上传时从0000开始递增，9999后重置
    */
    @ApiModelProperty(value = "批次号 | 4位字符，根据月报/季报/年报的上报周期，同期数据重复上传时从0000开始递增，9999后重置")
    private String batchNo;

    /**
    * 操作标识 | 值域：insert/update
    */
    @ApiModelProperty(value = "操作标识 | 值域：insert/update")
    private String op;

    /**
    * 上报时间 | 数据文件上传时间
    */
    @ApiModelProperty(value = "上报时间 | 数据文件上传时间")
    private LocalDateTime reportTime;

    /**
    * 写入时间 | 数据库写入时间
    */
    @ApiModelProperty(value = "写入时间 | 数据库写入时间")
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

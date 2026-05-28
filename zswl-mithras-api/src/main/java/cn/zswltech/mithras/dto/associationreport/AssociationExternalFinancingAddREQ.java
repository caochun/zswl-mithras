package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 金融局报送-对外融资信息清单表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("金融局报送-对外融资信息清单表新增-请求体")
public class AssociationExternalFinancingAddREQ {

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
    private Double loanBal;

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
    private Double finIntr;

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
    * 版本号 | 报表实例周期版本号
    */
    @ApiModelProperty(value = "版本号 | 报表实例周期版本号")
    private String version;

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

}

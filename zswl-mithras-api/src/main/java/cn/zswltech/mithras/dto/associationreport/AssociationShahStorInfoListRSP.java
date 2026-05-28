package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 股东股权信息一览表-股东股权信息
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("股东股权信息一览表-股东股权信息列表-返回体")
public class AssociationShahStorInfoListRSP {

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
    private Double altrBefShahFndrAmt;

    /**
    * 变更前出资比例
    */
    @ApiModelProperty(value = "变更前出资比例")
    private Double altrBefFndrRati;

    /**
    * 股权转让标志
    */
    @ApiModelProperty(value = "股权转让标志")
    private String storTranFlag;

    /**
    * 增减资金金额(万元)
    */
    @ApiModelProperty(value = "增减资金金额(万元)")
    private Double iordCptlAmt;

    /**
    * 最新出资金额(万元)
    */
    @ApiModelProperty(value = "最新出资金额(万元)")
    private Double lastFndrAmt;

    /**
    * 最新持股比例
    */
    @ApiModelProperty(value = "最新持股比例")
    private Double lastHoldRati;

    /**
    * 批复文件号
    */
    @ApiModelProperty(value = "批复文件号")
    private String aprvFileNum;

    /**
    * 批复时间
    */
    @ApiModelProperty(value = "批复时间")
    private LocalDate aprvTime;

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

package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailShahChangeInfoRSP  extends AssociationDetailBaseRSP{

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

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
     * 股东证件号码
     */
    @ApiModelProperty(value = "股东证件号码")
    private String shahCertNum;

    /**
     * 股东性质
     */
    @ApiModelProperty(value = "股东性质")
    private String shahCharCode;

    @ApiModelProperty(value = "股东性质Display")
    private String shahCharDisplay;

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
     * 变更前持股比例
     */
    @ApiModelProperty(value = "变更前持股比例")
    private BigDecimal altrBefFndrRati;

    /**
     * 股权转让标志
     */
    @ApiModelProperty(value = "股权转让标志")
    private String storTranFlag;

    @ApiModelProperty(value = "股权转让标志Display")
    private String storTranFlagDisplay;

    /**
     * 增减资金金额(万元)
     */
    @ApiModelProperty(value = "增减资金金额(万元)")
    private BigDecimal iordCptlAmt;

    /**
     * 变更后股东出资金额(万元)
     */
    @ApiModelProperty(value = "变更后股东出资金额(万元)")
    private BigDecimal altrShahFndrAmt;

    /**
     * 变更后持股比例
     */
    @ApiModelProperty(value = "变更后持股比例")
    private BigDecimal altrHoldRati;

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

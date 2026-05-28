package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * @description 股东股权信息一览表-股东变更记录
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("股东股权信息一览表-股东变更记录编辑-请求体")
public class AssociationShahChangeInfoModifyREQ {

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
    * 股东证件号码
    */
    @ApiModelProperty(value = "股东证件号码")
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
    * 变更前持股比例
    */
    @ApiModelProperty(value = "变更前持股比例")
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
    * 变更后股东出资金额(万元)
    */
    @ApiModelProperty(value = "变更后股东出资金额(万元)")
    private Double altrShahFndrAmt;

    /**
    * 变更后持股比例
    */
    @ApiModelProperty(value = "变更后持股比例")
    private Double altrHoldRati;

    /**
    * 批复文件号
    */
    @ApiModelProperty(value = "批复文件号")
    private String aprvFileNum;

    /**
    * 批复时间
    */
    @ApiModelProperty(value = "批复时间")
    private String aprvTime;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;



}

package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @description 基本情况统计表
 * @author hspcadmin
 * @date 2025-08-22
 */
@Data
@ApiModel("基本情况统计表编辑-请求体")
public class AssociationBasicSituationModifyREQ{

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
    * 法定代表人
    */
    @ApiModelProperty(value = "法定代表人")
    private String legr;

    /**
    * 成立日期
    */
    @ApiModelProperty(value = "成立日期")
    private String setpDate;

    /**
    * 批准单位
    */
    @ApiModelProperty(value = "批准单位")
    private String aprvUnit;

    /**
    * 批准文号
    */
    @ApiModelProperty(value = "批准文号")
    private String aprvFileNum;

    /**
    * 营运资金(万元)
    */
    @ApiModelProperty(value = "营运资金(万元)")
    private BigDecimal operCptl;

    /**
    * 国有资本(万元)
    */
    @ApiModelProperty(value = "国有资本(万元)")
    private BigDecimal sttoCptl;

    /**
    * 实收资本(万元)
    */
    @ApiModelProperty(value = "实收资本(万元)")
    private BigDecimal paidCptl;

    /**
    * 经济成分
    */
    @ApiModelProperty(value = "经济成分")
    @NotBlank(message = "经济成分不能为空")
    private String econClasCode;

    /**
    * 是否中央企业控股
    */
    @ApiModelProperty(value = "是否中央企业控股")
    private String ctarCorpHoldFlag;

    /**
    * 是否地方国企控股
    */
    @ApiModelProperty(value = "是否地方国企控股")
    private String lcalSoeHoldFlag;

    /**
    * 从业人员
    */
    @ApiModelProperty(value = "从业人员")
    private Integer prtiNum;

    /**
    * 注册地址
    */
    @ApiModelProperty(value = "注册地址")
    private String regAddr;

    /**
    * 实际经营地址
    */
    @ApiModelProperty(value = "实际经营地址")
    private String actlOperAddr;

    /**
    * 企业类别(内资/内资试点/外资)
    */
    @ApiModelProperty(value = "企业类别(内资/内资试点/外资)")
    private String corpClasCode;

    /**
    * 厂商系标志(厂商系/非厂商系)
    */
    @ApiModelProperty(value = "厂商系标志(厂商系/非厂商系)")
    private String mnfrFlag;

    /**
    * 上市标志(上市/非上市)
    */
    @ApiModelProperty(value = "上市标志(上市/非上市)")
    private String listFlag;

    /**
    * 分支机构数量(家)
    */
    @ApiModelProperty(value = "分支机构数量(家)")
    private Integer brchInsNum;

    /**
    * 省外分支机构数量(家)
    */
    @ApiModelProperty(value = "省外分支机构数量(家)")
    private Integer oprvBrchInsNum;

    /**
    * 省内分支机构数量(家)
    */
    @ApiModelProperty(value = "省内分支机构数量(家)")
    private Integer wprvBrchInsNum;

    /**
    * 设立的其他融资租赁子公司数量
    */
    @ApiModelProperty(value = "设立的其他融资租赁子公司数量")
    private Integer fnlChilCorpNum;

    /**
    * 设立的特殊项目公司（spv)
    */
    @ApiModelProperty(value = "设立的特殊项目公司（spv)")
    private Integer spclProjCorpSpvVol;

    /**
    * 分支机构地址
    */
    @ApiModelProperty(value = "分支机构地址")
    private String brchInsAddr;

    /**
    * 经批准的业务范围
    */
    @ApiModelProperty(value = "经批准的业务范围")
    private String hsapBusiScop;

    /**
    * 实际控制人
    */
    @ApiModelProperty(value = "实际控制人")
    private String actlCtlr;

    /**
    * 实际控制人持股比例
    */
    @ApiModelProperty(value = "实际控制人持股比例")
    private BigDecimal actlCtlrHoldRati;

    /**
    * 公司联系人
    */
    @ApiModelProperty(value = "公司联系人")
    private String corpConp;

    /**
    * 联系电话
    */
    @ApiModelProperty(value = "联系电话")
    private String contTel;

    /**
    * 联系邮箱
    */
    @ApiModelProperty(value = "联系邮箱")
    private String contMail;

    /**
    * 公司网址
    */
    @ApiModelProperty(value = "公司网址")
    private String corpWeb;

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    private String reportInstanceId;


}

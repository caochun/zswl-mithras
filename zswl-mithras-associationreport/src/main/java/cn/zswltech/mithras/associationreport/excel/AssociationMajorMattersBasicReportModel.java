package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @date 2025/4/22
 * @description 重大事项报告表-基本信息
 */
@Data
public class AssociationMajorMattersBasicReportModel extends AssociationReportBaseModel {

    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty("row_num")
    private Integer rowNum;

    /**
     * 企业统一社会信用代码
     */
    @ApiModelProperty("unif_soci_cred_code")
    private String unifSociCredCode;
    /**
     * 填报人联系方式
     */
    @ApiModelProperty(value = "填报人联系方式")
    private String inftContMode;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String corpName;

    /**
     * 法定资本(万元)
     */
    @ApiModelProperty(value = "法定资本(万元)")
    private BigDecimal leglCptl;

    /**
     * 营业地址
     */
    @ApiModelProperty(value = "营业地址")
    private String busiAddr;

    /**
     * 公司法人名称
     */
    @ApiModelProperty(value = "公司法人名称")
    private String corpLegpName;

    /**
     * 分支机构数量
     */
    @ApiModelProperty(value = "分支机构数量")
    private Integer brchInsNum;

    /**
     * 董事长姓名
     */
    @ApiModelProperty(value = "董事长姓名")
    private String chrmName;

    /**
     * 总经理姓名
     */
    @ApiModelProperty(value = "总经理姓名")
    private String gmgrName;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String contMode;

}

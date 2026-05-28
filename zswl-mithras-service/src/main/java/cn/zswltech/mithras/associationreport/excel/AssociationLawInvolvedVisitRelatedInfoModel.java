package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @date 2025/4/22
 * @description 涉法涉讼涉访信息表
 */
@Data
public class AssociationLawInvolvedVisitRelatedInfoModel extends AssociationReportBaseModel {

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
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private String onum;

    /**
     * 信息类别
     */
    @ApiModelProperty(value = "信息类别")
    private String caseClasCode;

    /**
     * 合同名称
     */
    @ApiModelProperty(value = "合同名称")
    private String agmtName;

    /**
     * 合同编号
     */
    @ApiModelProperty(value = "合同编号")
    private String agmtNo;

    /**
     * 涉及金额(元)
     */
    @ApiModelProperty(value = "涉及金额(元)")
    private BigDecimal invlAmt;

    /**
     * 是否销号
     */
    @ApiModelProperty(value = "是否销号")
    private String canbFlag;

}

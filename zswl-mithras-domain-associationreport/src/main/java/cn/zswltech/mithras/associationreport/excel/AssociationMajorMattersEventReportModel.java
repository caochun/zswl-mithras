package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @date 2025/4/22
 * @description 重大事项报告表-重大事项报告情况
 */
@Data
public class AssociationMajorMattersEventReportModel extends AssociationReportBaseModel {

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
     * 事项名称
     */
    @ApiModelProperty(value = "事项名称")
    private String piecName;

    /**
     * 重大事项说明
     */
    @ApiModelProperty(value = "重大事项说明")
    private String imprPiecExpl;

}

package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author
 * @date 2025/4/22
 * @description 金融局报送-对外融资信息清单表
 */
@Data
public class AssociationExternalFinancingModel extends AssociationReportBaseModel  {

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

    @ApiModelProperty("序号")
    private String onum;

    @ApiModelProperty("借款余额 | 单位：万元")
    private BigDecimal loanBal;

    @ApiModelProperty("融资业务类型 | 数据字典：evt00052")
    private String finBusiTypeCode;

    @ApiModelProperty("资金提供方")
    private String cptlProv;

    @ApiModelProperty("融资利率")
    private BigDecimal finIntr;

    @ApiModelProperty("融资借款日期")
    private LocalDate finLoanDate;

    @ApiModelProperty("融资到期日期")
    private LocalDate finMatuDate;


}

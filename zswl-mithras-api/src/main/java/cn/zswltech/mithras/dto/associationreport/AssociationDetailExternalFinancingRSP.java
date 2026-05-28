package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailExternalFinancingRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("序号")
    private String onum;

    @ApiModelProperty("借款余额 | 单位：万元")
    private BigDecimal loanBal;

    @ApiModelProperty("融资业务类型 | 数据字典：evt00052")
    private String finBusiTypeCode;

    @ApiModelProperty("融资业务类型展示文案")
    private String finBusiTypeCodeDisplay;

    @ApiModelProperty("资金提供方")
    private String cptlProv;

    @ApiModelProperty("融资利率")
    private BigDecimal finIntr;

    @ApiModelProperty("融资借款日期")
    private LocalDate finLoanDate;

    @ApiModelProperty("融资到期日期")
    private LocalDate finMatuDate;
}

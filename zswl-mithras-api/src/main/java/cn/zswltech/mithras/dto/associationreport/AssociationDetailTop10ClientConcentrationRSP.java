package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationDetailTop10ClientConcentrationRSP extends AssociationDetailBaseRSP {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty("序号")
    private String onum;

    @ApiModelProperty("客户姓名")
    private String custName;

    @ApiModelProperty("表内业务-前十大客户租赁余额")
    private BigDecimal onblToptCustLeasBal;

    @ApiModelProperty("表内业务-占净资产比例")
    private BigDecimal onblOnar;

    @ApiModelProperty("表外业务-担保")
    private BigDecimal ofblGuar;

    @ApiModelProperty("表外业务-其他")
    private BigDecimal ofblOth;

    @ApiModelProperty("扣减项-合格质物")
    private BigDecimal deitQulfSbim;

    @ApiModelProperty("扣减项-合格保证")
    private BigDecimal deitQulfAsue;

    @ApiModelProperty("扣减项-其他")
    private BigDecimal deitOth;

    @ApiModelProperty("信用风险敞口")
    private BigDecimal credExps;
}

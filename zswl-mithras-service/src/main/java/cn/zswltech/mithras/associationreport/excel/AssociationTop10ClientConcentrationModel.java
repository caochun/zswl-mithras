package cn.zswltech.mithras.associationreport.excel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description 金融协会报送-最大10家客户（含集团）集中度统计表实体类
 */
@Data
public class AssociationTop10ClientConcentrationModel extends AssociationReportBaseModel  {

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

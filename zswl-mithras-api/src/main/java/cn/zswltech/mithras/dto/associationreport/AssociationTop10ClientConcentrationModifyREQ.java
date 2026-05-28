package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 资产负债表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("资产负债表编辑-请求体")
public class AssociationTop10ClientConcentrationModifyREQ {

    /**
     * 自增主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 行号 | 同一批次数据从1开始递增
     */
    @ApiModelProperty(value = "行号 | 同一批次数据从1开始递增")
    private Integer rowNum;

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

    /**
     * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
     */
    @ApiModelProperty(value = "报表实例编号 uuid联合主键：(report_instance_id， row_num)")
    private String reportInstanceId;



}

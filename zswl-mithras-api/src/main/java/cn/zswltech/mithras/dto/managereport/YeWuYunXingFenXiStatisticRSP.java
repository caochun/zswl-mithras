package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/12/12
 * @description
 */
@Data
public class YeWuYunXingFenXiStatisticRSP {
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("所属月份")
    private String yearAndMonth;
    @ApiModelProperty("立项创建-数量")
    private Integer projEstablishCreateQuantity = 0;
    @ApiModelProperty("立项创建-金额")
    private Long projEstablishCreateAmount = 0L;
    @ApiModelProperty("评审创建-数量")
    private Integer projReviewCreateQuantity = 0;
    @ApiModelProperty("评审创建-金额")
    private Long projReviewCreateAmount = 0L;
    @ApiModelProperty("租赁物创建-数量")
    private Integer leaseItemCreateQuantity = 0;
    @ApiModelProperty("租赁物创建-金额")
    private Long leaseItemCreateAmount = 0L;
    @ApiModelProperty("合同创建-数量")
    private Integer contractCreateQuantity = 0;
    @ApiModelProperty("合同创建-金额")
    private Long contractCreateAmount = 0L;
    @ApiModelProperty("合同付款-数量")
    private Integer paymentCreateQuantity = 0;
    @ApiModelProperty("合同付款-金额")
    private Long paymentCreateAmount = 0L;
    @ApiModelProperty("合同投放-数量")
    private Integer paymentActualPayQuantity = 0;
    @ApiModelProperty("合同投放-金额")
    private Long paymentActualPayAmount = 0L;
}

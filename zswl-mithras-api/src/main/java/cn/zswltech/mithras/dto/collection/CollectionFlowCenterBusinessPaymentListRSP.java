package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionFlowCenterBusinessPaymentListRSP {


    private Integer paymentId;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("现金流项目 PaymentFlowItemEnum")
    private String cashFlowItem;

    private Long bizDeptId;

    @ApiModelProperty("业务部门")
    private String bizDept;

    @ApiModelProperty("应付")
    private Long paymentAmount;

    @ApiModelProperty("已付")
    private Long paidAmount;

    @ApiModelProperty("应付日期")
    private LocalDate applyPaymentDate;

    @ApiModelProperty("最新一笔付款日期，无核销不显示")
    private LocalDate paidInDate;

    @ApiModelProperty("付款申请编号")
    private String paymentCode;

    @ApiModelProperty("项目名称")
    private String projName;


}

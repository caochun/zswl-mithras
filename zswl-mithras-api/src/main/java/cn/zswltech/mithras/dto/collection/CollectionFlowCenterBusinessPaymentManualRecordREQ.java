package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CollectionFlowCenterBusinessPaymentManualRecordREQ {

    @ApiModelProperty("现金流项目")
    @NotNull(message = "付款ID不能为空")
    private Long paymentId;

    @ApiModelProperty("现金流项目 PaymentFlowItemEnum")
    @NotNull(message = "现金流项目不能为空")
    private String cashFlowItem;

    /**
     * 付款方式
     */
    @ApiModelProperty("付款方式")
    @NotNull(message = "付款方式不能为空 PaymentMethod")
    private String paymentMethod;

    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    @NotNull(message = "实付金额不能为空")
    private Long paidInAmount;

    /**
     * 实付日期
     */
    @ApiModelProperty("实付日期")
    @NotNull(message = "实付日期不能为空")
    private LocalDate paidInDate;

    @ApiModelProperty(value = "付款确认记录ID")
    private Long paymentActualDetailId;

    private BillManagementAddREQ billManagementAddREQ;

}

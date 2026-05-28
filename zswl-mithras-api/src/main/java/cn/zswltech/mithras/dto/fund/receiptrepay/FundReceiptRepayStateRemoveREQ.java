package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description fund_receipt_repay_state
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@ApiModel("fund_receipt_repay_state删除-请求体")
public class FundReceiptRepayStateRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}

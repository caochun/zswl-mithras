package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description fund_receipt_repay_state
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@ApiModel("fund_receipt_repay_state编辑-请求体")
public class FundReceiptRepayStateModifyREQ {

    /**
    * 主键
    */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
    * recepit_repay_id
    */
    @ApiModelProperty(value = "recepit_repay_id")
    private Long recepitRepayId;

    /**
    * write_off_state
    */
    @ApiModelProperty(value = "write_off_state")
    private String writeOffState;

}

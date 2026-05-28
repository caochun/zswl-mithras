package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @description fund_receipt_repay_batch
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@ApiModel("fund_receipt_repay_batch列表-返回体")
public class FundReceiptRepayBatchListRSP {

    /**
    * 主键
    */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
    * 流程id
    */
    @ApiModelProperty(value = "流程id")
    private String processState;

}

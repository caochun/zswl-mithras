package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 还款计划
 * @author zhaozhengkang
 * @date 2023-02-20
 */
@Data
@ApiModel("还款计划删除-请求体")
public class FundReceiptRepayPlanRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}

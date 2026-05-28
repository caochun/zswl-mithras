package cn.zswltech.mithras.dto.fund.receiptrepay;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 资金管理-融资管理-对方收款账户
 * @author zhaozhengkang
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-融资管理-对方收款账户删除-请求体")
public class FundReceiptAccountRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}

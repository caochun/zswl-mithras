package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 资金管理-融资管理-我方付款账户
 * @date 2023-02-22
 */
@Data
@ApiModel("资金管理-收付款管理-我方付款账户列表-请求体")
public class FundRepayAccountListREQ extends PageReq {
    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;

}

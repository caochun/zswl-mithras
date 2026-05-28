package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/2/22
 * @description
 */
@Data
@ApiModel("基础数据-我方账户-列表-请求体")
public class BaseDataBankAccountListREQ {
    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("账户余额（起）")
    private Long accountBalanceFrom;

    @ApiModelProperty("账户余额（止）")
    private Long accountBalanceTo;

    @ApiModelProperty("是否贷款账户")
    private Integer isLoan;

    @ApiModelProperty("创建时间（起） yyyy-MM-dd")
    private String createTimeFrom;

    @ApiModelProperty("创建时间（止） yyyy-MM-dd")
    private String createTimeTo;

    @ApiModelProperty("修改时间（起） yyyy-MM-dd")
    private String updateTimeFrom;

    @ApiModelProperty("修改时间（止） yyyy-MM-dd")
    private String updateTimeTo;
}

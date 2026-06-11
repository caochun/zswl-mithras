package cn.zswltech.mithras.dto.liquiditymanage.dayreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/13 16:42
 * @className AccountBalanceListRSP
 * @description
 */
@Data
@ApiModel(value = "账户余额列表响应参数")
public class AccountBalanceListRSP {

    @ApiModelProperty(value = "监管账户余额")
    private List<AccountBalanceVo> supervisionAccountBalanceList;

    @ApiModelProperty(value = "非监管账户余额")
    private List<AccountBalanceVo> nonSupervisionAccountBalanceList;

    @Data
    @ApiModel(value = "列表参数")
    public static class AccountBalanceVo {

        @ApiModelProperty(value = "银行")
        private String bankName;

        @ApiModelProperty(value = "日初余额")
        private Long accountBalance;
    }

}

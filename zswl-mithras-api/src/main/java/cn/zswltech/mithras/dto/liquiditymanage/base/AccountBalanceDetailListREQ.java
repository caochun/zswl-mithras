package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


/**
 * AccountBalanceListREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "账户余额明细列表请求参数")
public class AccountBalanceDetailListREQ {

    /**
     * 开户银行
     */
    @ApiModelProperty(value = "开户银行")
    private String accountBank;

    /**
     * 银行账号
     */
    @ApiModelProperty(value = "银行账号")
    private String accountNumber;

    /**
     * 账户性质
     */
    @ApiModelProperty(value = "账户性质")
    private String accountType;

    /**
     * 起止日期-开始
     */
    @ApiModelProperty(value = "起止日期-开始")
    @NotNull(message = "不得为空")
    private LocalDate queryDateStart;

    /**
     * 起止日期-结束
     */
    @ApiModelProperty(value = "起止日期-结束")
    @NotNull(message = "不得为空")
    private LocalDate queryDateEnd;

}

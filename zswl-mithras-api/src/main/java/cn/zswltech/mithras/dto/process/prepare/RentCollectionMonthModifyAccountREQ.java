package cn.zswltech.mithras.dto.process.prepare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/8/5
 * @description
 */
@Data
public class RentCollectionMonthModifyAccountREQ {
    @NotNull(message = "记录ID不能为空")
    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("户名")
    private String bankAccountName;

    @ApiModelProperty("账户")
    private String bankAccountNumber;

    @ApiModelProperty("开户行")
    private String bankName;
}

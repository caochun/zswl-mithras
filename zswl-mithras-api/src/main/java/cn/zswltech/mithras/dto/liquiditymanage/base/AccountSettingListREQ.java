package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AccountSettingListREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "回款账户配置请求体")
public class AccountSettingListREQ {

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资机构")
    private String organizationName;

    @ApiModelProperty(value = "是否模拟结清")
    private Boolean simulateSettle;

    @ApiModelProperty(value = "开户银行")
    private String accountBank;

    @ApiModelProperty(value = "银行账号")
    private String accountNumber;


}

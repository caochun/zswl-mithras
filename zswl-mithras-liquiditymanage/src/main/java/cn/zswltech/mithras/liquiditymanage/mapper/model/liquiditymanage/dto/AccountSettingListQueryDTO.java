package cn.zswltech.mithras.liquiditymanage.mapper.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AccountSettingListQueryDto
 *
 * @author chenyifei
 * @since 2024/12/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSettingListQueryDTO {

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

package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/6/3/09:43
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankFlowCenterListDTO {

    @ApiModelProperty(value = "核销金额")
    private Long writeOffAmount;

    @ApiModelProperty(value = "核销时间")
    private String writeOffTime;
}

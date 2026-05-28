package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/12/2 11:06
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-单个签约请求参数")
public class ContractTextManageSingleSignREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "签约方ID")
    @NotNull(message = "签约方ID不能为空")
    private Long signerId;
}

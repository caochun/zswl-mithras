package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/11/29 10:21
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-修改单签签约方式请求参数")
public class ContractTextManageUpdateSingleSigningWayREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 文本签约方式 {@link cn.zswltech.mithras.service.enums.contract.SigningWayEnum#name()}
     */
    @ApiModelProperty(value = "文本签约方式")
    @NotBlank(message = "文本签约方式不能为空")
    private String textSigningWay;

}

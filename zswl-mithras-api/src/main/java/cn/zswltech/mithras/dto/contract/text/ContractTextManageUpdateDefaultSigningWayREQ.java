package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/11/29 10:17
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-修改默认签约方式请求参数")
public class ContractTextManageUpdateDefaultSigningWayREQ {

    @ApiModelProperty(value = "主列表id")
    @NotNull(message = "主列表id不能为空")
    private Long mainId;

    /**
     * {@link cn.zswltech.mithras.contract.enums.contract.text.SigningWayEnum#name()}
     */
    @ApiModelProperty(value = "文本签约方式")
    @NotBlank(message = "文本签约方式不能为空")
    private String textSigningWay;
}

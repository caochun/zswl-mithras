package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/11/18 14:57
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-已签合同详情请求参数")
public class ContractTextManageSignedDetailREQ {

    @ApiModelProperty(value = "列表id")
    @NotNull(message = "列表id不能为空")
    private Long mainId;
}
